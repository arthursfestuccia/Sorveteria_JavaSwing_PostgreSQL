-- Execute conectado ao banco sorveteria.
CREATE TABLE IF NOT EXISTS funcionarios (
 id SERIAL PRIMARY KEY,
 nome VARCHAR(100) NOT NULL,
 cargo VARCHAR(60) NOT NULL,
 telefone VARCHAR(30),
 usuario VARCHAR(50) NOT NULL UNIQUE,
 senha VARCHAR(100) NOT NULL
);
CREATE TABLE IF NOT EXISTS produtos (
 id SERIAL PRIMARY KEY,
 nome VARCHAR(100) NOT NULL,
 descricao VARCHAR(200),
 preco NUMERIC(10,2) NOT NULL CHECK (preco >= 0),
 estoque INTEGER NOT NULL CHECK (estoque >= 0)
);
CREATE TABLE IF NOT EXISTS vendas (
 id SERIAL PRIMARY KEY,
 data_venda TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 total NUMERIC(10,2) NOT NULL
);
CREATE TABLE IF NOT EXISTS itens_venda (
 id SERIAL PRIMARY KEY,
 venda_id INTEGER NOT NULL REFERENCES vendas(id),
 produto_id INTEGER NOT NULL REFERENCES produtos(id),
 quantidade INTEGER NOT NULL CHECK (quantidade > 0),
 preco_unitario NUMERIC(10,2) NOT NULL
);
INSERT INTO funcionarios (nome,cargo,telefone,usuario,senha)
SELECT 'Administrador','Gerente','','admin','1234'
WHERE NOT EXISTS (SELECT 1 FROM funcionarios WHERE usuario='admin');
INSERT INTO produtos (nome,descricao,preco,estoque)
SELECT 'Sorvete de chocolate','Pote 500 ml',15.00,20
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome='Sorvete de chocolate');
