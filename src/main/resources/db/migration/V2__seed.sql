INSERT INTO product (id, name, sku, unit_price, created_at, updated_at, updated_by, is_deleted, version)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Notebook 14"', 'SKU-NOTEBOOK-14', 3500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', FALSE, 0),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Mouse', 'SKU-MOUSE', 120.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', FALSE, 0),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Teclado', 'SKU-TECLADO', 250.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', FALSE, 0);

INSERT INTO partner (id, name, available_credit, created_at, updated_at, updated_by, is_deleted, version)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Partner Alpha', 10000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', FALSE, 0),
    ('22222222-2222-2222-2222-222222222222', 'Partner Beta', 500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', FALSE, 0);
