INSERT INTO users (email, firstname, lastname, password)
VALUES ('testuser@test.de', 'Test', 'Testuser', 'password'),
       ('trompell@th-brandenburg.de', 'Tobias', 'Trompell', 'password'),
       ('luedrick@th-brandenburg.de', 'Rick', 'Lüdicke', 'password'),
       ('zahn@th-brandenburg.de', 'Richard', 'Zahn', 'password');

INSERT INTO location(id, latitude, longitude)
VALUES (10000, 13.4133, 52.5220);                               /* Berlin Alexanderplatz */
INSERT INTO location(id, latitude, longitude)
VALUES (10001, 13.3777, 52.5163);                               /* Brandenburg Gate */
INSERT INTO location(id, latitude, longitude)
VALUES (10002, 12.5316, 52.4125);                               /* Brandenburg */

INSERT INTO meal(id, name, description, creator_email, location_id)
VALUES (1000, 'Dönerreste', 'Ein leckerer Döner am Alex', 'zahn@th-brandenburg.de', 10000);
INSERT INTO meal(id, name, description, creator_email, location_id)
VALUES (1001, 'Vegetarische Pasta', 'Nur Biozutaten vom Brandenburger Tor', 'zahn@th-brandenburg.de', 10001);
INSERT INTO meal(id, name, description, creator_email, reservinguser_email, location_id)
VALUES (1002, 'Hamburger', 'Ein Hamburger in Brandenburg', 'zahn@th-brandenburg.de', 'trompell@th-brandenburg.de', 10002);

INSERT INTO meal_properties(meal_id, properties)
VALUES (1000, 'NO_FISH');
INSERT INTO meal_properties(meal_id, properties)
VALUES (1001, 'VEGETARIAN');
INSERT INTO meal_properties(meal_id, properties)
VALUES (1001, 'NUTS');
INSERT INTO meal_properties(meal_id, properties)
VALUES (1002, 'GLUTEN_FREE');
