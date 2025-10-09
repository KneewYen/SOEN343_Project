<template>
  <div class="login-container">
    <div class="login-card">
      <div class="logo-section">
        <h1 class="app-title">RideWithUs</h1>
        <p class="app-subtitle">Your journey, our priority</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <h2 class="form-title">Welcome Back</h2>
        
        <div class="form-group">
          <label for="email" class="form-label">Email</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            class="form-input"
            placeholder="Enter your email"
            required
          />
        </div>

        <div class="form-group">
          <label for="password" class="form-label">Password</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            class="form-input"
            placeholder="Enter your password"
            required
          />
        </div>

        <button type="submit" class="login-btn" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          {{ loading ? 'Signing In...' : 'Sign In' }}
        </button>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <div class="form-footer">
          <p>Don't have an account? 
            <button type="button" @click="$emit('switch-to-register')" class="link-btn">
              Sign Up
            </button>
          </p>
        </div>
      </form>
    </div>

    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { supabase } from '@/lib/supabase'

const emit = defineEmits(['switch-to-register', 'login-success'])

const loading = ref(false)
const error = ref('')

const form = reactive({
  email: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  error.value = ''

  try {
    const { data, error: authError } = await supabase.auth.signInWithPassword({
      email: form.email,
      password: form.password
    })

    if (authError) {
      throw authError
    }

    if (data.user) {
      emit('login-success', data.user)
    }
  } catch (err) {
    error.value = err.message || 'Login failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 50%, #ffb3c6 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
  margin: 0;
}

.login-card {
  background: white;
  border-radius: 20px;
  padding: 50px;
  box-shadow: 0 20px 40px rgba(255, 107, 157, 0.2);
  width: 95%;
  max-width: 500px;
  position: relative;
  z-index: 10;
  margin: 0 auto;
}

.logo-section {
  text-align: center;
  margin-bottom: 30px;
}

.app-title {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b9d, #ff8fab);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.app-subtitle {
  color: #666;
  font-size: 1rem;
  margin: 5px 0 0 0;
}

.form-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
  font-size: 0.9rem;
}

.form-input {
  width: 100%;
  padding: 15px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #ff6b9d;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.login-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #ff6b9d, #ff8fab);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(255, 107, 157, 0.3);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  background: #ffe6e6;
  color: #d63384;
  padding: 12px;
  border-radius: 8px;
  margin-top: 15px;
  font-size: 0.9rem;
  border-left: 4px solid #d63384;
}

.form-footer {
  text-align: center;
  margin-top: 25px;
  color: #666;
}

.link-btn {
  background: none;
  border: none;
  color: #ff6b9d;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
  font-size: inherit;
}

.link-btn:hover {
  color: #ff8fab;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

/* Web-First Responsive Design */

/* Default Desktop Styles (1024px+) */
.login-container {
  padding: 20px;
}

.login-card {
  max-width: 550px;
  padding: 60px;
}

.app-title {
  font-size: 3rem;
}

.app-subtitle {
  font-size: 1.2rem;
}

.form-title {
  font-size: 2rem;
  margin-bottom: 35px;
}

.form-input {
  padding: 18px;
  font-size: 1.1rem;
}

.login-btn {
  padding: 18px;
  font-size: 1.2rem;
}

.circle-1 {
  width: 250px;
  height: 250px;
}

.circle-2 {
  width: 180px;
  height: 180px;
}

.circle-3 {
  width: 120px;
  height: 120px;
}

/* Large Desktop (1440px+) */
@media (min-width: 1440px) {
  .login-container {
    padding: 20px;
  }
  
  .login-card {
    max-width: 650px;
    padding: 70px;
  }
  
  .app-title {
    font-size: 3.5rem;
  }
  
  .form-title {
    font-size: 2.2rem;
  }
  
  .form-input {
    padding: 20px;
    font-size: 1.2rem;
  }
  
  .login-btn {
    padding: 20px;
    font-size: 1.3rem;
  }
}

/* Medium Desktop (1200px - 1439px) */
@media (max-width: 1439px) and (min-width: 1200px) {
  .login-card {
    max-width: 480px;
    padding: 45px;
  }
  
  .app-title {
    font-size: 2.8rem;
  }
}

/* Small Desktop/Large Tablet (1024px - 1199px) */
@media (max-width: 1199px) and (min-width: 1024px) {
  .login-container {
    padding: 20px;
  }
  
  .login-card {
    max-width: 500px;
    padding: 50px;
  }
  
  .app-title {
    font-size: 2.5rem;
  }
  
  .form-title {
    font-size: 1.8rem;
  }
}

/* Tablet (768px - 1023px) */
@media (max-width: 1023px) and (min-width: 768px) {
  .login-container {
    padding: 20px;
  }
  
  .login-card {
    max-width: 450px;
    padding: 45px;
  }
  
  .app-title {
    font-size: 2.2rem;
  }
  
  .form-title {
    font-size: 1.6rem;
  }
  
  .form-input {
    padding: 15px;
  }
  
  .login-btn {
    padding: 15px;
  }
  
  .circle-1 {
    width: 180px;
    height: 180px;
  }
  
  .circle-2 {
    width: 120px;
    height: 120px;
  }
  
  .circle-3 {
    width: 80px;
    height: 80px;
  }
}

/* Mobile (up to 767px) */
@media (max-width: 767px) {
  .login-container {
    padding: 15px;
  }
  
  .login-card {
    padding: 30px 25px;
    margin: 10px;
    max-width: 100%;
    border-radius: 16px;
  }
  
  .app-title {
    font-size: 2rem;
  }
  
  .app-subtitle {
    font-size: 1rem;
  }
  
  .form-title {
    font-size: 1.5rem;
    margin-bottom: 25px;
  }
  
  .form-input {
    padding: 14px;
    font-size: 16px; /* Prevents zoom on iOS */
  }
  
  .login-btn {
    padding: 14px;
    font-size: 1.1rem;
  }
  
  .circle-1 {
    width: 120px;
    height: 120px;
  }
  
  .circle-2 {
    width: 80px;
    height: 80px;
  }
  
  .circle-3 {
    width: 60px;
    height: 60px;
  }
}

/* Small Mobile (up to 480px) */
@media (max-width: 480px) {
  .login-container {
    padding: 0;
  }
  
  .login-card {
    padding: 25px 20px;
    margin: 5px;
    border-radius: 12px;
  }
  
  .app-title {
    font-size: 1.8rem;
  }
  
  .form-title {
    font-size: 1.3rem;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-input {
    padding: 12px;
  }
  
  .login-btn {
    padding: 12px;
  }
}

/* Landscape Mobile */
@media (max-width: 767px) and (orientation: landscape) {
  .login-container {
    padding: 0;
  }
  
  .login-card {
    padding: 25px;
    max-height: 90vh;
    overflow-y: auto;
  }
  
  .logo-section {
    margin-bottom: 20px;
  }
  
  .app-title {
    font-size: 1.8rem;
  }
  
  .form-title {
    font-size: 1.4rem;
    margin-bottom: 20px;
  }
}
</style>
