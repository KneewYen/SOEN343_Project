<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <div class="header-content">
        <h1 class="app-title">RideWithUs</h1>
        <div class="user-info">
          <span class="welcome-text">Welcome, {{ userEmail }}!</span>
          <button @click="handleLogout" class="logout-btn">
            Logout
          </button>
        </div>
      </div>
    </header>

    <main class="dashboard-main">
      <div class="dashboard-content">
        <div class="welcome-section">
          <h2>Welcome to your Dashboard</h2>
          <p>You're successfully logged in to RideWithUs!</p>
        </div>

        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🚗</div>
            <h3>Request a Ride</h3>
            <p>Book a ride to your destination</p>
            <button class="feature-btn">Book Now</button>
          </div>

          <div class="feature-card">
            <div class="feature-icon">👨‍💼</div>
            <h3>Drive with Us</h3>
            <p>Start earning as a driver</p>
            <button class="feature-btn">Start Driving</button>
          </div>

          <div class="feature-card">
            <div class="feature-icon">📱</div>
            <h3>My Rides</h3>
            <p>View your ride history</p>
            <button class="feature-btn">View Rides</button>
          </div>

          <div class="feature-card">
            <div class="feature-icon">⚙️</div>
            <h3>Settings</h3>
            <p>Manage your account</p>
            <button class="feature-btn">Settings</button>
          </div>
        </div>

        <div class="user-details">
          <h3>Your Account Details</h3>
          <div class="details-grid">
            <div class="detail-item">
              <span class="detail-label">Email:</span>
              <span class="detail-value">{{ userEmail }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">User ID:</span>
              <span class="detail-value">{{ userId }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Status:</span>
              <span class="detail-value status-active">Active</span>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()

const emit = defineEmits(['logout'])

const userEmail = computed(() => authStore.userEmail.value)
const userId = computed(() => authStore.userId.value)

const handleLogout = () => {
  emit('logout')
}
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 50%, #ffb3c6 100%);
  margin: 0;
  padding: 0;
}

.dashboard-header {
  background: white;
  box-shadow: 0 2px 10px rgba(255, 107, 157, 0.1);
  padding: 20px 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-title {
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b9d, #ff8fab);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text {
  color: #666;
  font-weight: 500;
}

.logout-btn {
  background: linear-gradient(135deg, #ff6b9d, #ff8fab);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 107, 157, 0.3);
}

.dashboard-main {
  padding: 40px 20px;
}

.dashboard-content {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.welcome-section h2 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 700;
}

.welcome-section p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.feature-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(255, 107, 157, 0.1);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(255, 107, 157, 0.2);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.feature-card p {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.5;
}

.feature-btn {
  background: linear-gradient(135deg, #ff6b9d, #ff8fab);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

.feature-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 107, 157, 0.3);
}

.user-details {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(255, 107, 157, 0.1);
}

.user-details h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.details-grid {
  display: grid;
  gap: 15px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.detail-label {
  font-weight: 600;
  color: #555;
}

.detail-value {
  color: #333;
  font-family: monospace;
}

.status-active {
  color: #28a745;
  font-weight: 600;
}

/* Web-First Responsive Design */

/* Default Desktop Styles (1024px+) */
.dashboard-header {
  padding: 25px 0;
}

.header-content {
  padding: 0 40px;
}

.app-title {
  font-size: 2.5rem;
}

.welcome-text {
  font-size: 1.1rem;
}

.logout-btn {
  padding: 12px 24px;
  font-size: 1rem;
}

.dashboard-main {
  padding: 50px 40px;
}

.welcome-section h2 {
  font-size: 3rem;
}

.welcome-section p {
  font-size: 1.4rem;
}

.features-grid {
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.feature-card {
  padding: 35px 30px;
}

.feature-icon {
  font-size: 3.5rem;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 1.6rem;
}

.feature-card p {
  font-size: 1rem;
}

.user-details {
  padding: 35px 30px;
}

.user-details h3 {
  font-size: 1.6rem;
}

.detail-item {
  padding: 18px;
}

.detail-label {
  font-size: 1rem;
}

.detail-value {
  font-size: 0.95rem;
}

/* Large Desktop (1440px+) */
@media (min-width: 1440px) {
  .header-content {
    padding: 0 60px;
  }
  
  .app-title {
    font-size: 3rem;
  }
  
  .dashboard-main {
    padding: 60px 60px;
  }
  
  .welcome-section h2 {
    font-size: 3.5rem;
  }
  
  .welcome-section p {
    font-size: 1.6rem;
  }
  
  .features-grid {
    gap: 40px;
  }
  
  .feature-card {
    padding: 40px 35px;
  }
  
  .feature-icon {
    font-size: 4rem;
  }
  
  .feature-card h3 {
    font-size: 1.8rem;
  }
  
  .user-details {
    padding: 40px 35px;
  }
}

/* Medium Desktop (1200px - 1439px) */
@media (max-width: 1439px) and (min-width: 1200px) {
  .header-content {
    padding: 0 50px;
  }
  
  .app-title {
    font-size: 2.8rem;
  }
  
  .dashboard-main {
    padding: 45px 50px;
  }
  
  .welcome-section h2 {
    font-size: 2.8rem;
  }
  
  .features-grid {
    gap: 25px;
  }
}

/* Small Desktop/Large Tablet (1024px - 1199px) */
@media (max-width: 1199px) and (min-width: 1024px) {
  .header-content {
    padding: 0 35px;
  }
  
  .app-title {
    font-size: 2.3rem;
  }
  
  .dashboard-main {
    padding: 40px 35px;
  }
  
  .welcome-section h2 {
    font-size: 2.5rem;
  }
  
  .welcome-section p {
    font-size: 1.2rem;
  }
  
  .features-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 25px;
  }
  
  .feature-card {
    padding: 30px 25px;
  }
}

/* Tablet (768px - 1023px) */
@media (max-width: 1023px) and (min-width: 768px) {
  .dashboard-header {
    padding: 20px 0;
  }
  
  .header-content {
    padding: 0 30px;
  }
  
  .app-title {
    font-size: 2rem;
  }
  
  .dashboard-main {
    padding: 30px;
  }
  
  .welcome-section h2 {
    font-size: 2.2rem;
  }
  
  .welcome-section p {
    font-size: 1.1rem;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 25px;
  }
  
  .feature-card {
    padding: 30px 25px;
  }
  
  .feature-icon {
    font-size: 3rem;
  }
  
  .feature-card h3 {
    font-size: 1.4rem;
  }
  
  .user-details {
    padding: 30px 25px;
  }
}

/* Mobile (up to 767px) */
@media (max-width: 767px) {
  .dashboard-header {
    padding: 15px 0;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
    text-align: center;
    padding: 0 15px;
  }
  
  .app-title {
    font-size: 1.8rem;
  }
  
  .user-info {
    flex-direction: column;
    gap: 10px;
  }
  
  .welcome-text {
    font-size: 0.9rem;
  }
  
  .logout-btn {
    padding: 8px 16px;
    font-size: 0.9rem;
  }
  
  .dashboard-main {
    padding: 20px 15px;
  }
  
  .welcome-section h2 {
    font-size: 2rem;
  }
  
  .welcome-section p {
    font-size: 1rem;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .feature-card {
    padding: 25px 20px;
  }
  
  .feature-icon {
    font-size: 2.5rem;
    margin-bottom: 15px;
  }
  
  .feature-card h3 {
    font-size: 1.3rem;
  }
  
  .feature-card p {
    font-size: 0.9rem;
  }
  
  .user-details {
    padding: 25px 20px;
  }
  
  .user-details h3 {
    font-size: 1.3rem;
  }
  
  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
    padding: 12px;
  }
  
  .detail-label {
    font-size: 0.9rem;
  }
  
  .detail-value {
    font-size: 0.85rem;
  }
}

/* Small Mobile (up to 480px) */
@media (max-width: 480px) {
  .header-content {
    padding: 0 10px;
  }
  
  .app-title {
    font-size: 1.6rem;
  }
  
  .dashboard-main {
    padding: 15px 10px;
  }
  
  .welcome-section h2 {
    font-size: 1.8rem;
  }
  
  .welcome-section p {
    font-size: 0.95rem;
  }
  
  .feature-card {
    padding: 20px 15px;
  }
  
  .feature-icon {
    font-size: 2rem;
  }
  
  .feature-card h3 {
    font-size: 1.2rem;
  }
  
  .user-details {
    padding: 20px 15px;
  }
  
  .user-details h3 {
    font-size: 1.2rem;
  }
}

/* Landscape Mobile */
@media (max-width: 767px) and (orientation: landscape) {
  .dashboard-main {
    padding: 15px;
  }
  
  .welcome-section {
    margin-bottom: 25px;
  }
  
  .welcome-section h2 {
    font-size: 1.8rem;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
  
  .feature-card {
    padding: 20px 15px;
  }
}
</style>
