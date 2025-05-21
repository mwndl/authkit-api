# AuthKit API

## Description

AuthKit API is a robust and modular authentication template built with Spring Boot. It provides a complete foundation for secure user authentication and authorization, ideal for starting new projects with ready-to-use authentication features.

This project includes support for JWT (access and refresh tokens), password reset flows, and Two-Factor Authentication (2FA) using TOTP.

## Main Features

-   User registration and login
-   JWT authentication with access and refresh tokens
-   Secure password reset via email (token-based)
-   Two-Factor Authentication (2FA) with TOTP (Time-based One-Time Password)
-   Session management and token refresh
-   Centralized error handling with custom messages

## Technologies

-   Java 21
-   Spring Boot
-   Spring Web
-   Spring Security
-   Spring Data JPA
-   MySQL
-   JWT (JSON Web Tokens)
-   2FA TOTP

## How to Run Locally

1. Clone the repository:
    ```bash
    git clone https://github.com/mwndl/authkit-api.git
    cd authkit-api
    ```

2. Configure the `application.properties` or `application.yml` file with your database credentials.

3. Run the app via your IDE or with:
    ```bash
    ./mvnw spring-boot:run
    ```

4. The API will be available at:
    ```
    http://localhost:8080
    ```

## Project Structure

-   `controller`: REST API endpoints
-   `service`: business logic
-   `model`: JPA entities
-   `repository`: data access layer
-   `dto`: data transfer objects
-   `exception`: centralized error handling
-   `security`: authentication and authorization config

## Key Endpoints

All available endpoints, including request/response formats and authentication details, are documented in Swagger UI:

👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> Make sure the application is running locally before accessing.

## Author

This project is developed by **Marcos Wiendl**.  
For suggestions, feedback, or contributions, feel free to:

-   Open an issue or submit a pull request
-   Connect on [LinkedIn – Marcos Wiendl](https://www.linkedin.com/in/marcoswiendl)
