CREATE TABLE IF NOT EXISTS categoria (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao   TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuario (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nome            TEXT NOT NULL,
    cpf             TEXT NOT NULL UNIQUE,
    email           TEXT NOT NULL,
    tipo            TEXT NOT NULL CHECK(tipo IN ('CLIENTE', 'GERENTE')),
    data_cadastro   TEXT,
    nivel_acesso    INTEGER
);

CREATE TABLE IF NOT EXISTS produto (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nome            TEXT NOT NULL UNIQUE,
    preco           REAL NOT NULL CHECK(preco >= 0),
    qtd_estoque     INTEGER NOT NULL DEFAULT 0 CHECK(qtd_estoque >= 0),
    categoria_id    INTEGER NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE IF NOT EXISTS venda (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    data            TEXT NOT NULL,
    cliente_id      INTEGER NOT NULL,
    tipo_pagamento  TEXT,
    desconto        REAL DEFAULT 0.0,
    total           REAL DEFAULT 0.0,
    FOREIGN KEY (cliente_id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS item_venda (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    venda_id        INTEGER NOT NULL,
    produto_id      INTEGER NOT NULL,
    quantidade      INTEGER NOT NULL CHECK(quantidade > 0),
    preco_unitario  REAL NOT NULL CHECK(preco_unitario >= 0),
    FOREIGN KEY (venda_id) REFERENCES venda(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

-- Dados iniciais
INSERT OR IGNORE INTO categoria (descricao) VALUES ('Alimentos');
INSERT OR IGNORE INTO categoria (descricao) VALUES ('Bebidas');
INSERT OR IGNORE INTO categoria (descricao) VALUES ('Limpeza');
INSERT OR IGNORE INTO categoria (descricao) VALUES ('Higiene');
INSERT OR IGNORE INTO categoria (descricao) VALUES ('Eletronicos');

INSERT OR IGNORE INTO usuario (nome, cpf, email, tipo, nivel_acesso) VALUES ('Admin Gerente', '00000000000', 'gerente@loja.com', 'GERENTE', 1);
INSERT OR IGNORE INTO usuario (nome, cpf, email, tipo, data_cadastro) VALUES ('Cliente Padrao', '11111111111', 'cliente@email.com', 'CLIENTE', '2026-01-01');

INSERT OR IGNORE INTO produto (nome, preco, qtd_estoque, categoria_id) VALUES ('Arroz 5kg', 25.90, 50, 1);
INSERT OR IGNORE INTO produto (nome, preco, qtd_estoque, categoria_id) VALUES ('Feijao 1kg', 8.50, 40, 1);
INSERT OR IGNORE INTO produto (nome, preco, qtd_estoque, categoria_id) VALUES ('Refrigerante 2L', 7.99, 30, 2);
INSERT OR IGNORE INTO produto (nome, preco, qtd_estoque, categoria_id) VALUES ('Detergente 500ml', 3.50, 60, 3);
INSERT OR IGNORE INTO produto (nome, preco, qtd_estoque, categoria_id) VALUES ('Sabonete', 2.99, 100, 4);
INSERT OR IGNORE INTO produto (nome, preco, qtd_estoque, categoria_id) VALUES ('Fone de Ouvido', 45.00, 15, 5);
