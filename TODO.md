# Backlog — Sistema de Inventário

> Backlog priorizado do projeto. Itens no topo de cada fase têm maior prioridade.
> Cada item deve ter critérios de aceite antes de ser iniciado.
> Veja [docs/GUIA-DE-PROCESSO.md](docs/GUIA-DE-PROCESSO.md) para o fluxo de trabalho.

---

## Fase 1 — MVP (Espelho da Planilha)

### 1.1 Autenticação

- [x] Login com e-mail e senha (JWT)
- [x] Logout (invalidação de token)
- [ ] 🔴 Proteção de rotas no frontend (redirecionamento para login)
    - Aceite:
        - ADMIN: acessa todas as telas e operações
        - GESTOR: acessa CRUD de departamentos, categorias e bens (sem gestão de usuários)
        - USUARIO: apenas visualização de dados, sem botões de criar/editar/excluir
        - Rota protegida redireciona para /login se não autenticado
        - Rota não autorizada exibe mensagem de permissão negada
- [ ] 🔴 Proteção de endpoints no backend (filtro de autenticação)
    - Aceite:
        - Endpoints de escrita retornam 403 para perfil sem permissão
        - Endpoints de leitura acessíveis por todos os perfis autenticados
        - Endpoint de gestão de usuários restrito a ADMIN
        - Testes manuais cobrindo os 3 perfis
- [ ] 🟡 Refresh token / renovação de sessão
    - Aceite:
        - Token renovado automaticamente quando faltam menos de 30min para expirar
        - Renovação transparente para o usuário (sem redirecionamento para login)
        - Token expirado redireciona para login com mensagem
- [ ] 🟢 Login através de código único enviado por email

### 1.2 Cadastro de Categorias

- [x] CRUD de categorias (criar, listar, editar, excluir)
- [x] Campo de taxa de depreciação (%) por categoria
- [ ] 🔴 Validação: Apenas admin pode realizar exclusão
    - Aceite:
        - Validação no backend para impedir acesso a rota de exclusão
        - No frontend, não exibir ícones de deleção
- [ ] 🔴 Validação: impedir exclusão de categoria com bens vinculados (sugerir movimentação antes)
    - Aceite:
        - Ao tentar excluir categoria com bens, exibe modal com lista dos bens vinculados
        - Modal sugere mover os bens para outra categoria antes de excluir
        - Exclusão só é permitida quando não há bens vinculados
        - Bens dado BAIXA não impedem exclusão
- [ ] 🟡 Validação: cadastro de item com mesmo nome de um já excluído (confirmar com usuário a reativação)
    - Aceite:
        - Ao cadastrar nome duplicado de item inativo, exibe diálogo perguntando se deseja reativar
        - Se confirmar, reativa o item existente (mantendo histórico)
        - Se recusar, exibe mensagem de erro de nome duplicado
- [ ] 🟡 Paginação e busca por nome na listagem
    - Aceite:
        - Listagem paginada com 10 itens por página
        - Campo de busca com debounce (300ms) filtrando por nome
        - Indicador de total de resultados

### 1.3 Cadastro de Departamentos

- [x] CRUD de departamentos (criar, listar, editar, excluir)
- [ ] 🟡 Validação: cadastro de departamento com mesmo nome de um já excluído (confirmar com usuário a reativação)
    - Aceite:
        - Mesmo comportamento das categorias (diálogo de reativação)
- [ ] 🔴 Validação: Apenas admin pode realizar exclusão
    - Aceite:
        - Validação no backend para impedir acesso a rota de exclusão
        - No frontend, não exibir ícones de deleção
- [ ] 🔴 Validação: impedir exclusão de departamento com bens vinculados (sugerir movimentação antes)
    - Aceite:
        - Mesmo comportamento das categorias (modal com bens vinculados)
- [ ] 🟡 Paginação e busca por nome na listagem
    - Aceite:
        - Mesmo comportamento das categorias (paginação + busca)

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
- [ ] 🟡 Validação: cadastro de bem com mesma placa de um já excluído
    - Aceite:
        - Ao cadastrar placa duplicada de bem inativo, exibe diálogo sobre item estar BAIXADO
        - Informar também sobre solicitar ao ADMIN a recuperação do item dado BAIXA
- [ ] 🔴 Reverificar campos obrigatórios e ajustar lógica no backend para evitar nullpointers
    - Aceite:
        - Campos obrigatórios validados com @NotNull/@NotBlank nas requests
        - Frontend marca campos obrigatórios visualmente
        - Mensagens de validação claras retornadas pela API
        - Ajustar no banco constraints de colunas not null (ou definir valores default)
- [x] Listar bens com paginação
- [x] Editar bem existente
- [ ] 🔴 Excluir bem (exclusão lógica / soft delete)
    - Aceite:
        - Botão de excluir com diálogo de confirmação
        - Exclusão marca ativo=false (não remove do banco)
        - Bem excluído não aparece nas listagens padrão
        - Registro no histórico (BemHistorico) com tipo BAIXA
        - Apenas ADMIN
- [x] Visualizar detalhes de um bem (drawer/modal)
- [x] Cálculo automático de depreciação com base na categoria
- [ ] 🟡 Gerenciamento de intens BAIXADOS
    - Aceite
        - Apenas para ADMIN
        - Ver, editar e reativar itens baixados
        - Caso pertença a uma categoria excluída, informar necessidade de movimentação para categoria válida
- [ ] 🟢 Permitir a edição de campos calculados, mas sugerindo no cadastro o valor
    - Aceite:
        - Campos calculados (valor atual) preenchidos automaticamente mas editáveis
        - Indicação visual de que o valor foi calculado vs. editado manualmente

### 1.5 Dashboard Inicial

- [x] Card: total de bens cadastrados
- [x] Card: valor patrimonial total (soma dos valores atuais)
- [x] Card: bens em estado "Trocar e Ruim"
- [ ] 🟡 Card / Lista: próximas revisões programadas
    - Aceite:
        - Card mostrando contagem de bens com revisão próxima (30 dias)
        - Ao clicar, lista os bens com data de revisão ordenados por proximidade
- [ ] 🟡 Gráfico: distribuição de bens por categoria
    - Aceite:
        - Gráfico de barras ou pizza com quantidade de bens por categoria
        - Tooltip com valor total por categoria
- [ ] 🟡 Gráfico: distribuição de bens por departamento
    - Aceite:
        - Mesmo estilo do gráfico de categorias
- [ ] 🟢 Link dos cards para a página de bens com o filtro adequado
    - Aceite:
        - Clicar no card "Trocar/Ruim" navega para /bens?estado=TROCAR,RUIM
        - Query params preservados na URL

### 1.6 Listagem com Filtros

- [x] Filtro por categoria
- [x] Filtro por departamento
- [x] Filtro por estado de conservação
- [ ] 🟢 Filtro por faixa de valor
- [ ] 🟢 Filtro por período de aquisição
- [x] Combinação de múltiplos filtros simultâneos
- [ ] 🟢 Limpeza de filtros (botão "Limpar")

### 1.7 Relatório Exportável para Excel

- [ ] 🔴 Gerar relatório no formato Excel (.xlsx)
    - Aceite:
        - Botão "Exportar" na tela de listagem de bens
        - Exporta os resultados com filtros aplicados
        - Colunas: placa, descrição, categoria, departamento, valor aquisição, valor atual, estado
        - Formato .xlsx compatível com Excel e LibreOffice
        - Layout similar à aba "Relatório" da planilha original
        - Download automático após geração

### 1.8 Importação da Planilha Excel Existente

- [ ] 🟡 Upload e importação de planilha Excel
    - Aceite:
        - Upload de arquivo .xlsx / .xlsm na interface
        - Mapeamento automático das colunas da planilha para campos do sistema
        - Tela de pré-visualização com validação (linhas válidas vs. inválidas)
        - Resumo pré-importação (quantos registros serão criados)
        - Execução em lote com relatório pós-importação (sucesso/erro por linha)

### 1.9 Cadastro/Edição de Usuários

- [ ] 🔴 CRUD de usuários para Admin
    - Aceite:
        - Admin pode criar, editar e desativar usuários da organização
        - Listagem com filtro para exibir/ocultar inativos
        - Opção de reativar usuário inativo
- [ ] 🟡 Troca de senha pelo próprio usuário
    - Aceite:
        - Formulário com senha atual + nova senha + confirmação
        - Validação de força mínima de senha
- [ ] 🟢 Recuperação de senha
    - Aceite:
        - Formulário de "esqueci a senha" com campo de e-mail
        - Envio de link de redefinição com token temporário
        - Token expira em 1 hora

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
