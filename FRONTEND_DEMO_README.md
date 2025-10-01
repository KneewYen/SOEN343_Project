# Frontend Demo - Login/Register/Dashboard

This is a frontend-only demo application with login, register, and dashboard functionality. Since there's no backend yet, authentication is bypassed for demonstration purposes.

## Features

### 🔐 Login Page (`/login`)
- Email and password input fields
- Form validation
- **Bypass Login button** - Click to skip authentication and go to dashboard
- Link to register page

### 📝 Register Page (`/register`)
- Full name, email, password, and confirm password fields
- Password confirmation validation
- **Bypass Register button** - Click to skip registration and go to dashboard
- Link to login page

### 📊 Dashboard Page (`/dashboard`)
- Welcome message with user information
- User profile display
- Quick action buttons
- Recent activity feed
- System status indicators
- Logout functionality
- Demo mode notice

## How to Use the Bypass Functionality

Since there's no backend integration yet, you can use the bypass buttons to simulate authentication:

1. **On Login Page**: Click "Bypass Login (Demo Mode)" button
2. **On Register Page**: Click "Bypass Register (Demo Mode)" button

Both buttons will:
- Store demo user data in localStorage
- Redirect you to the dashboard
- Display the user information on the dashboard

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── Login.vue          # Login page with bypass functionality
│   │   ├── Register.vue       # Registration page with bypass functionality
│   │   └── Dashboard.vue      # Main dashboard after login
│   ├── router/
│   │   └── index.js           # Vue Router configuration
│   ├── App.vue                # Main app component
│   └── main.js                # App entry point with router setup
```

## Backend Folder Structure

The backend has been organized with the following structure:

```
backend/src/main/java/org/example/backend/
├── application/
│   └── service/               # Business logic services
├── domain/
│   ├── model/                 # Domain entities
│   └── repository/            # Repository interfaces
├── infrastructure/
│   ├── config/                # Configuration classes
│   ├── persistence/           # Data persistence implementations
│   └── security/              # Security configurations
└── web/
    ├── controller/            # REST controllers
    ├── dto/                   # Data Transfer Objects
    ├── middleware/            # Custom middleware
    └── exception/             # Exception handlers
```

## Installation and Setup

### Prerequisites
- Node.js (version 20.19+ or 22.12+)
- npm or yarn package manager

### Frontend Setup

1. **Navigate to the frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```
   This will install all required dependencies including:
   - Vue 3
   - Vue Router
   - Vite (development server)
   - Other development dependencies

3. **Start the development server**:
   ```bash
   npm run dev
   ```
   The application will be available at `http://localhost:5173`

### Backend Setup (when implemented)

1. **Navigate to the backend directory**:
   ```bash
   cd backend
   ```

2. **Run the Spring Boot application**:
   ```bash
   mvn spring-boot:run
   ```

## Project Structure

The frontend follows a clean architecture pattern with the following layers:

```
frontend/src/
├── presentation/           # Presentation Layer
│   ├── components/         # Reusable UI components
│   ├── pages/             # Page components
│   │   ├── auth/          # Authentication pages
│   │   └── dashboard/     # Dashboard pages
│   └── layouts/           # Layout components
├── application/           # Application Layer
│   ├── services/          # Business logic services
│   └── use-cases/         # Use case implementations
├── domain/                # Domain Layer
│   ├── entities/          # Domain entities
│   └── repositories/      # Repository interfaces
└── infrastructure/        # Infrastructure Layer
    ├── storage/           # Data storage implementations
    └── api/              # API clients
```

## Dependencies

The project uses the following main dependencies:

- **Vue 3**: Frontend framework
- **Vue Router 4**: Client-side routing
- **Vite**: Build tool and development server

All dependencies are listed in `package.json` and will be installed when you run `npm install`.

## Demo Flow

1. Start at the login page (`/`)
2. Click "Bypass Login (Demo Mode)" or "Bypass Register (Demo Mode)"
3. You'll be redirected to the dashboard
4. The dashboard shows your demo user information
5. Use the logout button to return to the login page

## Future Backend Integration

When the backend is ready, the bypass functionality can be easily replaced with actual API calls to:
- `/api/auth/login` for authentication
- `/api/auth/register` for user registration
- `/api/user/profile` for user data

The frontend is designed to be easily integrated with a Spring Boot backend using the existing folder structure.
