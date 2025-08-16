
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('VENDOR');

Insert into users (email,password, firstname,lastname,address,enabled) values ('wisdommaliki@gmail.com','$2a$10$3.fE9tK9bcOtmUU59bdCyOEmVAORSLtlC24mHh9Qi1hz37qGDOdYy','Wisdom','Maliki',
'21820 E briawood dr Aurora Colorado',true),('jane.smith@example.com', '$2a$10$Ls3Ms1FknWQqpAxhSNNno.tL6O/KXt9uMU7LcDiyt6NWdq4XUQDDm', 'Jane', 'Smith', '', true);

--assign roles to wisdom and john
Insert into user_roles (user_id,role_id) values (1,1),(1,2),(2,2);

INSERT INTO categories (name, slug, parent_id) VALUES
('Men', 'men', NULL),                                -- id 1
('Men / Shoes', 'men-shoes', 1),                     -- id 2
('Men / Pants', 'men-pants', 1),                     -- id 3
('Women', 'women', NULL),                            -- id 4
('Women / Shoes', 'women-shoes', 4);                 -- id 5






