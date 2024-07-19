# Ticket management application server #
This application helps users to manage users tasks in form of tickets.
A ticket have informations (title, description, creator user id, status), and can be assigned to a user.

## Preparing your environment ##
- Java version : **openjdk 21.0.2**
- Build the application with gradle : **.\gradlew clean build**
- Run the application with gradle : **.\gradlew bootRun**
- Server port : **8080**
- IDE used : IntelliJ Community

## Access ##
- Some data are created for tests during spring boot startup through **data.sql**
- H2 access :
  - URL : http://localhost:8080/h2-console/login.jsp
  - Jdbc URL : **jdbc:h2:mem:test**
  - Username : **sa**
  - Password : **password**
  - Check the initial data
    - select * from "user";
    - select * from "ticket";
- Open API : http://localhost:8080/api-docs
- Swagger UI : http://localhost:8080/swagger-ui/index.htm