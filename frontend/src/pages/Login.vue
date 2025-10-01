<template>
  <div class="login-page">
    <ThemeToggle />
    
    <div class="login-card">
      <h1>🚴‍♂️ Bike Rental System</h1>
      
      <form @submit.prevent="login">
        <input 
          type="email" 
          v-model="email" 
          placeholder="Email" 
          required 
        />
        <input 
          type="password" 
          v-model="password" 
          placeholder="Password" 
          required 
        />
        <button type="submit">Login</button>
      </form>
      
      <div class="demo-section">
        <p>Demo Mode:</p>
        <button @click="demoLogin" class="demo-btn">Demo Login</button>
      </div>
      
      <p class="register-link">
        Don't have an account? 
        <router-link to="/register">Register here</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import ThemeToggle from '../components/ThemeToggle.vue'

const router = useRouter()
const email = ref('')
const password = ref('')

const login = () => {
  // Simple login - will be handled by backend
  console.log('Login:', email.value, password.value)
  alert('Login functionality will be implemented with backend')
}

const demoLogin = () => {
  localStorage.setItem('user', JSON.stringify({
    name: 'Demo User',
    email: email.value || 'demo@example.com',
    type: 'user'
  }))
  router.push('/dashboard')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
}

.login-card {
  background: var(--white);
  padding: 48px;
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  width: 100%;
  max-width: 400px;
  text-align: center;
  border: 1px solid var(--grey-200);
  position: relative;
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-red), var(--dark-red));
}

h1 {
  margin-bottom: 32px;
  color: var(--grey-800);
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

input {
  width: 100%;
  margin-bottom: 20px;
  border: 2px solid var(--grey-300);
  background: var(--white);
  color: var(--grey-800);
  font-size: 16px;
  padding: 16px 20px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

input:focus {
  border-color: var(--primary-red);
  box-shadow: 0 0 0 4px rgba(220, 53, 69, 0.1);
  transform: translateY(-1px);
}

button {
  width: 100%;
  padding: 16px 24px;
  background: var(--primary-red);
  color: var(--white);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

button:hover {
  background: var(--dark-red);
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

button:active {
  transform: translateY(0);
}

.demo-section {
  margin: 24px 0;
  padding: 16px;
  background: var(--grey-100);
  border-radius: 8px;
  border: 2px dashed var(--grey-300);
}

.demo-section p {
  margin: 0 0 12px 0;
  color: var(--grey-600);
  font-weight: 500;
}

.demo-btn {
  background: var(--grey-600);
  color: var(--white);
  font-size: 14px;
  padding: 12px 20px;
}

.demo-btn:hover {
  background: var(--grey-700);
}

.register-link {
  margin-top: 24px;
  color: var(--grey-600);
}

.register-link a {
  color: var(--primary-red);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.register-link a:hover {
  color: var(--dark-red);
  text-decoration: underline;
}

/* Dark theme */
.dark-theme .login-card {
  background: var(--grey-800);
  border-color: var(--grey-600);
  color: var(--white);
}

.dark-theme h1 {
  color: var(--white);
}

.dark-theme input {
  background: var(--grey-700);
  color: var(--white);
  border-color: var(--grey-600);
}

.dark-theme input:focus {
  border-color: var(--primary-red);
  box-shadow: 0 0 0 4px rgba(220, 53, 69, 0.2);
}

.dark-theme .demo-section {
  background: var(--grey-700);
  border-color: var(--grey-600);
}

.dark-theme .demo-section p {
  color: var(--grey-300);
}

.dark-theme .register-link {
  color: var(--grey-300);
}

/* Responsive */
@media (max-width: 768px) {
  .login-page {
    padding: 16px;
  }
  
  .login-card {
    padding: 32px 24px;
  }
  
  h1 {
    font-size: 24px;
  }
}
</style>