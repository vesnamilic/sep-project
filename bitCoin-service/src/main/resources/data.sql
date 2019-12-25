
-- MERCHANT DATA --
INSERT INTO merchant(id,email, token) VALUES (1,'test@gmail.com', 'Funpnt4xVdM7J9yPJXJkPTW1NGlk35Moq3DgjmU67fMFfbfFtbDeVkvRFoLaTiZm');


-- TRANSACTION DATA -- 

INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (1, '2019-12-20 23:04:58 UTC', 223785, 0.5, 'BTC', 0 , 'BTC', 'invalid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (2, '2019-12-21 18:07:38 UTC', 223957, 0.5, 'BTC', 0 , '', 'expired', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (3, '2019-12-21 18:21:10 UTC', 223958, 0.5, 'BTC', 0 , '', 'expired', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (4, '2019-12-21 18:24:14 UTC', 223959, 5, 'USD', 0 , 'BTC', 'expired', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (5, '2019-12-21 18:26:38 UTC', 223960, 6, 'USD', 0, 'BTC', 'expired', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (6, '2019-12-21 18:32:05 UTC', 223962, 7, 'USD', 0 , 'BTC', 'expired', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (7, '2019-12-21 18:43:30 UTC', 223966, 7, 'USD', 0 , 'BTC', 'invalid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (8, '2019-12-21 18:50:57 UTC', 223967, 7, 'USD', 0 , 'BTC', 'invalid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (9, '2019-12-21 18:54:25 UTC', 223968, 7, 'USD', 0.000969 , 'BTC', 'paid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (10, '2019-12-21 19:07:45 UTC', 223970, 1, 'USD', 0.000139 , 'BTC', 'paid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (11, '2019-12-21 19:16:32 UTC', 223972, 1, 'USD', 0.000139 , 'BTC', 'paid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (12, '2019-12-21 19:30:21 UTC', 223975, 1, 'USD', 0.000139, 'BTC', 'paid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (13, '2019-12-22 15:48:57 UTC', 224046, 0.5, 'BTC', 0 , '', 'expired', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (14, '2019-12-22 15:48:57 UTC', 224063, 0.5, 'BTC', 0 , '', 'invalid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (15, '2019-12-22 21:08:41 UTC', 224065, 0.5, 'BTC', 0 , 'BTC', 'invalid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (16, '2019-12-22 21:41:42 UTC', 224066, 2, 'USD', 0 , 'BTC', 'invalid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (17, '2019-12-22 21:41:51 UTC', 224067, 2, 'USD', 0 , 'BTC', 'invalid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (18, '2019-12-22 21:42:01 UTC', 224068, 2, 'USD', 0.000267 , 'BTC', 'paid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (19, '2019-12-22 21:45:22 UTC', 224069, 2, 'USD', 0 , 'BTC', 'invalid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (20, '2019-12-22 22:00:15 UTC', 224070, 2, 'USD', 0.000267 , 'BTC', 'paid', 1);
INSERT INTO transaction(id, creation_date, payment_id, price_amount, price_currency, receive_amount, receive_currency, status, merchant_id) VALUES (21, '2019-12-22 22:03:41 UTC', 224071, 2, 'USD', 0.000267 , 'BTC', 'new', 1);