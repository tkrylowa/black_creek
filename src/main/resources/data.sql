INSERT INTO user_role (role_id, role_name)
VALUES (1, 'ROLE_REGISTERED_USER');

INSERT INTO public.users
(logged_in, created_at, role_id, type_id, updated_at, user_id, confirm_password, created_by, email, first_name, last_name, login, "password", token_expiration, updated_by, user_token)
VALUES(false, NULL, 1, NULL, NULL, 0, '$2a$10$72Yh77qhsJCw3p/AIBBXG.2KuPCoeQ3/iTPLq9Z4aNZWeXUZigfEC', NULL, 'test@mail.com', 'test', 'tset', 'login12345678', '$2a$10$3BVK.WcO28uL4IJvSOEofupESuhC6idwXwTnojLedtptbk8IEaDw2', NULL, NULL, NULL);

INSERT INTO public.events
("cost", event_capacity, event_end_date, event_start_date, is_cancelled, is_free, created_at, event_id, event_type_id, updated_at, user_id, event_name, created_by, event_description, updated_by)
VALUES(NULL, 11, NULL, NULL, false, false, NULL, 0, NULL, NULL, NULL, 'wqfwqfgqwgfqwgfttq', NULL, '1qwaergtegegye3geq33g3qg', NULL);