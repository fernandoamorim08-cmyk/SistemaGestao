-- ============================================================
--  SISTEMA DE GESTÃO DE PROJETOS E EQUIPES
--  Script SQL Completo
--  Banco de Dados: MySQL 8.0+
-- ============================================================
--  CONCEITO: Um banco de dados relacional organiza os dados
--  em tabelas (como planilhas) que se relacionam entre si
--  através de chaves primárias (PK) e estrangeiras (FK).
-- ============================================================

-- Cria o banco de dados se não existir
CREATE DATABASE IF NOT EXISTS sistema_gestao
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Seleciona o banco de dados para uso
USE sistema_gestao;

-- ============================================================
-- TABELA: usuarios
-- Armazena todos os usuários do sistema
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id          INT AUTO_INCREMENT PRIMARY KEY,  -- PK: identificador único
    nome        VARCHAR(100) NOT NULL,
    cpf         VARCHAR(14)  NOT NULL UNIQUE,    -- CPF único por usuário
    email       VARCHAR(100) NOT NULL UNIQUE,
    cargo       VARCHAR(50),
    login       VARCHAR(50)  NOT NULL UNIQUE,
    senha       VARCHAR(255) NOT NULL,           -- Armazenar hash em produção
    perfil      ENUM('ADMINISTRADOR','GERENTE','COLABORADOR') NOT NULL DEFAULT 'COLABORADOR',
    ativo       TINYINT(1) DEFAULT 1,            -- 1=ativo, 0=inativo
    criado_em   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABELA: projetos
-- Armazena os projetos cadastrados
-- ============================================================
CREATE TABLE IF NOT EXISTS projetos (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nome                VARCHAR(100) NOT NULL,
    descricao           TEXT,
    data_inicio         DATE NOT NULL,
    data_previsao_fim   DATE,
    status              ENUM('PLANEJADO','EM_ANDAMENTO','CONCLUIDO','CANCELADO') DEFAULT 'PLANEJADO',
    gerente_id          INT,                     -- FK → usuarios
    criado_em           DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- Chave estrangeira: liga o projeto ao seu gerente
    CONSTRAINT fk_projeto_gerente
        FOREIGN KEY (gerente_id) REFERENCES usuarios(id)
        ON DELETE SET NULL
);

-- ============================================================
-- TABELA: equipes
-- Armazena as equipes de trabalho
-- ============================================================
CREATE TABLE IF NOT EXISTS equipes (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    descricao   TEXT,
    criado_em   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABELA: equipe_membros  (tabela de relacionamento N:N)
-- Uma equipe pode ter vários membros; um membro pode estar
-- em várias equipes → relacionamento Muitos para Muitos
-- ============================================================
CREATE TABLE IF NOT EXISTS equipe_membros (
    equipe_id   INT NOT NULL,
    usuario_id  INT NOT NULL,
    data_entrada DATE DEFAULT (CURRENT_DATE),

    PRIMARY KEY (equipe_id, usuario_id),         -- PK composta

    CONSTRAINT fk_membro_equipe
        FOREIGN KEY (equipe_id) REFERENCES equipes(id) ON DELETE CASCADE,
    CONSTRAINT fk_membro_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ============================================================
-- TABELA: projeto_equipes  (tabela de relacionamento N:N)
-- Uma equipe pode participar de vários projetos e um projeto
-- pode ter várias equipes → relacionamento Muitos para Muitos
-- ============================================================
CREATE TABLE IF NOT EXISTS projeto_equipes (
    projeto_id  INT NOT NULL,
    equipe_id   INT NOT NULL,
    data_alocacao DATE DEFAULT (CURRENT_DATE),

    PRIMARY KEY (projeto_id, equipe_id),

    CONSTRAINT fk_pe_projeto
        FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE CASCADE,
    CONSTRAINT fk_pe_equipe
        FOREIGN KEY (equipe_id) REFERENCES equipes(id) ON DELETE CASCADE
);

-- ============================================================
-- TABELA: tarefas
-- Armazena as tarefas dos projetos
-- ============================================================
CREATE TABLE IF NOT EXISTS tarefas (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    titulo          VARCHAR(100) NOT NULL,
    descricao       TEXT,
    projeto_id      INT NOT NULL,               -- FK → projetos
    responsavel_id  INT,                         -- FK → usuarios
    data_limite     DATE,
    prioridade      ENUM('BAIXA','MEDIA','ALTA','CRITICA') DEFAULT 'MEDIA',
    status          ENUM('PENDENTE','EM_ANDAMENTO','CONCLUIDA','CANCELADA') DEFAULT 'PENDENTE',
    criado_em       DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tarefa_projeto
        FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE CASCADE,
    CONSTRAINT fk_tarefa_responsavel
        FOREIGN KEY (responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- ============================================================
-- INSERTS DE EXEMPLO (dados para testes)
-- ============================================================

-- Usuários de exemplo
INSERT INTO usuarios (nome, cpf, email, cargo, login, senha, perfil) VALUES
('Ana Silva',       '111.111.111-11', 'fernando@empresa.com',    'Diretor de TI',       'ana',    'admin123',  'ADMINISTRADOR'),
('Carlos Souza',    '222.222.222-22', 'carlos@empresa.com', 'Gerente de Projetos',  'carlos', 'gerente123','GERENTE'),
('Maria Oliveira',  '333.333.333-33', 'maria@empresa.com',  'Desenvolvedora',       'maria',  'colab123',  'COLABORADOR'),
('Pedro Lima',      '444.444.444-44', 'pedro@empresa.com',  'Designer',             'pedro',  'colab456',  'COLABORADOR'),
('Julia Mendes',    '555.555.555-55', 'julia@empresa.com',  'Analista de Sistemas', 'julia',  'colab789',  'COLABORADOR');

-- Projetos de exemplo
INSERT INTO projetos (nome, descricao, data_inicio, data_previsao_fim, status, gerente_id) VALUES
('Portal Corporativo', 'Desenvolvimento do portal web da empresa', '2024-01-10', '2024-06-30', 'EM_ANDAMENTO', 2),
('App Mobile',         'Aplicativo mobile para clientes',          '2024-03-01', '2024-09-30', 'PLANEJADO',    2),
('Migração de Dados',  'Migração do banco legado para o novo',      '2023-10-01', '2024-02-28', 'CONCLUIDO',    2);

-- Equipes de exemplo
INSERT INTO equipes (nome, descricao) VALUES
('Equipe Frontend',   'Responsável pelas interfaces visuais'),
('Equipe Backend',    'Responsável pelas APIs e banco de dados'),
('Equipe Design',     'Responsável pelo design e UX');

-- Membros das equipes
INSERT INTO equipe_membros (equipe_id, usuario_id) VALUES
(1, 3), -- Maria na Equipe Frontend
(1, 4), -- Pedro na Equipe Frontend
(2, 3), -- Maria na Equipe Backend
(2, 5), -- Julia na Equipe Backend
(3, 4); -- Pedro na Equipe Design

-- Alocação de equipes em projetos
INSERT INTO projeto_equipes (projeto_id, equipe_id) VALUES
(1, 1), -- Equipe Frontend no Portal Corporativo
(1, 2), -- Equipe Backend no Portal Corporativo
(2, 1), -- Equipe Frontend no App Mobile
(2, 3); -- Equipe Design no App Mobile

-- Tarefas de exemplo
INSERT INTO tarefas (titulo, descricao, projeto_id, responsavel_id, data_limite, prioridade, status) VALUES
('Criar tela de login',      'Desenvolver a tela de autenticação',      1, 3, '2024-02-15', 'ALTA',   'CONCLUIDA'),
('Configurar banco de dados','Setup inicial do banco',                   1, 5, '2024-02-10', 'CRITICA','CONCLUIDA'),
('Desenvolver menu lateral',  'Menu de navegação responsivo',            1, 3, '2024-03-20', 'MEDIA',  'EM_ANDAMENTO'),
('Criar protótipo do app',    'Protótipo interativo no Figma',           2, 4, '2024-04-15', 'ALTA',   'PENDENTE'),
('Definir arquitetura API',   'Documentar endpoints REST do app mobile', 2, 5, '2024-04-30', 'ALTA',   'PENDENTE');

-- ============================================================
-- CONSULTAS SQL BÁSICAS (exemplos didáticos)
-- ============================================================

-- 1. Listar todos os projetos em andamento
-- SELECT p.nome, p.data_inicio, p.data_previsao_fim, u.nome AS gerente
-- FROM projetos p
-- JOIN usuarios u ON p.gerente_id = u.id
-- WHERE p.status = 'EM_ANDAMENTO';

-- 2. Listar tarefas pendentes com seus responsáveis
-- SELECT t.titulo, t.prioridade, t.data_limite, u.nome AS responsavel, p.nome AS projeto
-- FROM tarefas t
-- LEFT JOIN usuarios u ON t.responsavel_id = u.id
-- JOIN projetos p ON t.projeto_id = p.id
-- WHERE t.status = 'PENDENTE'
-- ORDER BY t.prioridade DESC, t.data_limite ASC;

-- 3. Listar usuários por perfil
-- SELECT perfil, COUNT(*) AS total FROM usuarios GROUP BY perfil;

-- 4. Membros de uma equipe específica
-- SELECT u.nome, u.cargo, u.email
-- FROM equipe_membros em
-- JOIN usuarios u ON em.usuario_id = u.id
-- WHERE em.equipe_id = 1;
