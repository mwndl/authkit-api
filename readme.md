# AuthKit API

## Description

AuthKit API is a robust and modular authentication template built with Spring Boot. It provides a complete foundation for secure user authentication and authorization, ideal for starting new projects with ready-to-use authentication features.

This project includes support for JWT (access and refresh tokens), password reset flows, and Two-Factor Authentication (2FA) using TOTP.

## Main Features

-   User registration and login
-   JWT authentication with access and refresh tokens
-   Email verification with token and code-based methods
-   Secure password reset via email (token-based)
-   Two-Factor Authentication (2FA) with TOTP (Time-based One-Time Password)
-   Session management and token refresh
-   Centralized error handling with custom messages
-   Real-time notifications via WebSocket

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

## Email Verification

The API provides two methods for email verification:

1. **Token-based Verification**
   - When a user registers, a verification email is sent with a JWT token
   - The token contains the user's email and expires in 24 hours
   - Users can click the link in the email

2. **Code-based Verification**
   - The verification email also includes a 6-character alphanumeric code
   - Users can verify their email using this code
   - Requires authentication (valid access token)

3. **Resend Verification**
   - Users can request a new verification email
   - Rate-limited to prevent abuse
   - Requires authentication (valid access token)

### Security Features

- Tokens are single-use and expire after 24 hours
- Rate limiting on resend attempts
- Protection against already verified accounts
- JWT validation and error handling
- Secure token generation and validation

## Key Endpoints

All available endpoints, including request/response formats and authentication details, are documented in Swagger UI:

👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> Make sure the application is running locally before accessing.

## WebSocket Notifications

The API supports real-time notifications through WebSocket connections, allowing frontend applications to receive instant updates without polling. This is particularly useful for:

- Real-time authentication status updates
- Session expiration notifications
- Security alerts
- System-wide announcements

You can receive notifications in two ways:

1. **WebSocket (Real-time)**
   Connect to the WebSocket endpoint for instant updates:
   ```
   ws://localhost:8080/api/ws/notifications
   ```
   The WebSocket connection requires a valid JWT token for authentication, which should be provided in the connection URL as a query parameter.

2. **REST API (Polling)**
   Alternatively, you can fetch notifications using the standard GET endpoint:
   ```
   GET /api/v1/notifications
   ```
   This endpoint supports pagination and filtering, making it suitable for applications that prefer traditional HTTP requests.

## Author

This project is developed by **Marcos Wiendl**.  
For suggestions, feedback, or contributions, feel free to:

-   Open an issue or submit a pull request
-   Connect on [LinkedIn – Marcos Wiendl](https://www.linkedin.com/in/marcoswiendl)
