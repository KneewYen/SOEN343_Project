<template>
  <div class="dashboard-page">
    <ThemeToggle />
    
    <div class="dashboard-container">
      <header class="dashboard-header">
        <h1>🚴‍♂️ Bike Rental Dashboard</h1>
        <button @click="logout" class="logout-btn">Logout</button>
      </header>
      
      <main class="dashboard-content">
        <div class="welcome-card">
          <h2>Welcome, {{ user.name }}!</h2>
          <p>Email: {{ user.email }}</p>
          <p>Account Type: {{ user.type }}</p>
        </div>
        
        <div class="features-grid">
          <div class="feature-card">
            <h3>🚴‍♂️ Rent a Bike</h3>
            <p>Find and rent available bikes near you</p>
            <button class="feature-btn">Coming Soon</button>
          </div>
          
          <div class="feature-card">
            <h3>📍 Find Stations</h3>
            <p>Locate bike stations and check availability</p>
            <button class="feature-btn">Coming Soon</button>
          </div>
          
          <div class="feature-card">
            <h3>📊 My Rentals</h3>
            <p>View your current and past bike rentals</p>
            <button class="feature-btn">Coming Soon</button>
          </div>
          
          <div class="feature-card">
            <h3>⚙️ Settings</h3>
            <p>Manage your account and preferences</p>
            <button class="feature-btn">Coming Soon</button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ThemeToggle from '../components/ThemeToggle.vue'

const router = useRouter()
const user = ref({
  name: 'Demo User',
  email: 'demo@example.com',
  type: 'user'
})

onMounted(() => {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    user.value = JSON.parse(storedUser)
  } else {
    router.push('/login')
  }
})

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.dashboard-page {
  min-height: 100vh;
  background: var(--grey-50);
  position: relative;
}

.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding: 20px 0;
  border-bottom: 2px solid var(--grey-200);
}

.dashboard-header h1 {
  color: var(--grey-800);
  font-size: 32px;
  font-weight: 700;
  margin: 0;
}

.logout-btn {
  background: var(--grey-600);
  color: var(--white);
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: var(--grey-700);
  transform: translateY(-1px);
}

.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.welcome-card {
  background: var(--white);
  padding: 32px;
  border-radius: 16px;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--grey-200);
  text-align: center;
}

.welcome-card h2 {
  color: var(--grey-800);
  font-size: 28px;
  margin-bottom: 16px;
}

.welcome-card p {
  color: var(--grey-600);
  font-size: 16px;
  margin: 8px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.feature-card {
  background: var(--white);
  padding: 32px;
  border-radius: 16px;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--grey-200);
  text-align: center;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-red), var(--dark-red));
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.feature-card h3 {
  color: var(--grey-800);
  font-size: 20px;
  margin-bottom: 12px;
  font-weight: 600;
}

.feature-card p {
  color: var(--grey-600);
  margin-bottom: 24px;
  line-height: 1.5;
}

.feature-btn {
  background: var(--primary-red);
  color: var(--white);
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

.feature-btn:hover {
  background: var(--dark-red);
  transform: translateY(-1px);
}

/* Dark theme */
.dark-theme {
  background: var(--grey-900);
}

.dark-theme .dashboard-header {
  border-color: var(--grey-600);
}

.dark-theme .dashboard-header h1 {
  color: var(--white);
}

.dark-theme .welcome-card,
.dark-theme .feature-card {
  background: var(--grey-800);
  border-color: var(--grey-600);
}

.dark-theme .welcome-card h2 {
  color: var(--white);
}

.dark-theme .welcome-card p {
  color: var(--grey-300);
}

.dark-theme .feature-card h3 {
  color: var(--white);
}

.dark-theme .feature-card p {
  color: var(--grey-300);
}

/* Responsive */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .dashboard-header h1 {
    font-size: 24px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-card,
  .feature-card {
    padding: 24px;
  }
}
</style>