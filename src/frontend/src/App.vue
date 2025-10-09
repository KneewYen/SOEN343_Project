<script setup>
import { ref, onMounted } from 'vue'
import LoginView from './views/LoginView.vue'
import RegisterView from './views/RegisterView.vue'
import DashboardView from './views/DashboardView.vue'
import { useAuthStore } from './stores/authStore'

const authStore = useAuthStore()
const currentView = ref('login')
const isInitialized = ref(false)

// Initialize authentication on app start
onMounted(async () => {
  await authStore.initializeAuth()
  isInitialized.value = true
  
  // If user is authenticated, show dashboard
  if (authStore.isAuthenticated.value) {
    currentView.value = 'dashboard'
  }
})

// Handle view switching
const switchToRegister = () => {
  currentView.value = 'register'
}

const switchToLogin = () => {
  currentView.value = 'login'
}

const handleLoginSuccess = (user) => {
  console.log('Login successful:', user)
  currentView.value = 'dashboard'
}

const handleRegisterSuccess = (user) => {
  console.log('Registration successful:', user)
  currentView.value = 'dashboard'
}

const handleLogout = () => {
  authStore.signOut()
  currentView.value = 'login'
}
</script>

<template>
  <div id="app">
    <!-- Loading screen while initializing -->
    <div v-if="!isInitialized" class="loading-screen">
      <div class="loading-spinner"></div>
      <p>Loading RideWithUs...</p>
    </div>

    <!-- Main app content -->
    <div v-else>
      <!-- Login View -->
      <LoginView 
        v-if="currentView === 'login'"
        @switch-to-register="switchToRegister"
        @login-success="handleLoginSuccess"
      />

      <!-- Register View -->
      <RegisterView 
        v-if="currentView === 'register'"
        @switch-to-login="switchToLogin"
        @register-success="handleRegisterSuccess"
      />

      <!-- Dashboard View -->
      <DashboardView 
        v-if="currentView === 'dashboard'"
        @logout="handleLogout"
      />
    </div>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 50%, #ffb3c6 100%);
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  line-height: 1.6;
  color: #333;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  -webkit-text-size-adjust: 100%;
  -ms-text-size-adjust: 100%;
}

#app {
  min-height: 100vh;
  width: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
}

.loading-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 50%, #ffb3c6 100%);
  color: white;
  margin: 0;
  padding: 0;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-screen p {
  font-size: 1.2rem;
  font-weight: 500;
}
</style>
