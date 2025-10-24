<template>
  <div class="register-view">
    <ThemeToggle />
    <div class="register-container">
      <div class="back-to-home">
        <router-link to="/" class="home-link">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <polyline points="9,22 9,12 15,12 15,22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Back to Home
        </router-link>
      </div>
      <div class="register-card">
        <div class="logo-section">
          <div class="logo-icon">
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="32" cy="32" r="30" stroke="currentColor" stroke-width="2"/>
              <path d="M20 32h8l4-8h8l4 8h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <circle cx="20" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
              <circle cx="44" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
              <path d="M26 44h12" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h1 class="app-title">RideWithUs</h1>
          <p class="app-subtitle">Join the bike-sharing revolution</p>
        </div>

        <form @submit.prevent="handleRegister" class="register-form">
          <h2 class="form-title">Create Account</h2>

          <div class="form-group">
            <label for="fullName" class="form-label">
              <span class="label-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                </svg>
              </span>
              Full Name *
            </label>
            <input
              id="fullName"
              v-model="form.fullName"
              type="text"
              class="form-input"
              placeholder="Enter your full name"
              required
            />
          </div>

          <div class="form-group">
            <label for="userName" class="form-label">
              <span class="label-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                </svg>
              </span>
              Username *
            </label>
            <input
              id="userName"
              v-model="form.userName"
              type="text"
              class="form-input"
              placeholder="Choose a username"
              required
            />
          </div>

          <div class="form-group">
            <label for="email" class="form-label">
              <span class="label-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <polyline points="22,6 12,13 2,6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </span>
              Email *
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              class="form-input"
              placeholder="your.email@example.com"
              required
            />
          </div>

          <div class="form-group">
            <label for="address" class="form-label">
              <span class="label-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
                </svg>
              </span>
              Address
            </label>
            <input
              id="address"
              v-model="form.address"
              type="text"
              class="form-input"
              placeholder="Your address (optional)"
            />
          </div>

          <div class="form-group">
            <label for="password" class="form-label">
              <span class="label-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                  <circle cx="12" cy="16" r="1" fill="currentColor"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2"/>
                </svg>
              </span>
              Password *
            </label>
            <div class="password-input-container">
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="Create a password (min. 6 characters)"
                required
                minlength="6"
              />
              <button
                type="button"
                @click="togglePassword"
                class="password-toggle"
                :title="showPassword ? 'Hide password' : 'Show password'"
              >
                <svg v-if="!showPassword" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                </svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword" class="form-label">
              <span class="label-icon">🔑</span>
              Confirm Password *
            </label>
            <div class="password-input-container">
              <input
                id="confirmPassword"
                v-model="form.confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="Confirm your password"
                required
              />
              <button
                type="button"
                @click="togglePassword"
                class="password-toggle"
                :title="showPassword ? 'Hide password' : 'Show password'"
              >
                <svg v-if="!showPassword" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                </svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </div>

          <div class="form-group">
            <label for="paymentInfo" class="form-label">
              <span class="label-icon">💳</span>
              Payment Information
            </label>
            <input
              id="paymentInfo"
              v-model="form.paymentInfo"
              type="text"
              class="form-input"
              placeholder="Payment method (optional)"
            />
          </div>

          <button type="submit" class="register-btn" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            <span v-else>Create Account</span>
          </button>

          <div v-if="error" class="error-message">
            <span class="message-icon">⚠️</span>
            {{ error }}
          </div>

          <div v-if="success" class="success-message">
            <span class="message-icon">✓</span>
            {{ success }}
          </div>

          <div class="form-footer">
            <p>Already have an account?
              <router-link to="/login" class="link-btn">Sign In</router-link>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import ThemeToggle from '../components/ThemeToggle.vue'

export default {
  name: 'RegisterView',
  components: {
    ThemeToggle
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const form = ref({
      fullName: '',
      userName: '',
      email: '',
      address: '',
      password: '',
      confirmPassword: '',
      paymentInfo: ''
    })
    const loading = ref(false)
    const error = ref('')
    const success = ref('')
    const showPassword = ref(false)

    const handleRegister = async () => {
      loading.value = true
      error.value = ''
      success.value = ''

      // Client-side validation
      if (form.value.password !== form.value.confirmPassword) {
        error.value = 'Passwords do not match'
        loading.value = false
        return
      }

      if (form.value.password.length < 6) {
        error.value = 'Password must be at least 6 characters'
        loading.value = false
        return
      }

      try {
        const response = await authStore.signUp(form.value.email, form.value.password, {
          fullName: form.value.fullName,
          userName: form.value.userName,
          address: form.value.address,
          paymentInfo: form.value.paymentInfo
        })

        if (response.user) {
          success.value = 'Registration successful! Redirecting to dashboard...'

          // Clear form
          form.value = {
            fullName: '',
            userName: '',
            email: '',
            address: '',
            password: '',
            confirmPassword: '',
            paymentInfo: ''
          }

          // Redirect to rider dashboard after 2 seconds (all new users are riders)
          setTimeout(() => {
            router.push('/dashboard/rider')
          }, 2000)
        } else {
          error.value = 'Registration failed'
        }
      } catch (err) {
        error.value = err.message || 'Registration failed. Please try again.'
      } finally {
        loading.value = false
      }
    }

    const togglePassword = () => {
      showPassword.value = !showPassword.value
    }

    return {
      form,
      loading,
      error,
      success,
      showPassword,
      handleRegister,
      togglePassword
    }
  }
}
</script>

<style scoped>
.register-view {
  min-height: 100vh;
  background: var(--gradient);
  position: relative;
}

.register-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
}

.back-to-home {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
}

.home-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  text-decoration: none;
  font-weight: 600;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.home-link:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.register-card {
  background: var(--surface);
  border-radius: 24px;
  box-shadow: var(--card-shadow);
  padding: 48px;
  max-width: 580px;
  width: 100%;
  border: 2px solid var(--border);
  max-height: 90vh;
  overflow-y: auto;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-section {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  font-size: 64px;
  margin-bottom: 16px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.app-title {
  font-size: 36px;
  font-weight: 800;
  background: var(--gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px 0;
}

.app-subtitle {
  color: var(--text-secondary);
  margin: 0;
  font-size: 16px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text);
  margin: 0 0 8px 0;
  text-align: center;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.label-icon {
  font-size: 18px;
}

.form-input {
  padding: 14px 18px;
  border: 2px solid var(--border);
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: var(--background);
  color: var(--text);
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 4px var(--primary-light);
  transform: translateY(-2px);
}

/* Password input container */
.password-input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: var(--text-secondary);
  transition: color 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.password-toggle:hover {
  color: var(--primary);
  background: rgba(236, 72, 153, 0.1);
}

.password-toggle:focus {
  outline: none;
  color: var(--primary);
  background: rgba(236, 72, 153, 0.1);
}

.register-btn {
  background: var(--gradient);
  color: white;
  padding: 16px;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(236, 72, 153, 0.4);
}

.register-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message,
.success-message {
  padding: 14px 16px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-10px); }
  75% { transform: translateX(10px); }
}

.error-message {
  background-color: var(--error-bg);
  color: var(--error);
  border: 2px solid var(--error);
}

.success-message {
  background-color: var(--success-bg);
  color: var(--success);
  border: 2px solid var(--success);
}

.message-icon {
  font-size: 18px;
}

.form-footer {
  text-align: center;
  margin-top: 8px;
}

.form-footer p {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0;
}

.link-btn {
  color: var(--primary);
  text-decoration: none;
  font-weight: 700;
  transition: all 0.3s ease;
}

.link-btn:hover {
  text-decoration: underline;
  color: var(--primary-hover);
}

/* Custom scrollbar for dark mode */
.register-card::-webkit-scrollbar {
  width: 8px;
}

.register-card::-webkit-scrollbar-track {
  background: var(--background);
  border-radius: 4px;
}

.register-card::-webkit-scrollbar-thumb {
  background: var(--primary);
  border-radius: 4px;
}

/* Mobile-first responsive design */
@media (max-width: 480px) {
  /* Small mobile phones */
  .register-container {
    padding: 16px;
  }
  
  .back-to-home {
    top: 10px;
    left: 10px;
  }
  
  .home-link {
    padding: 6px 12px;
    font-size: 14px;
  }
  
  .register-card {
    padding: 24px 20px;
    border-radius: 20px;
  }
  
  .app-title {
    font-size: 28px;
  }
  
  .logo-icon {
    font-size: 40px;
  }
  
  .form-title {
    font-size: 24px;
  }
  
  .form-input {
    padding: 12px 16px;
    font-size: 16px;
  }
  
  .register-btn {
    padding: 14px;
    font-size: 16px;
  }
}

@media (min-width: 481px) and (max-width: 640px) {
  /* Large mobile phones */
  .register-container {
    padding: 20px;
  }
  
  .register-card {
    padding: 32px 24px;
  }
  
  .app-title {
    font-size: 32px;
  }
  
  .logo-icon {
    font-size: 48px;
  }
}

@media (min-width: 641px) and (max-width: 768px) {
  /* Small tablets */
  .register-card {
    padding: 40px 32px;
    max-width: 520px;
  }
  
  .app-title {
    font-size: 36px;
  }
  
  .logo-icon {
    font-size: 56px;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  /* Tablets */
  .register-card {
    padding: 48px 40px;
    max-width: 560px;
  }
  
  .app-title {
    font-size: 40px;
  }
  
  .logo-icon {
    font-size: 64px;
  }
}

@media (min-width: 1025px) and (max-width: 1440px) {
  /* Laptops and small desktops */
  .register-card {
    padding: 48px;
    max-width: 480px;
  }
}

@media (min-width: 1441px) {
  /* Large desktops */
  .register-card {
    padding: 56px;
    max-width: 520px;
  }
  
  .app-title {
    font-size: 40px;
  }
  
  .logo-icon {
    font-size: 72px;
  }
  
  .form-title {
    font-size: 32px;
  }
}

/* Landscape orientation adjustments */
@media (max-height: 600px) and (orientation: landscape) {
  .register-container {
    padding: 10px;
  }
  
  .register-card {
    padding: 24px;
  }
  
  .logo-section {
    margin-bottom: 20px;
  }
  
  .logo-icon {
    font-size: 40px;
  }
  
  .app-title {
    font-size: 28px;
  }
  
  .form-title {
    font-size: 24px;
  }
}
</style>

