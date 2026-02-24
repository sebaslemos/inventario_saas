# Guia de Processo — Desenvolvimento Solo Organizado

> Framework mínimo para um desenvolvedor que também gerencia o projeto.
> Zero burocracia, máximo de organização.

---

## Filosofia

1. **Documentação é código** — vive no repositório, evolui junto com o sistema
2. **Menos artefatos, mais disciplina** — poucos documentos, mas sempre atualizados
3. **Especificar antes de codar** — 10 minutos de escrita economizam 2 horas de retrabalho
4. **Incrementos pequenos** — entregas frequentes, branches curtas, merge rápido

---

## Artefatos do Projeto

O projeto mantém **5 documentos vivos** (incluindo este). Cada um tem um propósito claro e um momento certo para ser atualizado.

| Artefato                                   | O que é                         | Quando atualizar                    |
| ------------------------------------------ | ------------------------------- | ----------------------------------- |
| [README.md](../README.md)                  | Porta de entrada do projeto     | A cada mudança estrutural relevante |
| [TODO.md](../TODO.md)                      | Backlog priorizado              | Ao planejar ciclo ou surgir demanda |
| [ARCHITECTURE.md](ARCHITECTURE.md)         | Decisões técnicas e visão geral | Ao tomar decisão arquitetural       |
| [CHANGELOG.md](../CHANGELOG.md)            | Registro do que foi entregue    | Ao concluir feature/correção        |
| [GUIA-DE-PROCESSO.md](GUIA-DE-PROCESSO.md) | Este documento                  | Ao ajustar o processo               |

### O que NÃO manter

- Documentos de requisitos separados (o backlog é suficiente)
- Atas de reunião (você está sozinho)
- Diagramas UML detalhados (documente a arquitetura no nível certo)
- Wiki separada (tudo vive no repo)

---

## Ciclo de Desenvolvimento

O ciclo é semanal ou quinzenal — você escolhe a cadência. O importante é ter um ritmo.

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CICLO DE DESENVOLVIMENTO                     │
│                                                                     │
│  ┌──────────┐   ┌───────────┐   ┌───────────┐   ┌───────────────┐ │
│  │ PLANEJAR │──▶│ ESPECIF.  │──▶│ EXECUTAR  │──▶│   ENTREGAR    │ │
│  │ (30 min) │   │ (15 min)  │   │ (coding)  │   │  (30 min)     │ │
│  └──────────┘   └───────────┘   └───────────┘   └───────────────┘ │
│       │                                                  │         │
│       │              ┌────────────┐                      │         │
│       └──────────────│  REFLETIR  │◀─────────────────────┘         │
│                      │  (10 min)  │                                │
│                      └────────────┘                                │
└─────────────────────────────────────────────────────────────────────┘
```

### 1. Planejar (~30 min, início do ciclo)

**O que fazer:**

- Abrir o `TODO.md` e revisar o backlog
- Escolher 2-5 itens para o ciclo (seja realista)
- Marcar os itens escolhidos com `🔧 Em desenvolvimento` na seção de releases
- Criar uma branch para a feature principal (ou uma branch por feature)

**Regra de ouro:** se um item é grande demais para um ciclo, quebre-o em sub-itens.

### 2. Especificar (~15 min por feature, antes de codar)

**O que fazer:**

- No `TODO.md`, sob o item que vai implementar, escrever **critérios de aceite**
- São 2-5 frases simples que definem "o que significa estar pronto"
- Não é um documento formal — são lembretes para você mesmo

**Formato:**

```markdown
- [ ] Filtro por faixa de valor
    - Aceite:
        - Dois campos numéricos (mín/máx) no painel de filtros
        - Filtra combinado com os filtros existentes
        - Valor vazio = sem limite naquela direção
        - Query param na URL para compartilhar o filtro
```

**Por que isso importa:** sem critérios de aceite, você vai implementando e nunca sabe quando parar. Ou esquece de um detalhe e precisa revisitar o código.

### 3. Executar (o grosso do tempo)

**O que fazer:**

- Codar a feature na branch correspondente
- Fazer commits frequentes com mensagens descritivas
- Se tomar uma decisão técnica relevante, registrar em `ARCHITECTURE.md`

**Convenção de commits:**

```
tipo(escopo): descrição curta

Tipos: feat, fix, refactor, docs, style, test, chore
Escopo: bem, categoria, departamento, auth, dashboard, infra
```

Exemplos:

```
feat(bem): adiciona filtro por faixa de valor
fix(auth): corrige expiração do token JWT
refactor(departamento): extrai validação para service
docs: atualiza backlog com critérios de aceite
chore: atualiza dependências do frontend
```

### 4. Entregar (~30 min, fim do ciclo)

**O que fazer:**

- Testar a feature (manual e/ou automatizado)
- Fazer merge da branch (squash merge se muitos commits)
- Atualizar `CHANGELOG.md` com o que foi entregue
- Marcar os itens entregues como `[x]` no `TODO.md`
- Atualizar a tabela de releases no `TODO.md` (✅ Concluído)

### 5. Refletir (~10 min, fim do ciclo)

**O que fazer — responder mentalmente (ou anotar brevemente):**

- O que entreguei neste ciclo?
- Algo demorou mais do que esperava? Por quê?
- Preciso ajustar a estimativa dos próximos itens?
- Apareceu algum bug ou débito técnico? → Registrar no `TODO.md`

**Não é uma retrospectiva formal.** É um momento de calibrar suas expectativas para o próximo ciclo.

---

## Git Workflow

Workflow simples para dev solo — sem PRs formais, mas com branches organizadas.

```
main ─────────────────────────────────────────────▶ (sempre estável)
  │                                        ▲
  ├── feat/filtro-faixa-valor ────────────┘
  │                              ▲
  ├── feat/relatorio-excel ─────┘
  │
  ├── fix/corrige-calculo-depreciacao ────▶ (merge direto)
  │
  └── docs/atualiza-arquitetura ──────────▶ (merge direto)
```

### Regras

| Regra                     | Detalhe                                                                 |
| ------------------------- | ----------------------------------------------------------------------- |
| `main` é sempre funcional | Nunca commita código quebrado direto na main                            |
| Uma branch por feature    | Prefixo: `feat/`, `fix/`, `refactor/`, `docs/`, `chore/`                |
| Branches curtas           | Idealmente 1-5 dias. Se passou de uma semana, a feature é grande demais |
| Merge com squash          | Para features. Mantém o histórico limpo                                 |
| Hotfixes direto na main   | Só para correções urgentes e triviais                                   |
| Tags para releases        | `v1.0.0`, `v1.1.0`, etc. quando atingir um marco                        |

---

## Como escrever bons itens de backlog

O backlog (`TODO.md`) é seu artefato mais importante. Cada item deve ser:

### ✅ Bom item

```markdown
- [ ] Exportar relatório de bens para Excel (.xlsx)
    - Aceite:
        - Botão "Exportar" na tela de listagem de bens
        - Exporta os resultados com filtros aplicados
        - Colunas: placa, descrição, categoria, departamento, valor aquisição, valor atual, estado
        - Formato .xlsx compatível com Excel e LibreOffice
```

### ❌ Item vago

```markdown
- [ ] Melhorar relatórios
```

### Regras práticas

1. **Comece com verbo** — "Adicionar", "Corrigir", "Implementar", "Remover"
2. **Uma coisa por item** — se tem "e" no meio, provavelmente são dois itens
3. **Critérios de aceite** — 2-5 frases que respondem "como sei que está pronto?"
4. **Tamanho máximo** — se precisar de mais de ~3 dias, quebre em sub-itens

---

## Priorização

Não precisa de matriz de priorização elaborada. Use a posição no arquivo:

- **Topo da fase** = mais importante / próximo a fazer
- **Final da fase** = desejável, mas pode esperar

Se precisar de mais granularidade, use labels simples no item:

```markdown
- [ ] 🔴 Corrigir cálculo de depreciação para bens sem data de aquisição
- [ ] 🟡 Adicionar filtro por período de aquisição
- [ ] 🟢 Melhorar responsividade do dashboard em mobile
```

| Label | Significado                                  |
| ----- | -------------------------------------------- |
| 🔴    | Bloqueia ou impacta funcionalidade existente |
| 🟡    | Importante para completar a fase             |
| 🟢    | Nice-to-have, pode ficar para depois         |

---

## Quando documentar decisões técnicas

Nem toda decisão merece registro. Documente em `ARCHITECTURE.md` quando:

- ✅ Escolheu uma tecnologia/lib e teve alternativas viáveis
- ✅ Definiu um padrão que será seguido em todo o projeto
- ✅ Fez um trade-off consciente (performance vs. simplicidade, etc.)
- ✅ Algo não é óbvio e seu "eu do futuro" vai se perguntar "por quê?"

Não documente:

- ❌ Decisões óbvias ("usei Spring Data JPA para persistência")
- ❌ Detalhes de implementação (isso é o código)

---

## Checklist — Início de Projeto Novo

Se for iniciar outro projeto seguindo este framework:

- [ ] Criar `README.md` com visão geral, stack, como rodar
- [ ] Criar `TODO.md` com backlog inicial dividido em fases
- [ ] Criar `docs/ARCHITECTURE.md` com decisões iniciais
- [ ] Criar `CHANGELOG.md` vazio
- [ ] Criar `docs/GUIA-DE-PROCESSO.md` (copiar este)
- [ ] Configurar `.gitignore`
- [ ] Primeiro commit na `main`
- [ ] Primeiro ciclo de planejamento

---

## Resumo Executivo

```
📋 TODO.md          → "O que precisa ser feito" (backlog)
🏗️ ARCHITECTURE.md  → "Por que foi feito assim" (decisões)
📝 CHANGELOG.md     → "O que já foi feito" (histórico)
📖 README.md        → "O que é e como usar" (porta de entrada)
📐 GUIA-DE-PROCESSO → "Como eu trabalho" (este documento)
```

> **A melhor documentação é a que você realmente mantém atualizada.**
> **Cinco documentos atualizados valem mais que vinte abandonados.**
