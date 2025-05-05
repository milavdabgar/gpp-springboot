# GPP Portal Backend

This is the backend for the Government Polytechnic Palanpur Portal, implemented in Spring Boot with PostgreSQL as the database.

## Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL 12 or higher

## Project Setup

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/gpp-portal-backend.git
cd gpp-portal-backend
```

### 2. Configure database

Create a PostgreSQL database:

```sql
CREATE DATABASE gpp_portal;
CREATE USER portal_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE gpp_portal TO portal_user;
```

### 3. Configure application.properties

Update the `application.properties` file with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gpp_portal
spring.datasource.username=portal_user
spring.datasource.password=your_password
```

### 4. Build and run the application

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Or run with a specific profile:

```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

## API Documentation

API documentation is available through Swagger UI at:

```
http://localhost:9000/api/swagger-ui
```

## Authentication

The application uses JWT for authentication. To get a token, use the `/auth/login` endpoint with your credentials.

Example request:

```json
{
  "email": "admin@gppalanpur.in",
  "password": "Admin@123"
}
```

The default admin credentials are:
- Email: admin@gppalanpur.in
- Password: Admin@123

## API Structure

The API follows RESTful principles and is structured around the following resources:

- `/auth`: Authentication and user roles
- `/users`: User profile management
- `/admin`: Admin operations (user management, roles)
- `/departments`: Department management
- `/faculty`: Faculty management
- `/students`: Student management
- `/results`: Student results management
- `/projects`: Project management (events, teams, locations)

## Development

### Running with dev profile

```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

The dev profile includes:

- Enable H2 console at http://localhost:9000/api/h2-console
- More detailed logging
- Automatic database schema updates

## Deployment

### Running with prod profile

```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

The prod profile includes:

- Optimized database connection pool
- Reduced logging verbosity
- Enhanced security settings

## Database Migrations

This project uses Flyway for database migrations. Migration scripts are located in `src/main/resources/db/migration`.

To manually run migrations:

```bash
./mvnw flyway:migrate
```

## License

MIT