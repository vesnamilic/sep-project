INSERT INTO client("email", "client_id", "client_secret") VALUES ('mail@gmail.com', 'ovVP3S3wBDjLU7baJZj6kYhzqnp68XlxtXWVtFsR1wLP8lX3r9rdGJU6nDGLZeSI6yBE83pDRySRnGCfi3rxRwuBN6DxQiQq5b3RaSxU93inaPpnRikoDjHBXLycH12r', '/tKWaOT2Ue8OBmeKoyor9hD9mLYCnWQEgr9neqOeeUO3JJGeGLy2Cyl0c4gfPG8JTgmp/fI8GaieKI++1CIOktuuIoQVMJdEHzq+8KUyyKynaPpnRikoDjHBXLycH12r');

INSERT INTO billing_plan("billing_plan_id", "payment_amount", "payment_currency", "frequency", "type") VALUES ('P-8Y094631U7729082MY726GNQ', '5', 'USD', 'WEEK', 'INFINITE');

INSERT INTO client_billing_plans("client_id", "billing_plans_id") VALUES (1, 1);