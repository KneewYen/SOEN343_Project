# Supabase Frontend Setup Complete! 🚀

## ✅ What's Been Set Up

### 1. **Supabase Client Configuration**
- ✅ Installed `@supabase/supabase-js` package
- ✅ Created `src/lib/supabase.js` with your project configuration
- ✅ Configured with your URL: `https://hpgawptoeiwdajfrqvvz.supabase.co`
- ✅ Added your anon key for client-side operations

### 2. **API Client for Backend Communication**
- ✅ Created `src/lib/api.js` for backend API calls
- ✅ Configured to work with your Spring Boot backend at `http://localhost:8080/api`
- ✅ Includes timeout handling and error management

### 3. **Configuration Management**
- ✅ Created `src/lib/config.js` for centralized configuration
- ✅ Environment-specific settings for development/production

### 4. **Vite Configuration**
- ✅ Updated `vite.config.js` with environment handling
- ✅ Added proxy configuration for API calls
- ✅ Configured build settings for different environments

### 5. **Test Component**
- ✅ Created `SupabaseTest.vue` component to verify connections
- ✅ Tests both Supabase and backend API connectivity
- ✅ Added to your main App.vue for easy testing

## 🔧 Your Supabase Configuration

```javascript
// Supabase URL
https://hpgawptoeiwdajfrqvvz.supabase.co

// Anon Key (for client-side)
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhwZ2F3cHRvZWl3ZGFqZnJxdnZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5MDEwOTYsImV4cCI6MjA3NTQ3NzA5Nn0.xpD8QznbSLADZU7kUyDFn_4cEJvGeCrG9fKO-LrXKJY
```

## 🚀 How to Use

### 1. **Start Your Frontend**
```bash
cd /Users/nguyenle/Documents/SOEN343/RideWithUs/SOEN343_Project/src/frontend
npm run dev
```

### 2. **Test the Setup**
- Open `http://localhost:5173` in your browser
- You'll see the SupabaseTest component that will:
  - ✅ Test Supabase connection
  - ✅ Test backend API connection
  - ✅ Show configuration details

### 3. **Use Supabase in Your Components**
```vue
<script setup>
import { supabase, supabaseHelpers } from '@/lib/supabase'

// Get all users
const users = await supabaseHelpers.getUsers()

// Create a new user
const newUser = await supabaseHelpers.createUser({
  name: 'John Doe',
  email: 'john@example.com',
  userType: 'PASSENGER'
})
</script>
```

### 4. **Use Backend API in Your Components**
```vue
<script setup>
import { apiClient } from '@/lib/api'

// Get users from your Spring Boot backend
const users = await apiClient.getUsers()

// Create a user via backend
const newUser = await apiClient.createUser({
  name: 'Jane Doe',
  email: 'jane@example.com',
  userType: 'DRIVER'
})
</script>
```

## 📁 File Structure

```
src/
├── lib/
│   ├── supabase.js      # Supabase client & helpers
│   ├── api.js           # Backend API client
│   └── config.js        # Configuration utility
├── components/
│   └── SupabaseTest.vue # Test component
└── App.vue              # Updated with test component
```

## 🔑 Available Supabase Operations

### User Operations
- `getUsers()` - Get all users
- `getUserById(id)` - Get user by ID
- `createUser(userData)` - Create new user
- `updateUser(id, userData)` - Update user
- `deleteUser(id)` - Delete user

### Ride Operations
- `getRides()` - Get all rides with user details
- `getRideById(id)` - Get ride by ID
- `createRide(rideData)` - Create new ride
- `updateRide(id, rideData)` - Update ride

### Authentication (Ready for use)
- `signUp(email, password, userData)` - User registration
- `signIn(email, password)` - User login
- `signOut()` - User logout
- `getCurrentUser()` - Get current user

## 🎯 Next Steps

1. **Create Database Tables** in Supabase dashboard
2. **Test the connections** using the test component
3. **Build your Vue components** using the provided helpers
4. **Set up authentication** when ready
5. **Deploy your application** when complete

## 🐛 Troubleshooting

### If Supabase connection fails:
- Check your internet connection
- Verify the URL and API key are correct
- Make sure your Supabase project is active

### If Backend API fails:
- Make sure your Spring Boot backend is running on port 8080
- Check that the backend has the `/api/users/health` endpoint
- Verify CORS settings in your backend

### If you see build errors:
- Run `npm install` to ensure all dependencies are installed
- Check that your Node.js version is compatible (20.19.0+)

## 🎉 You're All Set!

Your Vue.js frontend is now fully configured to work with:
- ✅ Supabase for database operations
- ✅ Your Spring Boot backend for API calls
- ✅ Proper environment configuration
- ✅ Error handling and testing tools

Start building your ride-sharing app! 🚗💨
