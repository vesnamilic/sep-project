INSERT INTO payment_method(name, deleted, subscription) VALUES ('Bank', false, false);
INSERT INTO payment_method(name, deleted, subscription) VALUES ('PayPal', false, true);
INSERT INTO payment_method(name, deleted, subscription) VALUES ('Bitcoin', false, false);

INSERT INTO seller(email, name, deleted, activated) VALUES ('mail@gmail.com', 'ProdavnicaNeka', false, true);
INSERT INTO seller(email, name, deleted, activated) VALUES ('test@gmail.com', 'Test', false, true);
INSERT INTO seller(email, name, deleted, activated) VALUES ('maja@gmail.com', 'Test', false, true);

INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,1);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,2);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (1,3);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (2,3);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (3,1);

INSERT INTO subscription_plan("type", "frequency", "cycles_number", "seller_id") VALUES ('FIXED', 'MONTH', 3, 1);
INSERT INTO subscription_plan("type", "frequency", "cycles_number", "seller_id") VALUES ('FIXED', 'MONTH', 6, 1);
INSERT INTO subscription_plan("type", "frequency", "cycles_number", "seller_id") VALUES ('FIXED', 'MONTH', 12, 1);
INSERT INTO subscription_plan("type", "frequency", "cycles_number", "seller_id") VALUES ('FIXED', 'YEAR', 2, 1);

--Maja K NC
INSERT INTO seller(email, name, deleted, activated) VALUES ('magazine-one@upp.com', 'Magazine One', false, true);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (4,2);

INSERT INTO seller(email, name, deleted, activated) VALUES ('magazine-two@upp.com', 'Magazine Two', false, true);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (5,2);

INSERT INTO seller(email, name, deleted, activated) VALUES ('magazine-three@upp.com', 'Magazine Three', false, true);
INSERT INTO seller_payment_methods(seller_id, payment_methods_id) VALUES (6,2);