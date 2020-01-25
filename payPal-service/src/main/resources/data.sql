INSERT INTO client("email", "client_id", "client_secret") VALUES ('mail@gmail.com', '3GKHevsTQIrOJ0DgFz6eC0HaWh3MoxYcs2tTfMeb4P029OpiQY6QHg3qVSfR0JJjFZYoLYhuiTj3S7tWStthi4NPrA2F/WrpILnZ5abqWmkbSB4e6EaIyO4hQ48pF4Ha', '35wLipgTdYKx6Ho6UbDknlmdyvbu25l+Opm54mZXNKjWEVR1et8RU0s5VsXOCmGY1T7Hsm4v7CanNODgq6WWeatfn25y2IN29sVSg+7KvtdcCCRr70fa39Mof7YBuNAq');

INSERT INTO billing_plan("billing_plan_id", "payment_amount", "payment_currency", "frequency", "type") VALUES ('P-8Y094631U7729082MY726GNQ', '5', 'USD', 'WEEK', 'INFINITE');

INSERT INTO client_billing_plans("client_id", "billing_plans_id") VALUES (1, 1);

INSERT INTO crypto("id", "iv", "text") VALUES (1, 'nnrE242YiqTJWQYi', '3GKHevsTQIrOJ0DgFz6eC0HaWh3MoxYcs2tTfMeb4P029OpiQY6QHg3qVSfR0JJjFZYoLYhuiTj3S7tWStthi4NPrA2F/WrpILnZ5abqWmkbSB4e6EaIyO4hQ48pF4Ha');
INSERT INTO crypto("id", "iv", "text") VALUES (2, 'WDMWtVxdQh8xAj81', '35wLipgTdYKx6Ho6UbDknlmdyvbu25l+Opm54mZXNKjWEVR1et8RU0s5VsXOCmGY1T7Hsm4v7CanNODgq6WWeatfn25y2IN29sVSg+7KvtdcCCRr70fa39Mof7YBuNAq');
