INSERT INTO public.card(id, account_number, available_funds, cvv, exp_date, pan, reserved_funds) VALUES (1, '11111111', 200000, '2132', '9/2019', '111111111', 0);
INSERT INTO public.card(id, account_number, available_funds, cvv, exp_date, pan, reserved_funds) VALUES (2, '11111112', 200000, '1111', '9/2019', '222222222', 0);

INSERT INTO public.card_owner(id, email, last_name, merchantid, merchant_pass, name, card_id) VALUES (1, 'maja@gmail.com', 'matkovski', '111', 'pass', 'maja', 1);
INSERT INTO public.card_owner(id, email, last_name, merchantid, merchant_pass, name, card_id) VALUES (2, 'maja@gmail.com', 'firma', '222', 'pass', 'firma', 2);

INSERT INTO public.transaction(id, amount, buyer_pan, errorurl, failedurl, issuer_order_id, issuer_timestamp, merchant_order_id, merchant_timestamp, paymenturl, seller_pan, status, successurl, "timestamp", buyer_id, seller_id) VALUES (1111, 4234, '111111111', 'urlic', 'poyy', 1, '20198-09-09', '1', '2019-09-09', 'urlPaymenr', '222222222', 'N', 'URELSUCC', '2019-09-09', 1, 2);
INSERT INTO public.payment_info(paymentid, paymenturl, transaction_id) VALUES (1111, 'url', 1111);
