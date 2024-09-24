INSERT INTO users (first_name, last_name, email, password) VALUES ('User', 'Tester', 'test@example.com','$2a$10$u06mL5aQs7J8lbnMWuIulu1zwW2.Pf.ESkAt/jPkDrTbPN12u2eHe');
INSERT INTO users (first_name, last_name, email, password) VALUES ('Juan', 'Perez', 'juann@gmail.com', '$2a$10$3FV4k9zQj0zg1Z5Hl2qy8e7Y8q3JZ2HtY5jX0Xz9Z1zj9XeZcZ8Jq');
INSERT INTO users (first_name, last_name, email, password) VALUES ('Maria', 'Gomez', 'maria@gmail.com', '$2a$10$3FV4k9zQj0zg1Z5Hl2qy8e7Y8q3JZ2HtY5jX0Xz9Z1zj9XeZcZ8Jq');

INSERT INTO roles (authority) VALUES ('ADMIN');
INSERT INTO roles (authority) VALUES ('USER');

INSERT INTO user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);

INSERT INTO category (name, created_at) VALUES ('Jazz', NOW());
INSERT INTO category (name, created_at) VALUES ('Pop', NOW());
INSERT INTO category (name, created_at) VALUES ('Rock', NOW());
INSERT INTO category (name, created_at) VALUES ('Chill', NOW());
INSERT INTO category (name, created_at) VALUES ('Rap', NOW());
INSERT INTO category (name, created_at) VALUES ('Classical', NOW());
INSERT INTO category (name, created_at) VALUES ('Country', NOW());
INSERT INTO category (name, created_at) VALUES ('Blues', NOW());
INSERT INTO category (name, created_at) VALUES ('Reggae', NOW());
INSERT INTO category (name, created_at) VALUES ('Electronic', NOW());
INSERT INTO category (name, created_at) VALUES ('Metal', NOW());
INSERT INTO category (name, created_at) VALUES ('Alternative', NOW());

INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of Miles Davis', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of The Beatles', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of The Rolling Stones', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of Bob Marley', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of Eminem', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of Beethoven', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of Johnny Cash', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of B.B. King', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of Daft Punk', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of Metallica', 9.99, 'https://picsum.photos/200', NOW(), null);
INSERT INTO product (name, price, img_url, created_at, updated_at) VALUES ('The Best of SIAMES', 9.99, 'https://picsum.photos/200', NOW(), null);

INSERT INTO product_category (product_id, category_id) VALUES (1, 1);
INSERT INTO product_category (product_id, category_id) VALUES (2, 2);
INSERT INTO product_category (product_id, category_id) VALUES (2, 3);
INSERT INTO product_category (product_id, category_id) VALUES (3, 3);
INSERT INTO product_category (product_id, category_id) VALUES (3, 8);
INSERT INTO product_category (product_id, category_id) VALUES (4, 9);
INSERT INTO product_category (product_id, category_id) VALUES (5, 5);
INSERT INTO product_category (product_id, category_id) VALUES (6, 6);
INSERT INTO product_category (product_id, category_id) VALUES (7, 7);
INSERT INTO product_category (product_id, category_id) VALUES (8, 8);
INSERT INTO product_category (product_id, category_id) VALUES (9, 10);
INSERT INTO product_category (product_id, category_id) VALUES (10, 11);