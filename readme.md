
# FINAPP - Investment Goal Tracker

## Description

Finapp is a financial management application that allows users to organize their investments into "fund boxes" (goal-based savings or investment buckets). The project is built with Spring Boot on the backend and is designed to integrate with a mobile app. Users can manage deposits with collaborative financial goal support.

## Main Features

-   Create collaborative find boxes by inviting other users

-   User registration, authentication, and session management with JWT and refresh tokens

-   Create, edit, view, and delete fund boxes

-   Request password reset via email (token-based)

-   Add deposits (entries and exits)

-   Real-time fund box balance calculation

-   Automatic deposit unlinking when a fund box is deleted

-   Centralized error handling with custom messages


## Technologies

-   Java 21

-   Spring Boot

-   Spring Web

-   Spring Security

-   Spring Data JPA

-   MySQL

-   JWT (JSON Web Tokens)


## How to Run Locally

1.  Clone the repository

2.  Configure the `application.properties` file with your database credentials

3.  Run the app via your IDE or use `./mvnw spring-boot:run`

4.  The API will be available at `http://localhost:8080`


## Main Project Structure

-   `controller`: REST endpoints

-   `service`: business logic

-   `model`: JPA entities

-   `repository`: data access interfaces

-   `dto`: data transfer objects

-   `exception`: centralized error handling


## Business Rules

-   Each user can create multiple fund boxes, each with a unique name

-   A deposit can be linked or unlinked from a fund box

-   When a fund box is deleted, its deposits remain but are unlinked

-   Users can only access and manage their own data


## Key Endpoints

All available endpoints, request/response formats, and authentication details can be found in the Swagger UI:

👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> Make sure the application is running locally before accessing.

## Planned Features

-   Transform deposits into fixed income investments

-   Exportable financial reports


## Author

This project is developed by **Marcos Wiendl**.  
For suggestions, feedback, or contributions, feel free to:

-   Open an issue or submit a pull request

-   Connect on [LinkedIn – Marcos Wiendl](https://www.linkedin.com/in/marcoswiendl)