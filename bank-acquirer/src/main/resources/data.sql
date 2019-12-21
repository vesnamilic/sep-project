INSERT INTO public.card(id, account_number, available_funds, cvv, exp_date, pan, reserved_funds) VALUES (1, 'XMjnrNCBsDnUXS0ELJ/uqQ==', 200000, 'jTid9klSgMBm56p10HPspA==', '9/2019', 'Dj11Nsi4H8lNgNvztgAHiA==', 0);
INSERT INTO public.card(id, account_number, available_funds, cvv, exp_date, pan, reserved_funds) VALUES (2, 'QgqWzTtiRws+kvnYKVAMZQ==', 200000, '8iwFYp7n/K3Pg+vFHMz86g==', '9/2019', 'At6rtSo1ZjTgEA/01axLVQ==', 0);

INSERT INTO public.card_owner(id, email, last_name, merchantid, merchant_pass, name, card_id) VALUES (1, 'maja@gmail.com', 'matkovski', 'iVCA15Fsvut7uPzgkSXHIg==','0e+mJMVaD6P6yRU675fViQ==', 'maja', 1);
INSERT INTO public.card_owner(id, email, last_name, merchantid, merchant_pass, name, card_id) VALUES (2, 'maja@gmail.com', 'firma', 'w2xR8XbjoNIfVAThjr5vqA==', '0e+mJMVaD6P6yRU675fViQ==', 'firma', 2);

INSERT INTO public.transaction(id, amount, buyer_pan, errorurl, failedurl, issuer_order_id, issuer_timestamp, merchant_order_id, merchant_timestamp, paymenturl, seller_pan, status, successurl, "timestamp", buyer_id, seller_id) VALUES (1111, 4234, 'Dj11Nsi4H8lNgNvztgAHiA==', 'urlic', 'poyy', 1, '20198-09-09', '1', '2019-09-09', 'urlPaymenr', 'At6rtSo1ZjTgEA/01axLVQ==', 'UNSUCCESSFULLY', 'URELSUCC', '2019-09-09', 1, 2);
INSERT INTO public.payment_info(paymentid, paymenturl, transaction_id) VALUES (1111, 'url', 1111);