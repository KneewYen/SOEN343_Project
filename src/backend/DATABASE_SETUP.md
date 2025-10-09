# Database Setup Guide

## Overview
Your Spring Boot application is now configured with a common database setup using PostgreSQL and JPA/Hibernate.

## Configuration Files

### 1. Application Configuration
- `application.yml` - Main configuration file
- `application-dev.yml` - Development environment settings
- `application-prod.yml` - Production environment settings

### 2. Database Configuration
- `DatabaseConfig.java` - Database connection configuration
- `SupabaseConfig.java` - Supabase integration configuration

## Database Entities

### User Entity
- **Fields**: id, name, email, phone, userType, isActive, timestamps
- **User Types**: DRIVER, PASSENGER, BOTH
- **Relationships**: One-to-many with rides (as driver and passenger)

### Ride Entity
- **Fields**: id, driver, passenger, pickupLocation, destination, fare, status, timestamps
- **Ride Status**: REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
- **Relationships**: Many-to-one with users (driver and passenger)

## Repositories

### UserRepository
- Find by email, phone, user type
- Find available drivers
- Check existence by email/phone

### RideRepository
- Find by driver, passenger, status
- Find active rides
- Find rides by fare/distance range

## Services

### UserService
- CRUD operations for users
- Business logic for user management
- Validation for email/phone uniqueness

## Controllers

### UserController
- REST endpoints for user operations
- CORS enabled for frontend integration
- Health check endpoint

## Environment Variables

Create a `.env` file with:
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/ridewithus
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_SERVICE_ROLE_KEY=your-service-role-key
SUPABASE_ANON_KEY=your-anon-key
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
SPRING_PROFILES_ACTIVE=dev
```

## Running the Application

### Development Mode
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Mode
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## API Endpoints

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/type/{userType}` - Get users by type
- `GET /api/users/drivers/available` - Get available drivers
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/health` - Health check

## Database Setup

### PostgreSQL Setup
1. Install PostgreSQL
2. Create database: `CREATE DATABASE ridewithus;`
3. Update connection details in `application.yml`

### H2 Database (Testing)
- Automatically configured for testing
- Access console at: `http://localhost:8080/api/h2-console`

## Next Steps

1. **Set up PostgreSQL database**
2. **Configure environment variables**
3. **Test the API endpoints**
4. **Add more entities as needed**
5. **Implement authentication/authorization**
6. **Add more business logic services**

## Dependencies Added

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- PostgreSQL Driver
- H2 Database (for testing)
- Apache HttpComponents 5
- Jackson for JSON processing


