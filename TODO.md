# TODO — Plano de Implementação do Sistema de Inventário

---

## Fase 1 — MVP (Espelho da Planilha)

### 1.1 Autenticação

- [x] Login com e-mail e senha (JWT)
- [x] Logout (invalidação de token)
- [ ] Login através de código único enviado por email
- [ ] Proteção de rotas no frontend (redirecionamento para login)
    - ADMIN: todas as operações na tenant
    - GESTOR: CRUD de departamentos, categorias e bens
    - USUARIO: Apenas leitura de dados e exportação de relatório
- [ ] Proteção de endpoints no backend (filtro de autenticação)
- [ ] Refresh token / renovação de sessão

### 1.2 Cadastro de Categorias

- [x] CRUD de categorias (criar, listar, editar, excluir)
- [x] Campo de taxa de depreciação (%) por categoria
- [ ] Validação: impedir exclusão de categoria com bens vinculados (sugerir movimentação antes)
- [ ] Validação: cadastro de item com mesmo nome de um já excluído (confirmar com usuário a reativação)
- [ ] Paginação e busca por nome na listagem

### 1.3 Cadastro de Departamentos

- [x] CRUD de departamentos (criar, listar, editar, excluir)
- [ ] Validação: cadastro de departamento com mesmo nome de um já excluído (confirmar com usuário a reativação)
- [ ] Validação: impedir exclusão de departamento com bens vinculados (sugerir movimentação antes)
- [ ] Paginação e busca por nome na listagem

### 1.4 Cadastro e Gestão de Bens (CRUD Completo)

- [x] Criar bem com todos os campos da planilha
    - [x] Código / Tombamento
    - [x] Descrição
    - [x] Categoria
    - [x] Departamento
    - [x] Data de aquisição
    - [x] Valor de aquisição
    - [x] Valor atual (calculado pela depreciação)
    - [x] Estado de conservação (Bom / Regular / Trocar)
    - [x] Observações
- [ ] Validação: cadastro de bem com mesma placa de um já excluído (confirmar com usuário a reativação)
- [ ] Reverificar campos obrigatórios e ajustar lógica no backend para evitar nullpointers
- [x] Listar bens com paginação
- [x] Editar bem existente
- [ ] Excluir bem (exclusão lógica / soft delete)
- [x] Visualizar detalhes de um bem (drawer/modal)
- [x] Cálculo automático de depreciação com base na categoria
- [ ] Permitir a edição de campos calculados, mas sugerindo no cadastro o valor

### 1.5 Dashboard Inicial

- [x] Card: total de bens cadastrados
- [x] Card: valor patrimonial total (soma dos valores atuais)
- [x] Card: bens em estado "Trocar e Ruim"
- [ ] Card / Lista: próximas revisões programadas
- [ ] Gráfico: distribuição de bens por categoria
- [ ] Gráfico: distribuição de bens por departamento
- [ ] Link dos cards para a página de bens com o filtro adequado

### 1.6 Listagem com Filtros

- [x] Filtro por categoria
- [x] Filtro por departamento
- [x] Filtro por estado de conservação
- [ ] Filtro por faixa de valor
- [ ] Filtro por período de aquisição
- [x] Combinação de múltiplos filtros simultâneos
- [ ] Limpeza de filtros (botão "Limpar")

### 1.7 Relatório Exportável para Excel

- [ ] Gerar relatório no formato Excel (.xlsx)
- [ ] Incluir todos os campos do bem no relatório
- [ ] Permitir exportar resultado filtrado
- [ ] Layout similar à aba "Relatório" da planilha original
- [ ] Botão de download acessível na tela de listagem

### 1.8 Importação da Planilha Excel Existente

- [ ] Upload de arquivo Excel (.xlsx / .xlsm)
- [ ] Mapeamento das colunas da planilha para campos do sistema
- [ ] Validação de dados antes da importação (linhas inválidas)
- [ ] Resumo pré-importação (quantos registros serão criados)
- [ ] Execução da importação em lote
- [ ] Relatório pós-importação (sucesso / erros por linha)

### 1.9 Cadastro/Edição de Usuários

- [ ] Crud de usuários para Admin
    - [ ] Permitir visualização de inativados
- [ ] Troca de senha pelo próprio usuário
- [ ] Recuperação de senha

---

## Fase 2 — Qualidade & Rastreabilidade

### 2.1 Histórico de Movimentações

- [ ] Registrar transferência de bem entre departamentos
- [ ] Formulário: departamento de origem, destino, data, motivo
- [ ] Listagem de movimentações por bem (timeline)
- [ ] Relatório de movimentações com filtro por período

### 2.2 Registro de Baixa de Bem

- [ ] Formulário de baixa (motivo: descarte, venda, doação, extravio)
- [ ] Campos: data da baixa, valor residual, observações
- [ ] Bem de baixa não aparece nas listagens padrão
- [ ] Relatório de bens baixados com filtro por período e motivo

### 2.3 Histórico de Revisões / Manutenções

- [ ] Registrar revisão ou manutenção realizada em um bem
- [ ] Campos: data, tipo (preventiva / corretiva), descrição, custo
- [ ] Programar próxima revisão (data prevista)
- [ ] Listagem de revisões por bem (timeline)
- [ ] Indicador visual de revisão vencida / próxima

### 2.4 Upload de Fotos e Documentos

- [ ] Upload de foto do bem (imagem principal)
- [ ] Upload de documentos (NF, laudos, garantias) em PDF/imagem
- [ ] Visualização de documentos anexados na tela de detalhes do bem
- [ ] Limite de tamanho por arquivo e por bem
- [ ] Armazenamento em serviço de storage (S3 / MinIO)

### 2.5 QR Code para Placa do Bem

- [ ] Geração automática de QR Code ao cadastrar bem
- [ ] QR Code contém URL para a página de detalhes do bem
- [ ] Visualização do QR Code na tela de detalhes
- [ ] Download do QR Code em alta resolução (para impressão)
- [ ] Impressão em lote de etiquetas com QR Code

### 2.6 Alertas por E-mail

- [ ] Configuração de serviço de envio de e-mail (SMTP / SendGrid)
- [ ] Alerta: bens com revisão próxima (X dias antes)
- [ ] Alerta: bens em estado "Trocar" (resumo periódico)
- [ ] Template de e-mail com formatação profissional
- [ ] Configuração de frequência de envio (diário / semanal)
- [ ] Opção do usuário para ativar/desativar alertas

---

## Fase 3 — SaaS & Multi-Tenancy

### 3.1 Portal de Gestão de Tenants (Super Admin)

- [ ] Tela de listagem de todos os tenants (organizações)
- [ ] Criar novo tenant manualmente
- [ ] Ativar / desativar tenant
- [ ] Visualizar métricas por tenant (qtd. bens, usuários, storage)
- [ ] Gerenciar plano de assinatura do tenant

### 3.2 Autoregistro de Organização (Onboarding)

- [ ] Página pública de cadastro de nova organização
- [ ] Formulário: nome da organização, CNPJ, dados do admin
- [ ] Criação automática do tenant + usuário admin
- [ ] E-mail de boas-vindas com instruções iniciais
- [ ] Wizard de configuração inicial (categorias, departamentos)

### 3.3 Planos de Assinatura (Free / Pro)

- [ ] Definir limites do plano Free (qtd. bens, usuários, storage)
- [ ] Definir benefícios do plano Pro
- [ ] Tela de upgrade de plano
- [ ] Integração com gateway de pagamento (Stripe / Asaas)
- [ ] Controle de limites e bloqueio ao atingir cota
- [ ] Tela de faturamento e histórico de pagamentos

### 3.4 Gestão de Usuários por Organização

- [ ] Convidar usuário por e-mail
- [ ] Definir papel do usuário (Admin / Operador / Visualizador)
- [ ] Listagem de usuários da organização
- [ ] Ativar / desativar usuário
- [ ] Remover usuário da organização
- [ ] Auditoria de ações por usuário

---

## Registro de Bugs

> Registre aqui os bugs encontrados durante o desenvolvimento e uso do sistema.

| #   | Descrição | Severidade | Status | Data Abertura | Data Correção | Versão |
| --- | --------- | ---------- | ------ | ------------- | ------------- | ------ |
| —   | —         | —          | —      | —             | —             | —      |

**Severidades:** `Crítico` · `Alto` · `Médio` · `Baixo`
**Status:** `Aberto` · `Em andamento` · `Corrigido` · `Não reproduzível`

---

## Controle de Versões (Releases)

### v1.0 — MVP (Fase 1)

**Escopo:** Espelho funcional da planilha Excel com interface web moderna.

| Funcionalidade                              | Status      |
| ------------------------------------------- | ----------- |
| Autenticação (login / logout / troca senha) | ⬜ Pendente |
| Cadastro de Categorias                      | ⬜ Pendente |
| Cadastro de Departamentos                   | ⬜ Pendente |
| Cadastro e Gestão de Bens (CRUD)            | ⬜ Pendente |
| Dashboard inicial                           | ⬜ Pendente |
| Listagem com filtros                        | ⬜ Pendente |
| Relatório exportável para Excel             | ⬜ Pendente |
| Importação da planilha existente            | ⬜ Pendente |

---

### v2.0 — Qualidade & Rastreabilidade (Fase 2)

**Escopo:** Recursos avançados de controle patrimonial e rastreabilidade.

| Funcionalidade                      | Status      |
| ----------------------------------- | ----------- |
| Histórico de movimentações          | ⬜ Pendente |
| Registro de baixa de bem            | ⬜ Pendente |
| Histórico de revisões / manutenções | ⬜ Pendente |
| Upload de fotos e documentos        | ⬜ Pendente |
| QR Code para placa do bem           | ⬜ Pendente |
| Alertas por e-mail                  | ⬜ Pendente |

---

### v3.0 — SaaS & Multi-Tenancy (Fase 3)

**Escopo:** Transformação em plataforma SaaS multi-tenant.

| Funcionalidade                            | Status      |
| ----------------------------------------- | ----------- |
| Portal de gestão de tenants (super admin) | ⬜ Pendente |
| Autoregistro de organização (onboarding)  | ⬜ Pendente |
| Planos de assinatura (Free / Pro)         | ⬜ Pendente |
| Gestão de usuários por organização        | ⬜ Pendente |

---

> **Legenda de status:**
> ⬜ Pendente · 🔧 Em desenvolvimento · ✅ Concluído · 🚫 Cancelado
