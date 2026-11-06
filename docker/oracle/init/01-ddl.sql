-- Executado automaticamente na primeira inicializacao do container como SYS,
-- no CDB raiz. Por isso muda para o PDB ECOMMERCE e para o schema ecommerce.
-- Idempotente: so cria os objetos se ainda nao existirem (Oracle 23ai).

ALTER SESSION SET CONTAINER = ECOMMERCE;
ALTER SESSION SET CURRENT_SCHEMA = ecommerce;

CREATE TABLE IF NOT EXISTS sales (
    id        NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    amount    NUMBER(15, 2) NOT NULL,
    dt_sale   TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    status    VARCHAR2(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS cancel (
    id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sale_id       NUMBER NOT NULL,
    cancel_amount NUMBER(15, 2) NOT NULL,
    dt_cancel     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    status        VARCHAR2(20) NOT NULL,
    CONSTRAINT fk_cancel_sale FOREIGN KEY (sale_id) REFERENCES sales (id)
);

CREATE INDEX IF NOT EXISTS idx_cancel_sale_id ON cancel (sale_id);
