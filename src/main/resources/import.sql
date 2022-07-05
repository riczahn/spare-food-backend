INSERT INTO users (email, firstname, lastname, password)
VALUES ('testuser@test.de', 'Test', 'Testuser', 'password'),
       ('trompell@th-brandenburg.de', 'Tobias', 'Trompell', 'password'),
       ('luedrick@th-brandenburg.de', 'Rick', 'Lüdicke', 'password'),
       ('zahn@th-brandenburg.de', 'Richard', 'Zahn', 'password');

INSERT INTO meal(id, name, description, creator_email)
VALUES (1, 'Dönerreste', 'Ein leckerer Döner', 'zahn@th-brandenburg.de');
INSERT INTO meal(id, name, description, creator_email)
VALUES (2, 'Vegetarische Pasta', 'Nur Biozutaten', 'zahn@th-brandenburg.de');
INSERT INTO meal(id, name, description, creator_email, reservinguser_email)
VALUES (3, 'Hamburger', 'This meal is reserved by default', 'zahn@th-brandenburg.de', 'zahn@th-brandenburg.de');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1, 'NO_FISH');
INSERT INTO meal_properties(meal_id, properties)
VALUES (2, 'VEGETARIAN');
INSERT INTO meal_properties(meal_id, properties)
VALUES (2, 'NUTS');
INSERT INTO meal_properties(meal_id, properties)
VALUES (3, 'GLUTEN_FREE');
