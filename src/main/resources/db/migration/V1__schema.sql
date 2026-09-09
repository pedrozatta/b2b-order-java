CREATE TABLE product (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_product_sku ON product (sku);
CREATE INDEX idx_product_is_deleted ON product (is_deleted);

CREATE TABLE partner (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    available_credit NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_partner_is_deleted ON partner (is_deleted);

CREATE TABLE customer_order (
    id UUID NOT NULL PRIMARY KEY,
    partner_id UUID NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(40) NOT NULL,
    credit_debited BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_customer_order_partner FOREIGN KEY (partner_id) REFERENCES partner (id)
);

CREATE INDEX idx_customer_order_status ON customer_order (status);
CREATE INDEX idx_customer_order_created_at ON customer_order (created_at);
CREATE INDEX idx_customer_order_is_deleted ON customer_order (is_deleted);

CREATE TABLE order_item (
    id UUID NOT NULL PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES customer_order (id)
);

CREATE INDEX idx_order_item_order_id ON order_item (order_id);
