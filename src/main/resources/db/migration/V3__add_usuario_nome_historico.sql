-- Adiciona coluna para desnormalizar o nome do usuário no histórico,
-- facilitando consultas de auditoria sem join na tabela usuario.
ALTER TABLE bem_historico ADD COLUMN usuario_nome VARCHAR(150) AFTER usuario_id;
