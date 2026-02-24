# 📦 Inventário — Sistema de Gestão Patrimonial

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white" alt="Java 25" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.0.3" />
  <img src="https://img.shields.io/badge/React-19.2-61DAFB?logo=react&logoColor=black" alt="React 19.2" />
  <img src="https://img.shields.io/badge/TypeScript-5.9-3178C6?logo=typescript&logoColor=white" alt="TypeScript 5.9" />
  <img src="https://img.shields.io/badge/Vite-7.3-646CFF?logo=vite&logoColor=white" alt="Vite 7.3" />
  <img src="https://img.shields.io/badge/MariaDB-11-003545?logo=mariadb&logoColor=white" alt="MariaDB 11" />
  <img src="https://img.shields.io/badge/Tailwind%20CSS-4.2-06B6D4?logo=tailwindcss&logoColor=white" alt="Tailwind CSS 4" />
  <img src="https://img.shields.io/badge/License-AGPL--3.0-blue" alt="AGPL-3.0 License" />
</p>

Sistema web completo para **controle de patrimônio e ativos imobilizados**, construído com arquitetura multi-tenant (SaaS-ready). Desenvolvido originalmente para o **Aeroclube da Paraíba**, o sistema substitui planilhas Excel por uma interface moderna com cálculo automático de depreciação, histórico de movimentações e relatórios exportáveis.

---

## 🎯 Visão Geral

O sistema permite que organizações cadastrem, acompanhem e gerenciem seus bens patrimoniais com:

- **Cadastro completo de bens** — placa/tombamento, descrição, categoria, departamento, valor de aquisição, estado de conservação e mais
- **Cálculo automático de depreciação** — baseado na taxa anual configurada por categoria
- **Dashboard analítico** — totalizadores de valor patrimonial, quantidade de bens e alertas de conservação
- **Filtros combinados** — por categoria, departamento e estado de conservação
- **Controle de acesso** — autenticação via JWT com perfis (Admin, Gestor, Usuário)
- **Multi-tenancy** — isolamento de dados por organização via coluna `tenant_id`
- **Histórico de alterações** — rastreabilidade de todas as mudanças nos bens

---

## 🛠️ Stack Tecnológica

### Backend

| Tecnologia        | Versão | Finalidade                        |
| ----------------- | ------ | --------------------------------- |
| Java              | 25     | Linguagem principal               |
| Spring Boot       | 4.0.3  | Framework web e DI                |
| Spring Security   | 7.x    | Autenticação e autorização        |
| Spring Data JPA   | 4.x    | Persistência / ORM                |
| Hibernate         | 7.2    | Implementação JPA                 |
| Flyway            | 11.14  | Versionamento de schema           |
| MariaDB           | 11     | Banco de dados relacional         |
| jjwt              | 0.12.6 | Geração e validação de tokens JWT |
| MapStruct         | 1.6.3  | Mapeamento DTO ↔ Entity           |
| Lombok            | —      | Redução de boilerplate            |
| Apache POI        | 5.3    | Importação/exportação Excel       |
| SpringDoc OpenAPI | 2.8.4  | Documentação da API (Swagger)     |

### Frontend

| Tecnologia     | Versão | Finalidade                                  |
| -------------- | ------ | ------------------------------------------- |
| React          | 19.2   | Biblioteca de UI                            |
| TypeScript     | 5.9    | Tipagem estática                            |
| Vite           | 7.3    | Build tool e dev server                     |
| Tailwind CSS   | 4.2    | Estilização utility-first                   |
| React Router   | 7.13   | Roteamento SPA                              |
| TanStack Query | 5.x    | Cache e gerenciamento de estado server-side |
| Axios          | 1.13   | Client HTTP                                 |
| Lucide React   | —      | Ícones                                      |

### Infraestrutura

| Tecnologia     | Finalidade                                      |
| -------------- | ----------------------------------------------- |
| Docker Compose | Orquestração de ambiente local (MariaDB)        |
| Maven          | Build e gerenciamento de dependências (backend) |
| npm            | Gerenciamento de dependências (frontend)        |

---

## 📁 Estrutura do Projeto

```
inventario/
├── docker-compose.yml              # MariaDB 11 para desenvolvimento
├── pom.xml                         # Configuração Maven (backend)
├── mvnw / mvnw.cmd                 # Maven Wrapper
├── TODO.md                         # Roadmap detalhado do projeto
│
├── frontend/                       # Aplicação React (SPA)
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── src/
│       ├── main.tsx                # Ponto de entrada
│       ├── App.tsx                 # Componente raiz
│       ├── routes.tsx              # Definição de rotas
│       ├── index.css               # Estilos globais (Tailwind v4)
│       ├── components/
│       │   ├── AppLayout.tsx       # Layout com sidebar e header
│       │   └── BemDetailDrawer.tsx # Drawer de detalhes do bem
│       ├── contexts/
│       │   └── AuthContext.tsx     # Contexto de autenticação
│       ├── lib/
│       │   ├── api.ts             # Instância Axios com interceptors
│       │   ├── types.ts           # Tipos TypeScript
│       │   └── format.ts          # Formatadores (moeda, data)
│       └── pages/
│           ├── LoginPage.tsx
│           ├── DashboardPage.tsx
│           ├── BensPage.tsx
│           ├── BemFormPage.tsx
│           ├── CategoriasPage.tsx
│           ├── DepartamentosPage.tsx
│           └── UsuariosPage.tsx
│
└── src/main/                       # Aplicação Spring Boot
    ├── java/br/com/sbsistemas/inventario/
    │   ├── InventarioApplication.java
    │   ├── domain/                 # Regras de negócio por domínio
    │   │   ├── bem/               # Bens: Entity, Controller, Service, Repository, DTOs
    │   │   ├── categoria/         # Categorias: CRUD completo
    │   │   ├── departamento/      # Departamentos: CRUD completo
    │   │   ├── tenant/            # Tenant: Entity e Repository
    │   │   └── usuario/           # Usuários: Entity, Controller, Service, Auth
    │   ├── infra/
    │   │   ├── config/            # Configurações (CORS, Web, etc.)
    │   │   ├── excel/             # Serviço de importação/exportação Excel
    │   │   ├── exception/         # Handler global de exceções
    │   │   └── security/          # JWT Filter, Provider, SecurityConfig
    │   └── shared/
    │       ├── BaseEntity.java    # Entidade base com campos de auditoria
    │       ├── TenantContext.java  # ThreadLocal para tenant corrente
    │       ├── UserContext.java   # ThreadLocal para usuário corrente
    │       ├── dto/               # DTOs compartilhados
    │       ├── exception/         # Exceções de negócio
    │       └── util/              # Utilitários
    └── resources/
        ├── application.properties  # Configurações da aplicação
        └── db/migration/           # Migrações Flyway
            ├── V1__init_schema.sql
            ├── V2__seed_inicial.sql
            ├── V3__add_usuario_nome_historico.sql
            └── V4__seed_bens_planilha.sql
```

---

## 🗄️ Modelo de Dados

```
┌─────────┐       ┌──────────┐       ┌──────────────┐
│  tenant  │──┐   │ usuario  │       │   categoria   │
│──────────│  │   │──────────│       │──────────────│
│ id       │  ├──▶│ tenant_id│       │ id           │
│ slug     │  │   │ email    │   ┌──▶│ tenant_id    │
│ nome     │  │   │ perfil   │   │   │ nome         │
│ plano    │  │   └──────────┘   │   │ taxa_anual   │
└─────────┘  │                   │   │ vida_util    │
             │   ┌───────────┐   │   └──────────────┘
             │   │    bem    │   │
             │   │───────────│   │   ┌──────────────┐
             ├──▶│ tenant_id │   │   │ departamento │
             │   │ placa     │───┘   │──────────────│
             │   │ descricao │       │ id           │
             │   │ categoria │──────▶│ tenant_id    │
             │   │ depart.   │       │ nome         │
             │   │ valor_aq. │       └──────────────┘
             │   │ estado    │
             │   └─────┬─────┘
             │         │
             │   ┌─────▼──────────┐
             └──▶│ bem_historico  │
                 │────────────────│
                 │ tipo           │
                 │ descricao      │
                 │ data_evento    │
                 └────────────────┘
```

**Principais tabelas:**

- **`tenant`** — organização/empresa (multi-tenancy por coluna)
- **`usuario`** — usuários com perfil (Admin / Gestor / Usuário)
- **`categoria`** — categorias de bens com taxa de depreciação anual e vida útil
- **`departamento`** — setores da organização
- **`bem`** — ativos imobilizados com todos os dados patrimoniais
- **`bem_historico`** — registro de alterações, transferências e baixas

---

## 🚀 Como Executar

### Pré-requisitos

- **Java 25** (ou superior)
- **Node.js 20+** (recomendado 24 LTS)
- **Docker** e **Docker Compose**
- **Maven 3.9+** (ou use o Maven Wrapper incluso)

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/inventario.git
cd inventario
```

### 2. Inicie o banco de dados

```bash
docker compose up -d
```

Isso sobe um container MariaDB 11 na porta `3306` com o banco `inventario` pré-configurado.

### 3. Execute o backend

```bash
./mvnw spring-boot:run
```

O backend sobe na porta **8080**. As migrações Flyway criam o schema e populam dados iniciais automaticamente.

### 4. Execute o frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend sobe na porta **5173** com proxy automático para o backend (`/api → localhost:8080`).

### 5. Acesse o sistema

| URL                                   | Descrição                     |
| ------------------------------------- | ----------------------------- |
| http://localhost:5173                 | Aplicação (frontend)          |
| http://localhost:8080/swagger-ui.html | Documentação da API (Swagger) |

**Credenciais padrão:**

| E-mail                 | Senha    | Perfil |
| ---------------------- | -------- | ------ |
| admin@aeroclube-pb.org | admin123 | ADMIN  |

---

## 📡 Endpoints da API

A API REST segue o padrão `/api/{recurso}` com autenticação via Bearer Token (JWT).

### Autenticação

| Método | Endpoint          | Descrição                  |
| ------ | ----------------- | -------------------------- |
| `POST` | `/api/auth/login` | Autenticação (retorna JWT) |

### Bens

| Método | Endpoint                   | Descrição                           |
| ------ | -------------------------- | ----------------------------------- |
| `GET`  | `/api/bens`                | Listar bens (paginado, com filtros) |
| `GET`  | `/api/bens/{id}`           | Detalhes de um bem                  |
| `POST` | `/api/bens`                | Cadastrar novo bem                  |
| `PUT`  | `/api/bens/{id}`           | Atualizar bem                       |
| `GET`  | `/api/bens/{id}/historico` | Histórico de alterações             |
| `GET`  | `/api/dashboard/resumo`    | Resumo para dashboard               |

### Categorias

| Método   | Endpoint               | Descrição           |
| -------- | ---------------------- | ------------------- |
| `GET`    | `/api/categorias`      | Listar categorias   |
| `POST`   | `/api/categorias`      | Criar categoria     |
| `PUT`    | `/api/categorias/{id}` | Atualizar categoria |
| `DELETE` | `/api/categorias/{id}` | Remover categoria   |

### Departamentos

| Método   | Endpoint                  | Descrição              |
| -------- | ------------------------- | ---------------------- |
| `GET`    | `/api/departamentos`      | Listar departamentos   |
| `POST`   | `/api/departamentos`      | Criar departamento     |
| `PUT`    | `/api/departamentos/{id}` | Atualizar departamento |
| `DELETE` | `/api/departamentos/{id}` | Remover departamento   |

### Usuários

| Método | Endpoint             | Descrição         |
| ------ | -------------------- | ----------------- |
| `GET`  | `/api/usuarios`      | Listar usuários   |
| `POST` | `/api/usuarios`      | Criar usuário     |
| `PUT`  | `/api/usuarios/{id}` | Atualizar usuário |

> 📖 Documentação interativa completa disponível em `/swagger-ui.html`

---

## 🔐 Autenticação e Segurança

- **JWT (JSON Web Token)** com expiração configurável (padrão: 24h)
- **BCrypt** para hash de senhas
- **Filtro de autenticação** aplicado a todos os endpoints `/api/**` (exceto `/api/auth/**`)
- **Controle de acesso** por perfil: `ADMIN`, `GESTOR`, `USUARIO`
- **Multi-tenancy por coluna** — cada registro é isolado pelo `tenant_id` do usuário autenticado
- **ThreadLocal** (`TenantContext` / `UserContext`) para propagação do contexto de segurança

---

## 📊 Funcionalidades Implementadas

### ✅ MVP (v1.0)

- [x] Autenticação com JWT (login / logout)
- [x] CRUD completo de **Categorias** (com taxa de depreciação)
- [x] CRUD completo de **Departamentos**
- [x] CRUD completo de **Bens** (todos os campos da planilha)
- [x] Cálculo automático de **depreciação** por categoria
- [x] **Dashboard** com totalizadores (total de bens, valor patrimonial, alertas)
- [x] Listagem com **filtros combinados** (categoria, departamento, estado)
- [x] **Paginação** na listagem de bens
- [x] **Histórico de alterações** com detecção automática de campos modificados
- [x] **Drawer de detalhes** com informações completas do bem
- [x] Layout responsivo com **sidebar** e navegação intuitiva
- [x] Documentação da API com **Swagger/OpenAPI**
- [x] Migrações de banco com **Flyway** (schema + dados iniciais)

### 🔜 Roadmap

<details>
<summary><strong>v2.0 — Qualidade & Rastreabilidade</strong></summary>

- [ ] Histórico de movimentações (transferências entre departamentos)
- [ ] Registro de baixa de bem (descarte, venda, doação, extravio)
- [ ] Histórico de revisões e manutenções (preventiva / corretiva)
- [ ] Upload de fotos e documentos (NF, laudos, garantias)
- [ ] Geração de QR Code para placa do bem
- [ ] Alertas por e-mail (revisão próxima, bens em estado "Trocar")
- [ ] Relatório exportável para Excel (.xlsx)
- [ ] Importação de planilha Excel existente

</details>

<details>
<summary><strong>v3.0 — SaaS & Multi-Tenancy</strong></summary>

- [ ] Portal de gestão de tenants (super admin)
- [ ] Autoregistro de organização (onboarding)
- [ ] Planos de assinatura (Free / Pro) com gateway de pagamento
- [ ] Gestão de usuários por organização (convite, perfis, auditoria)

</details>

> Roadmap detalhado disponível em [TODO.md](TODO.md)

---

## ⚙️ Configuração

As principais propriedades ficam em `src/main/resources/application.properties`:

| Propriedade                | Descrição                         | Padrão                                     |
| -------------------------- | --------------------------------- | ------------------------------------------ |
| `spring.datasource.url`    | URL do banco MariaDB              | `jdbc:mariadb://localhost:3306/inventario` |
| `app.jwt.secret`           | Chave secreta para assinatura JWT | (alterar em produção)                      |
| `app.jwt.expiration-ms`    | Tempo de expiração do token (ms)  | `86400000` (24h)                           |
| `app.cors.allowed-origins` | Origens permitidas para CORS      | `http://localhost:5173`                    |
| `spring.flyway.enabled`    | Habilitar migrações automáticas   | `true`                                     |

---

## 🧪 Testes

```bash
# Backend
./mvnw test

# Frontend
cd frontend
npm run lint
npm run build
```

---

## 🤝 Contribuição

1. Faça um **fork** do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/minha-feature`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona minha feature'`)
4. Push para a branch (`git push origin feature/minha-feature`)
5. Abra um **Pull Request**

---

## 📄 Licença

Este projeto está sob a licença **GNU AGPL-3.0**. Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👤 Autor

Desenvolvido por **Sebastião Lemos** — [GitHub](https://github.com/sebaslemos)

---

> ⭐ Se este projeto foi útil, considere dar uma estrela no repositório!
