# SpareFood Backend
The backend for the SpareFood Application developed at the University of Applied Sciences in Brandenburg a. d. Havel in 2022 in the context of the master degrees class of Mobile User Experience.

## How to run:
1. Install PostgreSQL
2. Adjust the user credentials and database name in the application.properties file to your needs
3. Run `./mvnw quarkus:dev` or on some linux systems `./mvnw sh quarkus:dev` or on windows `mvnw quarkus:dev`
4. Enjoy

## Using The API
* Every request requires a Bearer token. Generate a token at <url>/auth/generate-token and pass it with every request in the Authorization header

## Things that should be cleaned up if I have ever the time
* AssertJ and Hamcrest are mixed due to RESTAssured only supporting Hamcrest...
* Ambiguous namings. (create, add, persist) settle for one and only one.
* No hashing of passwords yet
* No good feedback when token expires
* No Feedback if request failed. We are leaving the user alone with their problem
* Very slow queries
