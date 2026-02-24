-- V2: Tenant e usuário admin inicial para desenvolvimento
-- Senha: admin123  (BCrypt hash)

INSERT INTO tenant (slug, nome, plano, ativo)
VALUES ('aeroclube-pb', 'Aeroclube da Paraíba', 'FREE', TRUE);

INSERT INTO usuario (tenant_id, nome, email, senha_hash, perfil, ativo)
VALUES (
    (SELECT id FROM tenant WHERE slug = 'aeroclube-pb'),
    'Administrador',
    'admin@aeroclube.pb',
    '$2a$10$AeiX6sNyqy7nay3M2EmCuuGLg/Qr/SZN7sev1o687Bq6b/MmeK/cK',
    'ADMIN',
    TRUE
);

-- Categorias padrão (espelho da planilha)
INSERT INTO categoria (tenant_id, nome, taxa_anual, vida_util_anos, revisar_em_anos, ativo)
SELECT t.id, 'Computadores e Periféricos', 0.2000, 5,  1, TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO categoria (tenant_id, nome, taxa_anual, vida_util_anos, revisar_em_anos, ativo)
SELECT t.id, 'Edifícios', 0.0400, 25, 2, TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO categoria (tenant_id, nome, taxa_anual, vida_util_anos, revisar_em_anos, ativo)
SELECT t.id, 'Instalações', 0.1000, 10, 2, TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO categoria (tenant_id, nome, taxa_anual, vida_util_anos, revisar_em_anos, ativo)
SELECT t.id, 'Máquinas e Equipamentos', 0.1000, 10, 1, TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO categoria (tenant_id, nome, taxa_anual, vida_util_anos, revisar_em_anos, ativo)
SELECT t.id, 'Móveis e Utensílios', 0.1000, 10, 2, TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

INSERT INTO categoria (tenant_id, nome, taxa_anual, vida_util_anos, revisar_em_anos, ativo)
SELECT t.id, 'Veículos', 0.2000, 5, 1, TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';

-- Departamentos padrão
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Compras',      TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Contabilidade', TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Financeiro',   TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Fiscal',       TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Geral',        TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Jurídico',     TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'Logística',    TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'RH',           TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
INSERT INTO departamento (tenant_id, nome, ativo)
SELECT t.id, 'T.I.',         TRUE FROM tenant t WHERE t.slug = 'aeroclube-pb';
