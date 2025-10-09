<template>
  <div class="register-container">
    <div class="register-card">
      <div class="logo-section">
        <h1 class="app-title">RideWithUs</h1>
        <p class="app-subtitle">Join the ride-sharing revolution</p>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <h2 class="form-title">Create Account</h2>
        
        <div class="form-row">
          <div class="form-group">
            <label for="firstName" class="form-label">First Name</label>
            <input
              id="firstName"
              v-model="form.firstName"
              type="text"
              class="form-input"
              placeholder="First name"
              required
            />
          </div>

          <div class="form-group">
            <label for="lastName" class="form-label">Last Name</label>
            <input
              id="lastName"
              v-model="form.lastName"
              type="text"
              class="form-input"
              placeholder="Last name"
              required
            />
          </div>
        </div>

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
          <label for="phone" class="form-label">Phone Number</label>
          <input
            id="phone"
            v-model="form.phone"
            type="tel"
            class="form-input"
            placeholder="Enter your phone number"
            required
          />
        </div>

        <div class="form-group">
          <label for="userType" class="form-label">I want to be a</label>
          <select
            id="userType"
            v-model="form.userType"
            class="form-select"
            required
          >
            <option value="">Select your role</option>
            <option value="PASSENGER">Passenger</option>
            <option value="DRIVER">Driver</option>
            <option value="BOTH">Both (Passenger & Driver)</option>
          </select>
        </div>

        <div class="form-group">
          <label for="password" class="form-label">Password</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            class="form-input"
            placeholder="Create a password"
            required
            minlength="6"
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword" class="form-label">Confirm Password</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            class="form-input"
            placeholder="Confirm your password"
            required
          />
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input
              v-model="form.agreeToTerms"
              type="checkbox"
              required
            />
            <span class="checkmark"></span>
            I agree to the <a href="#" class="terms-link">Terms of Service</a> and <a href="#" class="terms-link">Privacy Policy</a>
          </label>
        </div>

        <button type="submit" class="register-btn" :disabled="loading || !isFormValid">
          <span v-if="loading" class="spinner"></span>
          {{ loading ? 'Creating Account...' : 'Create Account' }}
        </button>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <div v-if="success" class="success-message">
          {{ success }}
        </div>

        <div class="form-footer">
          <p>Already have an account? 
            <button type="button" @click="$emit('switch-to-login')" class="link-btn">
              Sign In
            </button>
          </p>
        </div>
      </form>
    </div>

    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { supabase } from '@/lib/supabase'

const emit = defineEmits(['switch-to-login', 'register-success'])

const loading = ref(false)
const error = ref('')
const success = ref('')

const form = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  userType: '',
  password: '',
  confirmPassword: '',
  agreeToTerms: false
})

const isFormValid = computed(() => {
  return form.firstName && 
         form.lastName && 
         form.email && 
         form.phone && 
         form.userType && 
         form.password && 
         form.confirmPassword && 
         form.password === form.confirmPassword &&
         form.agreeToTerms
})

const handleRegister = async () => {
  if (form.password !== form.confirmPassword) {
    error.value = 'Passwords do not match'
    return
  }

  loading.value = true
  error.value = ''
  success.value = ''

  try {
    // Create user in Supabase Auth
    const { data: authData, error: authError } = await supabase.auth.signUp({
      email: form.email,
      password: form.password,
      options: {
        data: {
          first_name: form.firstName,
          last_name: form.lastName,
          phone: form.phone,
          user_type: form.userType
        }
      }
    })

    if (authError) {
      throw authError
    }

    if (authData.user) {
      // Create user profile in database
      const { error: profileError } = await supabase
        .from('users')
        .insert([{
          name: `${form.firstName} ${form.lastName}`,
          email: form.email,
          phone: form.phone,
          user_type: form.userType,
          is_active: true
        }])

      if (profileError) {
        console.warn('Profile creation failed:', profileError)
        // Don't throw here as auth was successful
      }

      success.value = 'Account created successfully! Please check your email to verify your account.'
      
      // Emit success after a short delay
      setTimeout(() => {
        emit('register-success', authData.user)
      }, 2000)
    }
  } catch (err) {
    error.value = err.message || 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
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

.register-card {
  background: white;
  border-radius: 20px;
  padding: 50px;
  box-shadow: 0 20px 40px rgba(255, 107, 157, 0.2);
  width: 95%;
  max-width: 600px;
  position: relative;
  z-index: 10;
  max-height: 90vh;
  overflow-y: auto;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
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

.form-input,
.form-select {
  width: 100%;
  padding: 15px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
  background: white;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #ff6b9d;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.checkbox-group {
  margin-bottom: 25px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  cursor: pointer;
  font-size: 0.9rem;
  line-height: 1.4;
  color: #555;
}

.checkbox-label input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 20px;
  height: 20px;
  border: 2px solid #f0f0f0;
  border-radius: 4px;
  position: relative;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark {
  background: linear-gradient(135deg, #ff6b9d, #ff8fab);
  border-color: #ff6b9d;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.terms-link {
  color: #ff6b9d;
  text-decoration: none;
}

.terms-link:hover {
  text-decoration: underline;
}

.register-btn {
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

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(255, 107, 157, 0.3);
}

.register-btn:disabled {
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

.success-message {
  background: #e6f7e6;
  color: #28a745;
  padding: 12px;
  border-radius: 8px;
  margin-top: 15px;
  font-size: 0.9rem;
  border-left: 4px solid #28a745;
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
  top: 5%;
  left: 5%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 15%;
  left: 15%;
  animation-delay: 4s;
}

.circle-4 {
  width: 80px;
  height: 80px;
  top: 20%;
  right: 30%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

/* Web-First Responsive Design */

/* Default Desktop Styles (1024px+) */
.register-container {
  padding: 20px;
}

.register-card {
  max-width: 700px;
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

.form-row {
  gap: 25px;
}

.form-input,
.form-select {
  padding: 18px;
  font-size: 1.1rem;
}

.register-btn {
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

.circle-4 {
  width: 100px;
  height: 100px;
}

/* Large Desktop (1440px+) */
@media (min-width: 1440px) {
  .register-container {
    padding: 20px;
  }
  
  .register-card {
    max-width: 800px;
    padding: 70px;
  }
  
  .app-title {
    font-size: 3.5rem;
  }
  
  .form-title {
    font-size: 2.2rem;
  }
  
  .form-input,
  .form-select {
    padding: 20px;
    font-size: 1.2rem;
  }
  
  .register-btn {
    padding: 20px;
    font-size: 1.3rem;
  }
  
  .form-row {
    gap: 30px;
  }
}

/* Medium Desktop (1200px - 1439px) */
@media (max-width: 1439px) and (min-width: 1200px) {
  .register-card {
    max-width: 580px;
    padding: 45px;
  }
  
  .app-title {
    font-size: 2.8rem;
  }
  
  .form-row {
    gap: 25px;
  }
}

/* Small Desktop/Large Tablet (1024px - 1199px) */
@media (max-width: 1199px) and (min-width: 1024px) {
  .register-container {
    padding: 20px;
  }
  
  .register-card {
    max-width: 650px;
    padding: 50px;
  }
  
  .app-title {
    font-size: 2.5rem;
  }
  
  .form-title {
    font-size: 1.8rem;
  }
  
  .form-row {
    gap: 20px;
  }
}

/* Tablet (768px - 1023px) */
@media (max-width: 1023px) and (min-width: 768px) {
  .register-container {
    padding: 20px;
  }
  
  .register-card {
    max-width: 600px;
    padding: 45px;
  }
  
  .app-title {
    font-size: 2.2rem;
  }
  
  .form-title {
    font-size: 1.6rem;
  }
  
  .form-input,
  .form-select {
    padding: 15px;
  }
  
  .register-btn {
    padding: 15px;
  }
  
  .form-row {
    gap: 15px;
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
  
  .circle-4 {
    width: 70px;
    height: 70px;
  }
}

/* Mobile (up to 767px) */
@media (max-width: 767px) {
  .register-container {
    padding: 15px;
  }
  
  .register-card {
    padding: 30px 25px;
    margin: 10px;
    max-width: 100%;
    border-radius: 16px;
    max-height: 95vh;
    overflow-y: auto;
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
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .form-input,
  .form-select {
    padding: 14px;
    font-size: 16px; /* Prevents zoom on iOS */
  }
  
  .register-btn {
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
  
  .circle-4 {
    width: 50px;
    height: 50px;
  }
}

/* Small Mobile (up to 480px) */
@media (max-width: 480px) {
  .register-container {
    padding: 0;
  }
  
  .register-card {
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
  
  .form-input,
  .form-select {
    padding: 12px;
  }
  
  .register-btn {
    padding: 12px;
  }
  
  .checkbox-label {
    font-size: 0.85rem;
  }
}

/* Landscape Mobile */
@media (max-width: 767px) and (orientation: landscape) {
  .register-container {
    padding: 0;
  }
  
  .register-card {
    padding: 25px;
    max-height: 90vh;
    overflow-y: auto;
  }
  
  .logo-section {
    margin-bottom: 15px;
  }
  
  .app-title {
    font-size: 1.8rem;
  }
  
  .form-title {
    font-size: 1.4rem;
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 12px;
  }
}
</style>
