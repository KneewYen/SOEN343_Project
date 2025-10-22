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

        <!-- Backend Data Display -->
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🚲</div>
            <h3>Stations Data</h3>
            <div v-if="loading">Loading...</div>
            <div v-else>
              <p>Total Stations: {{ stations.length }}</p>
              <div v-for="station in stations" :key="station.id" class="data-item">
                <strong>{{ station.name }}</strong><br>
                Address: {{ station.address }}<br>
                Total Bikes: {{ station.count }}<br>
                Capacity: {{ station.capacity }}<br>
                Status: {{ station.status }}<br>
                Free Docks: {{ getFreeDocksCount(station) }}<br>
                Available Bikes: {{ getAvailableBikesCount(station) }}
              </div>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-icon">📋</div>
            <h3>Reservation Endpoints</h3>
            <p>Available endpoints:</p>
            <ul>
              <li>GET /reservation/{id}</li>
              <li>GET /reservation/valid/{id}</li>
              <li>POST /reservation/createReservation/{bikeId}/{userId}</li>
              <li>DELETE /reservation/delete/{id}</li>
            </ul>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-icon">🚴</div>
            <h3>Trip Endpoints</h3>
            <p>Available endpoints:</p>
            <ul>
              <li>POST /trip/{reservationId}</li>
              <li>PUT /trip/{tripId}/{stationId}</li>
            </ul>
          </div>

          <div class="feature-card" v-if="userRole === 'operator'">
            <div class="feature-icon">⚙️</div>
            <h3>Operator Endpoints</h3>
            <p>Available endpoints:</p>
            <ul>
              <li>POST /operator/bike/{id}/toggle</li>
              <li>POST /operator/dock/{id}/toggle</li>
              <li>POST /operator/station/{id}/toggle</li>
              <li>POST /operator/moveBike</li>
              <li>POST /operator/rebalanceBikes</li>
            </ul>
          </div>
        </div>

        <!-- User Details -->
        <div class="user-details">
          <h3>Your Account Details</h3>
          <div class="details-grid">
            <div class="detail-item">
              <span class="detail-label">Email:</span>
              <span class="detail-value">{{ userEmail || 'Not available' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">User ID:</span>
              <span class="detail-value">{{ userId || 'Not available' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Role:</span>
              <span class="detail-value">{{ userRole || 'rider' }}</span>
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
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { apiClient } from '@/lib/api'

const authStore = useAuthStore()

const emit = defineEmits(['logout'])

// Reactive data
const loading = ref(false)
const stations = ref([])
const apiTestResult = ref('')

// Computed properties
const userEmail = computed(() => authStore.userEmail.value)
const userId = computed(() => authStore.userId.value)
const userRole = computed(() => authStore.userRole?.value || 'rider')

// Methods
const handleLogout = () => {
  emit('logout')
}

const loadStations = async () => {
  try {
    loading.value = true
    const stationsData = await apiClient.getAllStations()
    stations.value = stationsData
    console.log('Loaded stations:', stationsData)
  } catch (error) {
    console.error('Error loading stations:', error)
  } finally {
    loading.value = false
  }
}

const getFreeDocksCount = (station) => {
  if (!station.dockIds) return 0
  return station.dockIds.filter(dock => dock.status === 'EMPTY').length
}

const getAvailableBikesCount = (station) => {
  if (!station.dockIds) return 0
  return station.dockIds.filter(dock => dock.status === 'OCCUPIED' && dock.bikeId).length
}

// Lifecycle
onMounted(() => {
  loadStations()
})
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

/* Simple data display styles */
.data-item {
  margin: 10px 0;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 0.9rem;
}

.data-item strong {
  color: #ff6b9d;
}

.test-section {
  margin-top: 15px;
  padding: 15px;
  background: #e8f4fd;
  border-radius: 8px;
}

.test-section h4 {
  margin-bottom: 10px;
  color: #333;
}

.test-result {
  margin-top: 10px;
  padding: 10px;
  background: #f0f8ff;
  border-radius: 5px;
  font-size: 0.9rem;
  color: #0066cc;
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
