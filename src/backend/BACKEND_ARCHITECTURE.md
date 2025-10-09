# Backend Architecture - Layered Structure

This document describes the layered architecture implementation for the RideWithUs backend application.

## Architecture Overview

The backend follows a layered architecture pattern with clear separation of concerns:

```
src/main/java/org/example/
├── ui/                          # 1.2 UI and Presentation Layer
│   ├── controller/              # REST Controllers
│   └── dto/                     # Data Transfer Objects
├── presentation/                # 1.3 Application Layer (State Management)
│   └── stores/                  # Reserved for future state management
├── application/                 # 1.4 Domain Layer (Business Logic)
│   └── service/                 # Business logic services
├── domain/                      # 1.5 Persistence Layer / Infrastructure Layer
│   ├── entity/                  # JPA Entities
│   └── repository/              # Data Access Layer
├── infrastructure/              # Infrastructure Layer
│   └── config/                  # Configuration classes
└── Main.java                    # Application entry point
```

## Layer Descriptions

### 1.2 UI and Presentation Layer (`/ui/`)
- **Purpose**: Handles HTTP requests and responses, API endpoints
- **Contains**:
  - `controller/`: REST Controllers for API endpoints
  - `dto/`: Data Transfer Objects for API communication
- **Responsibilities**:
  - Handling HTTP requests
  - Request/response mapping
  - Input validation
  - API documentation

### 1.3 Application Layer (`/presentation/`)
- **Purpose**: Manages application state and coordinates between UI and business logic
- **Contains**:
  - `stores/`: Reserved for future state management components
- **Responsibilities**:
  - Application state coordination
  - Transaction management
  - Cross-cutting concerns

### 1.4 Domain Layer (`/application/`)
- **Purpose**: Contains business logic and application services
- **Contains**:
  - `service/`: Business logic services
- **Responsibilities**:
  - Implementing business rules
  - Processing business logic
  - Coordinating between controllers and repositories
  - Managing business workflows

### 1.5 Persistence Layer / Infrastructure Layer (`/domain/`)
- **Purpose**: Handles data persistence and domain models
- **Contains**:
  - `entity/`: JPA Entities representing domain models
  - `repository/`: Data Access Layer (Spring Data JPA repositories)
- **Responsibilities**:
  - Data persistence
  - Database operations
  - Domain model definitions
  - Data access patterns

### Infrastructure Layer (`/infrastructure/`)
- **Purpose**: Handles external integrations and configuration
- **Contains**:
  - `config/`: Configuration classes
- **Responsibilities**:
  - Database configuration
  - External service configuration
  - Security configuration
  - Application properties

## Package Structure Examples

```java
// UI Layer
package org.example.ui.controller;
package org.example.ui.dto;

// Application Layer
package org.example.application.service;

// Domain Layer
package org.example.domain.entity;
package org.example.domain.repository;

// Infrastructure Layer
package org.example.infrastructure.config;
```

## Benefits of This Architecture

1. **Separation of Concerns**: Each layer has a specific responsibility
2. **Maintainability**: Easy to locate and modify specific functionality
3. **Testability**: Each layer can be tested independently
4. **Scalability**: Easy to add new features without affecting other layers
5. **Dependency Management**: Clear dependency flow from UI to Infrastructure
6. **Spring Boot Integration**: Leverages Spring Boot's component scanning and dependency injection

## Current Implementation

- **Controllers**: `UserController` handles user-related API endpoints
- **Services**: `UserService` contains business logic for user operations
- **Entities**: `User` and `Ride` entities represent domain models
- **Repositories**: `UserRepository` and `RideRepository` handle data access
- **Configuration**: Database and Supabase configuration classes

## Future Considerations

- Add DTOs for better API contract management
- Implement proper exception handling across layers
- Add validation layers
- Consider adding a mapper layer for entity-DTO conversion
- Add security configuration in infrastructure layer
