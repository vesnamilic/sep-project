INSERT INTO payment_method(name, deleted) VALUES ('Bank', false);
INSERT INTO payment_method(name, deleted) VALUES ('PayPal', false);
--INSERT INTO payment_method(name, deleted) VALUES ('BitCoin', false);

INSERT INTO seller(email, name, deleted) VALUES ('mail@gmail.com', 'ProdavnicaNeka', false);

INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,1);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,2);
--INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,3);

