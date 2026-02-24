# Arquitetura — Sistema de Inventário

> Documento vivo com a visão técnica do sistema e as decisões arquiteturais relevantes.
> Atualizar sempre que uma decisão técnica significativa for tomada.

---

## Visão Geral

Sistema web para gestão de patrimônio e ativos imobilizados, construído como uma aplicação multi-tenant (SaaS-ready) com backend em Spring Boot e frontend em React.

```
┌─────────────────────────────────────────────────────────┐
│                      FRONTEND                            │
│           React 19 + TypeScript + Vite                   │
│   ┌──────────┐  ┌──────────┐  ┌───────────────────┐    │
│   │  Pages   │  │ Contexts │  │ TanStack Query     │    │
│   │ (views)  │  │  (auth)  │  │ (cache + fetching) │    │
│   └────┬─────┘  └────┬─────┘  └────────┬──────────┘    │
│        └──────────────┴────────────────┬┘               │
│                                   Axios (api.ts)         │
│                                     │ /api/*             │
└─────────────────────────────────────┼───────────────────┘
                                      │ HTTP (JWT Bearer)
┌─────────────────────────────────────┼───────────────────┐
│                      BACKEND        │                    │
│           Spring Boot 4 + Java 25   │                    │
│   ┌──────────────────┐     ┌────────▼───────────┐       │
│   │  Security Filter │────▶│   Controllers      │       │
│   │  (JWT + Tenant)  │     │  (@RestController)  │       │
│   └──────────────────┘     └────────┬───────────┘       │
│                                     │                    │
│   ┌─────────────────┐     ┌────────▼───────────┐       │
│   │  ThreadLocal     │◀───│    Services         │       │
│   │  TenantContext   │     │  (regras negócio)  │       │
│   │  UserContext     │     └────────┬───────────┘       │
│   └─────────────────┘              │                    │
│                            ┌───────▼────────────┐       │
│                            │   Repositories     │       │
│                            │  (Spring Data JPA) │       │
│                            └───────┬────────────┘       │
│                                    │                     │
└────────────────────────────────────┼─────────────────────┘
                                     │ JDBC
                              ┌──────▼──────┐
                              │  MariaDB 11 │
                              │  (Docker)   │
                              └─────────────┘
```

---

## Estilo Arquitetural

**Arquitetura em camadas com organização por feature (DDD-lite).**

Não é hexagonal — não há ports/adapters explícitos. Controllers chamam Services diretamente, Services chamam Repositories. A organização principal é por **domínio/feature**, não por camada técnica.

```
domain/
├── bem/            # Entity, Controller, Service, Repository, DTOs
├── categoria/      # Entity, Controller, Service, Repository, DTOs
├── departamento/   # Entity, Controller, Service, Repository, DTOs
├── tenant/         # Entity, Repository
└── usuario/        # Entity, Controller, Service, Repository, DTOs, Auth

infra/              # Preocupações transversais
├── config/         # CORS, Web, OpenAPI
├── excel/          # Import/Export com Apache POI
├── exception/      # Handler global de exceções
└── security/       # JWT Filter, Provider, SecurityConfig

shared/             # Base classes e utilitários
├── BaseEntity.java # @MappedSuperclass (id, createdAt, updatedAt)
├── TenantContext    # ThreadLocal para tenant corrente
├── UserContext      # ThreadLocal para usuário corrente
├── dto/            # DTOs compartilhados (PageResponse, etc.)
├── exception/      # Exceções de negócio
└── util/           # Utilitários (DepreciacaoUtil, etc.)
```

**Motivação:** para um projeto deste porte, a separação por feature é mais navegável que a separação por camada técnica. Tudo que diz respeito a "bem" está em `domain/bem/`.

---

## Modelo de Domínio

```
Tenant (1) ─────┬──── (*) Usuario        (email+tenant = unique)
                ├──── (*) Categoria       (nome+tenant = unique)
                ├──── (*) Departamento    (nome+tenant = unique)
                └──── (*) Bem             (placa+tenant = unique)
                                │
Categoria (1) ──────── (*) Bem  │
Departamento (1) ────── (*) Bem │
                                │
                        Bem (1) ──── (*) BemHistorico
```

### Entidades principais

| Entidade         | Responsabilidade           | Campos-chave                                     |
| ---------------- | -------------------------- | ------------------------------------------------ |
| **Tenant**       | Raiz do multi-tenancy      | slug, nome, plano (FREE/PRO), ativo              |
| **Usuario**      | Autenticação e autorização | email, senhaHash, perfil (ADMIN/GESTOR/USUARIO)  |
| **Categoria**    | Parâmetros de depreciação  | taxaAnual, vidaUtilAnos, revisarEmAnos           |
| **Departamento** | Organização setorial       | nome                                             |
| **Bem**          | Ativo patrimonial          | placa, valorAquisicao, estado, dataCompra, ativo |
| **BemHistorico** | Trilha de auditoria        | tipoEvento, descricao, dataEvento, usuarioNome   |

---

## Decisões Arquiteturais (ADRs)

> Formato leve: Contexto → Decisão → Consequência.

### ADR-001: Multi-tenancy por coluna discriminadora

- **Contexto:** sistema precisa isolar dados entre organizações. Opções: banco por tenant, schema por tenant, ou coluna discriminadora.
- **Decisão:** coluna `tenant_id` em todas as tabelas de negócio. Isolamento garantido no nível de query (repository).
- **Consequência:** simples de implementar e operar. Requer disciplina — todo query deve filtrar por `tenant_id`. Não usa Hibernate Filter automático; o filtro é explícito nos métodos do Repository (`findByIdAndTenantId`, JPQL com `:tenantId`).

### ADR-002: ThreadLocal para contexto de request

- **Contexto:** tenant e usuário corrente precisam estar disponíveis em qualquer camada sem passar como parâmetro em todo método.
- **Decisão:** `TenantContext` e `UserContext` usam `ThreadLocal`, populados no `JwtAuthFilter` e limpos no `finally`.
- **Consequência:** API mais limpa (services não recebem tenantId como parâmetro). Risco: esquecer de limpar o ThreadLocal causa vazamento entre requests. Mitigado pelo `finally` no filtro.

### ADR-003: Depreciação calculada on-the-fly

- **Contexto:** valor atual dos bens depende da taxa de depreciação da categoria e do tempo. Opções: armazenar valor calculado ou calcular a cada leitura.
- **Decisão:** `DepreciacaoUtil.calcular(Bem)` computa todos os valores derivados (valor atual, idade, próxima revisão, anos restantes) a cada leitura, retornando um record `Calculado`.
- **Consequência:** dados sempre corretos sem necessidade de jobs de atualização. Custo computacional insignificante para o volume esperado. Cálculo facilmente testável de forma isolada.

### ADR-004: Detecção de mudanças para histórico

- **Contexto:** o sistema precisa registrar exatamente quais campos mudaram quando um bem é editado.
- **Decisão:** `BemService.detectarCamposAlterados()` compara valores da entidade atual vs. request antes de aplicar as mutações, gerando registros em `BemHistorico` com detalhamento campo a campo.
- **Consequência:** histórico granular e legível. Nome do usuário é desnormalizado no histórico (`usuarioNome`) para preservar o registro mesmo se o usuário for alterado/excluído no futuro.

### ADR-005: Records como DTOs

- **Contexto:** DTOs precisam ser imutáveis e concisos. Java 17+ offers records.
- **Decisão:** todos os Request/Response são Java `record`s. Factory methods estáticos (`BemResponse.from(Bem)`) para conversão entity → DTO.
- **Consequência:** zero boilerplate, imutabilidade garantida, serialização JSON funciona nativamente. DTOs pequenos e específicos são declarados como inner records nos Controllers.

### ADR-006: Exclusão lógica (soft delete)

- **Contexto:** bens patrimoniais não devem ser apagados do banco — precisam de rastreabilidade.
- **Decisão:** campo `ativo` (boolean) + `dataBaixa` + `motivoBaixa`. Listagens padrão filtram `ativo = true`.
- **Consequência:** dados nunca são perdidos. Permite relatórios de bens baixados. Aumenta levemente a complexidade das queries.

### ADR-007: Flyway para migrações + validate mode

- **Contexto:** schema do banco precisa ser versionado e reproduzível.
- **Decisão:** migrações Flyway (`V1` a `V4`). Hibernate configurado com `ddl-auto=validate` (apenas valida, não altera schema).
- **Consequência:** controle total sobre o schema. Hibernate detecta inconsistências na inicialização. Sem surpresas de auto-DDL em produção.

### ADR-008: Frontend — React Query como camada de estado do servidor

- **Contexto:** necessidade de cache, refetch automático e gerenciamento de estado assíncrono no frontend.
- **Decisão:** TanStack Query (React Query) com `staleTime: 30s`, `retry: 1`. Estado de UI em React Context. Sem Redux.
- **Consequência:** cache transparente, menos requests ao backend, invalidação declarativa. Mínima complexidade de estado global.

---

## Padrões e Convenções

### Backend

| Padrão                   | Onde                    | Exemplo                                              |
| ------------------------ | ----------------------- | ---------------------------------------------------- |
| Package-by-feature       | `domain/*`              | `domain/bem/BemController.java`                      |
| BaseEntity               | Todas as entities       | `id`, `createdAt`, `updatedAt` via JPA Auditing      |
| Records para DTOs        | Request/Response        | `BemResponse`, `CategoriaRequest`                    |
| PageResponse wrapper     | Endpoints paginados     | `PageResponse<T>` encapsula `Page<T>`                |
| Global exception handler | `@RestControllerAdvice` | `NotFoundException` → 404, `BusinessException` → 422 |
| Method-level security    | Write endpoints         | `@PreAuthorize("hasAnyRole('ADMIN','GESTOR')")`      |

### Frontend

| Padrão                 | Onde                       | Exemplo                               |
| ---------------------- | -------------------------- | ------------------------------------- |
| Pages + Components     | `pages/`, `components/`    | `BensPage.tsx`, `BemDetailDrawer.tsx` |
| Centralized API client | `lib/api.ts`               | Axios com interceptors JWT            |
| Auth via Context       | `contexts/AuthContext.tsx` | `useAuth()` hook                      |
| Route guards           | `routes.tsx`               | `RequireAuth`, `RequireAdmin`         |
| Utility-first CSS      | Todos os componentes       | Tailwind CSS v4                       |

### Convenções de código

- **Nomes de classes:** PascalCase em português para domínio (`Bem`, `Categoria`), inglês para infra (`JwtProvider`, `BaseEntity`)
- **Endpoints:** `kebab-case` em português (`/api/bens`, `/api/categorias`, `/api/departamentos`)
- **Constraints de banco:** nomeadas (`uq_bem_placa_tenant`, `fk_bem_categoria`)
- **Enums:** maiúsculas (`BOM`, `MEDIO`, `RUIM`, `TROCAR`, `ADMIN`, `GESTOR`, `USUARIO`)

---

## Segurança

```
Request com JWT
     │
     ▼
┌─────────────────────────┐
│     JwtAuthFilter       │
│  1. Extrai Bearer token │
│  2. Valida assinatura   │
│  3. Popula SecurityCtx  │
│  4. Set TenantContext   │
│  5. Set UserContext     │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│ @PreAuthorize check     │
│ (method-level security) │
└────────────┬────────────┘
             │
             ▼
        Controller
```

- **Autenticação:** JWT stateless, HMAC-SHA, 24h de expiração
- **Autorização:** 3 perfis (`ADMIN` > `GESTOR` > `USUARIO`), controlados via `@PreAuthorize`
- **Sessão:** `STATELESS` — sem sessão server-side, CSRF desabilitado
- **Senhas:** BCrypt hash
- **Frontend:** token em `localStorage`, interceptor Axios, auto-logout em 401

---

## Infraestrutura Local

```yaml
# docker-compose.yml
MariaDB 11 → porta 3306, charset utf8mb4, volume persistente
```

| Componente                 | URL                                   | Porta |
| -------------------------- | ------------------------------------- | ----- |
| Frontend (Vite dev server) | http://localhost:5173                 | 5173  |
| Backend (Spring Boot)      | http://localhost:8080                 | 8080  |
| Swagger UI                 | http://localhost:8080/swagger-ui.html | 8080  |
| MariaDB                    | localhost:3306                        | 3306  |

O Vite faz proxy de `/api/*` para o backend, evitando problemas de CORS em desenvolvimento.
