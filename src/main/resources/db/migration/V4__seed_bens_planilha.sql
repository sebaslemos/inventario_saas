-- ============================================================
-- V4 – Importação dos bens da planilha patrimonial
--      PLANILHA DO PATRIMÔNIO DO AEROCLUBE DA PARAÍBA
-- ============================================================

-- ----------------------------------------------------------------
-- 1. Novas categorias presentes na planilha
-- ----------------------------------------------------------------
INSERT IGNORE INTO categoria (tenant_id, nome, taxa_anual, vida_util_anos, revisar_em_anos, ativo)
SELECT t.id, 'Aeronaves', 0.0500, 20, 1, TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT IGNORE INTO categoria (tenant_id, nome, taxa_anual, vida_util_anos, revisar_em_anos, ativo)
SELECT t.id, 'Trator de Aeronave', 0.1000, 10, 1, TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- ----------------------------------------------------------------
-- 2. Novos departamentos presentes na planilha
-- ----------------------------------------------------------------
INSERT IGNORE INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Sub Sede - Cabedelo', TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT IGNORE INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Sede do Aeroclube', TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- ----------------------------------------------------------------
-- 3. Bens (ativo imobilizado)
--    Regras de importação:
--      • Placas normalizadas para MAIÚSCULAS
--      • data_compra ausente/inválida → '1900-01-01'
--      • valor_aquisicao ausente       → 0.00
--      • fornecedor 'sem infor.'       → NULL
--      • numero_nf 'S/NF'             → NULL
--      • estado da planilha: Bom→BOM, Médio→MEDIO
--      • X00 duplicado: primeiro=instalação (X00A), segundo=edifício (X00B)
-- ----------------------------------------------------------------

-- X00A – Iluminação Pista (Instalações)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X00A',
    (SELECT id FROM categoria    WHERE nome = 'Instalações'    AND tenant_id = t.id),
    'Iluminação Pista',
    0.00, NULL, NULL, NULL,
    '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Geral'          AND tenant_id = t.id),
    NULL, NULL, 'BOM', NULL, TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0009 – Notebook Samsung
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0009',
    (SELECT id FROM categoria    WHERE nome = 'Computadores e Periféricos' AND tenant_id = t.id),
    'Notebook',
    2500.00, 'Samsung', '50', '123654',
    '2015-01-10',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'        AND tenant_id = t.id),
    'Mesa 001', 'Charles Xavier', 'BOM', '2023-01-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X00B – Prédio administrativo (Edifícios)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X00B',
    (SELECT id FROM categoria    WHERE nome = 'Edifícios' AND tenant_id = t.id),
    'Prédio administrativo',
    1000000.00, NULL, NULL, NULL,
    '2000-01-10',
    (SELECT id FROM departamento WHERE nome = 'Geral'     AND tenant_id = t.id),
    'Situado á rua Guilherme, 1500. Centro de Joinville/SC. Registro de imóvel abcde156',
    'Ciclope', 'BOM', '2023-01-05', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0001 – Aero Boero AB 115 (Aeronave)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0001',
    (SELECT id FROM categoria    WHERE nome = 'Aeronaves'          AND tenant_id = t.id),
    'Aero Boero - AB 115',
    150000.00, 'Aero Boero', '107-B', NULL,
    '1989-10-17',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'  AND tenant_id = t.id),
    'PP-FGV', NULL, 'BOM', '2025-08-20', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0004 – Condicionador de Ar TCL (1)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0004',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios'   AND tenant_id = t.id),
    'Condicionador de Ar TCL',
    0.00, NULL, NULL, NULL,
    '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'   AND tenant_id = t.id),
    'Sala de Reunião', NULL, 'BOM', '2025-10-27', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0005 – Condicionador de Ar TCL (2)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0005',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios'   AND tenant_id = t.id),
    'Condicionador de Ar TCL',
    0.00, NULL, NULL, NULL,
    '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'   AND tenant_id = t.id),
    'Sala de Reunião', NULL, 'BOM', '2025-10-27', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0006 – Mesa de Reunião (Sub Sede)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0006',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios'   AND tenant_id = t.id),
    'Mesa de Reunião',
    0.00, NULL, NULL, NULL,
    '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'   AND tenant_id = t.id),
    'Sala de Reunião', NULL, 'MEDIO', '2025-10-27', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0007 – Gela Água Esmaltec
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0007',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios'   AND tenant_id = t.id),
    'Gela Água Esmaltec',
    0.00, NULL, NULL, NULL,
    '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'   AND tenant_id = t.id),
    'Sala de Reunião', NULL, 'BOM', '2025-10-27', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0008 – Impressora EPSON
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0008',
    (SELECT id FROM categoria    WHERE nome = 'Computadores e Periféricos' AND tenant_id = t.id),
    'Impressora EPSON',
    0.00, NULL, NULL, NULL,
    '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'        AND tenant_id = t.id),
    'Sala de Reunião', NULL, 'MEDIO', '2025-10-27', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0010 – Emplastificador APP-Tech
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0010',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios'   AND tenant_id = t.id),
    'Emplastificador APP-Tech',
    0.00, NULL, NULL, NULL,
    '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'   AND tenant_id = t.id),
    'Sala de Reunião', NULL, 'MEDIO', '2025-10-27', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0011..X0022 – Cadeiras (Hangar 0001 / Sede do Aeroclube)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0011',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira c/ braço baixa giratória', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0012',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira c/braço baixa giratória', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0013',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira c/braço baixa giratória', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0014',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira c/braço alta giratória', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0015',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira c/braço baixa giratória', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0016',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira c/braço alta giratória', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0017',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira c/braço fixa', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0018',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira s/braço fixa', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0019',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira s/braço fixa', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0020',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira s/braço fixa', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0021',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira s/braço fixa', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0022',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira s/braço fixa', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0023 – Reboque de Aeronave (Trator de Aeronave)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0023',
    (SELECT id FROM categoria    WHERE nome = 'Trator de Aeronave' AND tenant_id = t.id),
    'Reboque de Aeronave',
    26000.00, 'Tug Max Indust.', '1', '69',
    '2025-07-23',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'  AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0024..X0029 – Cadeiras Escolares
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0024',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Escolar', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0025',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Escolar', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0026',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Escolar', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0027',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Escolar', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0028',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Escolar', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0029',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Escolar', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0030 – Condicionador de Ar Consul
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0030',
    (SELECT id FROM categoria    WHERE nome = 'Máquinas e Equipamentos' AND tenant_id = t.id),
    'Condicionador de Ar Consul', 1000.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'        AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0031 – Condicionador de Ar Sprint
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0031',
    (SELECT id FROM categoria    WHERE nome = 'Máquinas e Equipamentos' AND tenant_id = t.id),
    'Condicionador de Ar Sprint', 1000.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'        AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0032 – Condicionador de Ar Philco (Máquinas)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0032',
    (SELECT id FROM categoria    WHERE nome = 'Máquinas e Equipamentos' AND tenant_id = t.id),
    'Condicionador de Ar Philco', 1000.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'        AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0033 – Condicionador de Ar Philco (Móveis)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0033',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Condicionador de Ar Philco', 1000.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0034 – Microondas LG
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0034',
    (SELECT id FROM categoria    WHERE nome = 'Máquinas e Equipamentos' AND tenant_id = t.id),
    'Microondas LG', 500.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'        AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0035 – Cadeira s/braço baixa (Portaria)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0035',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira s/braço baixa', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Portaria', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0036 – Cadeira c/braço baixa (Portaria)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0036',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira c/braço baixa', 200.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Portaria', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0037 – Computador AOC AHD EXT
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0037',
    (SELECT id FROM categoria    WHERE nome = 'Computadores e Periféricos' AND tenant_id = t.id),
    'Computador AOC - AHD EXT', 1500.00, 'AOC', NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'        AND tenant_id = t.id),
    'Sala de Reunião', NULL, 'MEDIO', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0038 – Motoserra Honda 4T
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0038',
    (SELECT id FROM categoria    WHERE nome = 'Máquinas e Equipamentos' AND tenant_id = t.id),
    'Motoserra Honda - 4T', 1000.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'        AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0039 – Motoserra Stihl 2T
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0039',
    (SELECT id FROM categoria    WHERE nome = 'Máquinas e Equipamentos' AND tenant_id = t.id),
    'Motoserra Stihl - 2T', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'        AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2025-07-01', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0040..X0044 – Cadeiras Altas c/Braço (Sede Aeroclube)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0040',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Alta c/Braço', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0041',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Alta c/Braço', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0042',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Alta c/Braço', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0043 – placa na planilha está em categoria "Trator de Aeronave" porém descrição é "Cadeira Alta c/Braço"
--         mantemos a categoria da planilha conforme dado original
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo,
                 observacoes)
SELECT t.id, 'X0043',
    (SELECT id FROM categoria    WHERE nome = 'Trator de Aeronave' AND tenant_id = t.id),
    'Cadeira Alta c/Braço', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'  AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE,
    'Verificar categoria: consta como Trator de Aeronave na planilha original'
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0044',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Alta c/Braço', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0045 – Mesa de Reunião (Sede do Aeroclube)
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0045',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Mesa de Reunião', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0046..X0047 – Cadeiras Giratórias de Apoio
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0046',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Giratória de Apoio', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0047',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Cadeira Giratória de Apoio', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0048..X0049 – Mesas de Centro
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0048',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Mesa de Centro', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0049',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Mesa de Centro', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0050..X0054 – Mesas de Apoio Lateral
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0050',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Mesa de Apoio Lateral', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0051',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Mesa de Apoio Lateral', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0052',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Mesa de Apoio Lateral', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0053',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Mesa de Apoio Lateral', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0054',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Mesa de Apoio Lateral', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0055..X0056 – Sofás de 05 Lugares
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0055',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Sofá de 05 lugares', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0056',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Sofá de 05 lugares', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0057..X0062 – Extintores
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0057',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Extintor Pó Químico', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0058',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Extintor - Água', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0059',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Extintor Pó Químico', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0060',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Extintor - Água', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0061',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Extintor Pó Químico', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT t.id, 'X0062',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios' AND tenant_id = t.id),
    'Extintor - Água', 0.00, NULL, NULL, NULL, '1900-01-01',
    (SELECT id FROM departamento WHERE nome = 'Sede do Aeroclube'   AND tenant_id = t.id),
    'Hangar nº 0001', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- X0063 – TV Samsung LED 55 Pol.
INSERT INTO bem (tenant_id, placa, categoria_id, descricao, valor_aquisicao, fornecedor, numero_serie, numero_nf,
                 data_compra, departamento_id, descricao_local, responsavel, estado, ultima_revisao, ativo)
SELECT
    t.id,
    'X0063',
    (SELECT id FROM categoria    WHERE nome = 'Móveis e Utensílios'   AND tenant_id = t.id),
    'TV Samsung LED - 55 Pol.',
    2329.90, 'Ferreira Costa', '10', '509390',
    '2025-11-29',
    (SELECT id FROM departamento WHERE nome = 'Sub Sede - Cabedelo'   AND tenant_id = t.id),
    'Sala de Reunião', NULL, 'BOM', '2026-02-08', TRUE
FROM tenant t WHERE t.slug = 'aeroclube-pb';
