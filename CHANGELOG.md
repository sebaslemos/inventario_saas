# Changelog

Todas as mudanças relevantes do projeto são documentadas aqui.

O formato segue [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e o projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

---

## [Unreleased]

> Funcionalidades em desenvolvimento que ainda não foram liberadas em uma versão.

### Em andamento (V1.0.0)

- [Autenticação](TODO#11-autenticação):
    - Proteção de rotas no frontend (redirecionamento para login por perfil)
    - Proteção de endpoints no backend (filtro de autenticação por perfil)
- [Categorias](TODO#12-cadastro-de-categorias)
    - Validações de exclusão (apenas admin, com bens vinculados)
- [Departamentos](TODO#13-cadastro-de-departamentos)
    - Validações de exclusão (apenas admin, com bens vinculados)
- [Bens](TODO#14-cadastro-e-gestão-de-bens-crud-completo)
    - Exclusão de Bens
    - Reverificar campos obrigatórios e ajustar lógica no backend para evitar nullpointers
- [Relatórios](TODO#17-relatório-exportável-para-excel)
    - Upload e importação de planilha Excel

---

## [0.1.0] — 2026-02-24

Primeira versão funcional do MVP — espelho da planilha Excel com interface web moderna.

### Adicionado

- **Autenticação** — login com e-mail/senha via JWT, logout
- **Categorias** — CRUD completo com taxa de depreciação anual e vida útil
- **Departamentos** — CRUD completo
- **Bens** — CRUD completo com todos os campos da planilha original
    - Placa/tombamento, descrição, categoria, departamento
    - Valor de aquisição, data de compra, fornecedor, número de série/NF
    - Estado de conservação (Bom / Médio / Ruim / Trocar)
    - Responsável, descrição do local, observações
- **Cálculo automático de depreciação** — linear, baseado na taxa anual da categoria
- **Dashboard** — cards com total de bens, valor patrimonial e alertas de conservação
- **Filtros combinados** — por categoria, departamento e estado de conservação
- **Paginação** — listagem de bens paginada
- **Histórico de alterações** — detecção automática de campos modificados
- **Drawer de detalhes** — visualização completa do bem com dados calculados
- **Multi-tenancy** — isolamento de dados por organização via `tenant_id`
- **Migrações Flyway** — schema versionado (V1 a V4) com dados iniciais
- **Swagger/OpenAPI** — documentação interativa da API
- **Layout responsivo** — sidebar com navegação, Tailwind CSS

### Infraestrutura

- Backend: Spring Boot 4.0.3 + Java 25
- Frontend: React 19 + TypeScript + Vite 7
- Banco: MariaDB 11 via Docker Compose
- Segurança: JWT stateless + BCrypt + Spring Security

---

<!--
Formato para novas entradas:

## [X.Y.Z] — AAAA-MM-DD

### Adicionado (para novas funcionalidades)
### Alterado (para mudanças em funcionalidades existentes)
### Corrigido (para correções de bugs)
### Removido (para funcionalidades removidas)
### Segurança (para correções de vulnerabilidades)
### Infraestrutura (para mudanças em build, deploy, dependências)
-->
