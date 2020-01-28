INSERT INTO payment_method(name, deleted, subscription) VALUES ('Bank', false, false);
INSERT INTO payment_method(name, deleted, subscription) VALUES ('PayPal', false, true);
INSERT INTO payment_method(name, deleted, subscription) VALUES ('Bitcoin', false, false);

INSERT INTO seller(email, name, deleted) VALUES ('mail@gmail.com', 'ProdavnicaNeka', false);
INSERT INTO seller(email, name, deleted) VALUES ('test@gmail.com', 'Test', false);
INSERT INTO seller(email, name, deleted) VALUES ('maja@gmail.com', 'Test', false);


INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,1);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,2);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,3);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (2,3);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (3,1);