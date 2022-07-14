INSERT INTO users (email, firstname, lastname, password)
VALUES ('testuser@test.de', 'Test', 'Testuser', 'password'),
       ('anothertestuser@test.de', 'Test', 'Testuser2', 'password'),
       ('trompell@th-brandenburg.de', 'Tobias', 'Trompell', 'password'),
       ('luedrick@th-brandenburg.de', 'Rick', 'Lüdicke', 'password'),
       ('zahn@th-brandenburg.de', 'Richard', 'Zahn', 'password');

/*
    Locations
 */

INSERT INTO location(id, latitude, longitude)
VALUES (10000, 13.4133, 52.5220);                   /* Berlin Alexanderplatz */
INSERT INTO location(id, latitude, longitude)
VALUES (10001, 13.3777, 52.5163);                   /* Brandenburg Gate */
INSERT INTO location(id, latitude, longitude)
VALUES (10002, 13.3762, 52.5186);                   /* Reichstag */
INSERT INTO location(id, latitude, longitude)
VALUES (10003, 13.3378, 52.5079);                   /* Berlin Zoo */
INSERT INTO location(id, latitude, longitude)
VALUES (10004, 13.4090, 52.5776);                   /* Park Pankow */
INSERT INTO location(id, latitude, longitude)
VALUES (10005, 12.5316, 52.4125);                   /* Brandenburg */

/*
    Meals
 */

INSERT INTO meal(id, picturepath, name, description, creator_email, location_id)
VALUES (1000, '1000', 'Dönerreste', 'Ein leckerer Döner am Alex', 'zahn@th-brandenburg.de', 10000),
       (1001, '1001', 'Vegetarische Pasta', 'Nur Biozutaten vom Brandenburger Tor', 'zahn@th-brandenburg.de', 10001),
       (1003, '1003', 'Gemüsecurry', 'mit Champignons, Blumenkohl und Reis', 'zahn@th-brandenburg.de', 10004),
       (1004, '1004', 'Kichererbsen-Spinat-Curry', 'saftiges Curry mit Sesam', 'zahn@th-brandenburg.de', 10003),
       (1005, '1005', 'Nudeln mit Grünkern', 'vegane Nudeln mit Grünkern nach Hackfleisch-Art', 'zahn@th-brandenburg.de', 10003),
       (1006, '1006', 'Pizza Margarita', 'selbstgemachte Pizza mit Tomaten und Basilikum', 'zahn@th-brandenburg.de', 10001),
       (1007, '1007', 'Sauerkrautauflauf', 'mit Reis und Räuchertofu', 'zahn@th-brandenburg.de', 10004),
       (1008, '1008', 'Cordon Bleu', 'mit Kartoffeln und Bratensoße', 'zahn@th-brandenburg.de', 10002);

INSERT INTO meal(id, name, description, creator_email, reservinguser_email, location_id)
VALUES (1002, 'Hamburger', 'Ein Hamburger in Brandenburg', 'zahn@th-brandenburg.de', 'trompell@th-brandenburg.de', 10005);

/*
    Properties
 */

INSERT INTO meal_properties(meal_id, properties)
VALUES (1000, 'NO_FISH'),
    (1000, 'NO_NUTS'),
    (1000, 'NO_PORK');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1001, 'VEGETARIAN'),
       (1001, 'NO_FISH'),
       (1001, 'NO_LACTOSE'),
       (1001, 'NOT_HOT'),
       (1001, 'NO_PORK'),
       (1001, 'SOY');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1002, 'NO_WHEAT'),
       (1002, 'NO_FISH'),
       (1002, 'PROTEIN'),
       (1002, 'NO_PORK');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1003, 'VEGAN'),
       (1003, 'NO_NUTS'),
       (1003, 'VEGETARIAN'),
       (1003, 'NO_FISH'),
       (1003, 'NO_PORK'),
       (1007, 'NO_WHEAT');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1004, 'VEGAN'),
       (1004, 'VEGETARIAN'),
       (1004, 'PROTEIN'),
       (1004, 'NO_FISH'),
       (1004, 'NO_PORK'),
       (1007, 'NO_WHEAT');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1005, 'VEGETARIAN'),
       (1005, 'NO_LACTOSE'),
       (1005, 'NOT_HOT'),
       (1005, 'NO_PORK');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1006, 'VEGETARIAN'),
       (1006, 'NO_NUTS'),
       (1006, 'NO_FISH'),
       (1006, 'NOT_HOT'),
       (1006, 'NO_PORK');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1007, 'VEGETARIAN'),
       (1007, 'VEGAN'),
       (1007, 'SOY'),
       (1007, 'NO_NUTS'),
       (1007, 'NO_FISH'),
       (1007, 'NO_WHEAT'),
       (1007, 'NOT_HOT');

INSERT INTO meal_properties(meal_id, properties)
VALUES (1008, 'NO_FISH'),
       (1008, 'NO_NUTS'),
       (1008, 'PROTEIN'),
       (1008, 'NOT_HOT');
