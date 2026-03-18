CREATE TABLE IF NOT EXISTS menu_item (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    restaurant_only BOOLEAN NOT NULL,
    restaurant_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    customer_uuid UUID NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status_order VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGSERIAL PRIMARY KEY,
    menu_item_id BIGINT NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    quantity NUMERIC(19, 2) NOT NULL,
    order_id BIGINT NOT NULL,
    CONSTRAINT fk_order_item_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_item(id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

INSERT INTO menu_item (id, name, price, restaurant_only, restaurant_id)
VALUES
    (1, 'Pizza Margherita', 45.90, false, 1),
    (2, 'Lasanha à Bolonhesa', 52.00, false, 1),
    (3, 'Tech Burger Extra', 38.50, false, 2),
    (4, 'Batata Rústica', 18.00, false, 2);
