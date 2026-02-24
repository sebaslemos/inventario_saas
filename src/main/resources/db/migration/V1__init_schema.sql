-- ============================================================
-- V1 – Schema inicial do Inventário (multi-tenant)
-- ============================================================

-- Tenant (organização/empresa cliente)
CREATE TABLE tenant (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    slug        VARCHAR(60)     NOT NULL UNIQUE COMMENT 'Identificador URL-friendly (ex: aeroclube-pb)',
    nome        VARCHAR(150)    NOT NULL,
    plano       VARCHAR(30)     NOT NULL DEFAULT 'FREE' COMMENT 'FREE | PRO',
    ativo       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Usuário
CREATE TABLE usuario (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    tenant_id       BIGINT          NOT NULL,
    nome            VARCHAR(150)    NOT NULL,
    email           VARCHAR(200)    NOT NULL,
    senha_hash      VARCHAR(255)    NOT NULL,
    perfil          VARCHAR(20)     NOT NULL DEFAULT 'USUARIO' COMMENT 'ADMIN | GESTOR | USUARIO',
    ativo           BOOLEAN         NOT NULL DEFAULT TRUE,
    ultimo_login    DATETIME(6),
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuario_email_tenant (email, tenant_id),
    CONSTRAINT fk_usuario_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Categoria de ativo (com parâmetros de depreciação)
CREATE TABLE categoria (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    tenant_id           BIGINT          NOT NULL,
    nome                VARCHAR(100)    NOT NULL,
    taxa_anual          DECIMAL(5,4)    NOT NULL COMMENT 'Ex: 0.2000 = 20% ao ano',
    vida_util_anos      INT             NOT NULL,
    revisar_em_anos     INT             NOT NULL DEFAULT 1,
    ativo               BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_categoria_nome_tenant (nome, tenant_id),
    CONSTRAINT fk_categoria_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Departamento / setor
CREATE TABLE departamento (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    tenant_id   BIGINT          NOT NULL,
    nome        VARCHAR(100)    NOT NULL,
    ativo       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_departamento_nome_tenant (nome, tenant_id),
    CONSTRAINT fk_departamento_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bem (ativo imobilizado)
CREATE TABLE bem (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    tenant_id           BIGINT          NOT NULL,
    placa               VARCHAR(30)     NOT NULL COMMENT 'Tag patrimonial (ex: X001)',
    categoria_id        BIGINT          NOT NULL,
    descricao           VARCHAR(255)    NOT NULL,
    valor_aquisicao     DECIMAL(15,2)   NOT NULL,
    fornecedor          VARCHAR(150),
    numero_serie        VARCHAR(100),
    numero_nf           VARCHAR(50)     COMMENT 'Número da nota fiscal',
    data_compra         DATE            NOT NULL,
    departamento_id     BIGINT          NOT NULL,
    descricao_local     VARCHAR(255)    COMMENT 'Localização física detalhada',
    responsavel         VARCHAR(150),
    estado              VARCHAR(20)     NOT NULL DEFAULT 'BOM' COMMENT 'BOM | MEDIO | RUIM | TROCAR',
    ultima_revisao      DATE,
    observacoes         TEXT,
    ativo               BOOLEAN         NOT NULL DEFAULT TRUE COMMENT 'FALSE = bem baixado',
    data_baixa          DATE,
    motivo_baixa        VARCHAR(255),
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by          BIGINT,
    updated_by          BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uq_bem_placa_tenant (placa, tenant_id),
    CONSTRAINT fk_bem_tenant       FOREIGN KEY (tenant_id)     REFERENCES tenant (id),
    CONSTRAINT fk_bem_categoria    FOREIGN KEY (categoria_id)  REFERENCES categoria (id),
    CONSTRAINT fk_bem_departamento FOREIGN KEY (departamento_id) REFERENCES departamento (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Histórico de movimentações do bem (transferências, revisões, baixas)
CREATE TABLE bem_historico (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    tenant_id   BIGINT          NOT NULL,
    bem_id      BIGINT          NOT NULL,
    tipo        VARCHAR(30)     NOT NULL COMMENT 'TRANSFERENCIA | REVISAO | BAIXA | ALTERACAO',
    descricao   TEXT            NOT NULL,
    data_evento DATE            NOT NULL,
    usuario_id  BIGINT,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_historico_bem    FOREIGN KEY (bem_id)     REFERENCES bem (id),
    CONSTRAINT fk_historico_tenant FOREIGN KEY (tenant_id)  REFERENCES tenant (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices de pesquisa
CREATE INDEX idx_bem_tenant       ON bem (tenant_id);
CREATE INDEX idx_bem_categoria    ON bem (categoria_id);
CREATE INDEX idx_bem_departamento ON bem (departamento_id);
CREATE INDEX idx_bem_estado       ON bem (estado);
CREATE INDEX idx_bem_ativo        ON bem (ativo);
CREATE INDEX idx_historico_bem    ON bem_historico (bem_id);
