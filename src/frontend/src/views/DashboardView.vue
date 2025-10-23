<template>
  <div class="dashboard-root">
    <!-- Redirect UI when visiting the root dashboard route -->
    <div v-if="isRedirecting" class="dashboard-redirect">
      <div class="loading-container">
        <div class="loading-spinner">
          <div class="spinner"></div>
        </div>
        <h2 class="loading-title">Redirecting to your dashboard...</h2>
        <p class="loading-text">Please wait while we set up your personalized experience.</p>
      </div>
    </div>

    <!-- Full dashboard UI -->
    <div v-else class="dashboard-layout">
      <header class="dashboard-header">
        <div class="header-content">
          <h1 class="app-title">RideWithUs Dashboard</h1>
          <div class="header-actions">
            <button class="logout-btn" @click="handleLogout">Logout</button>
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

            <!-- Map Component -->
            <div class="feature-card">
              <StationsMap :stations="stations" :loading="loading" />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import apiClient from '@/lib/api'
import StationsMap from '@/components/StationsMap.vue'

const emit = defineEmits(['logout'])
const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

// Determine whether this is the root `/dashboard` route that should redirect
const isRootDashboard = route.path === '/dashboard' || route.name === 'Dashboard'
const isRedirecting = ref(isRootDashboard)

// Reactive data
const loading = ref(false)
const stations = ref([])

// Computed properties
const userEmail = computed(() => authStore.userEmail?.value)
const userId = computed(() => authStore.userId?.value)
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
  if (isRedirecting.value) {
    // Redirect behavior kept from original branch
    const userData = localStorage.getItem('user')
    if (userData) {
      const user = JSON.parse(userData)
      setTimeout(() => {
        if (user.role === 'operator') {
          router.push('/dashboard/operator')
        } else {
          router.push('/dashboard/rider')
        }
      }, 1500)
    } else {
      router.push('/login')
    }
  } else {
    // Load dashboard data when rendering full dashboard
    loadStations()
  }
})
</script>

<style scoped>
.dashboard-root {
  min-height: 100vh;
  background: var(--gradient, linear-gradient(135deg, #ffd6e8 0%, #ff8fab 100%));
  display: flex;
  flex-direction: column;
}

/* Redirect loader */
.dashboard-redirect {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.loading-container {
  text-align: center;
  color: white;
  max-width: 500px;
  width: 100%;
}

.loading-spinner {
  margin-bottom: 24px;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.loading-text {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
  line-height: 1.5;
}

/* Dashboard layout */
.dashboard-layout {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.dashboard-header {
  padding: 20px 0;
  background: transparent;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.app-title {
  font-size: 1.6rem;
  color: #333;
}

.header-actions .logout-btn {
  background: linear-gradient(135deg, #ff6b9d, #ff8fab);
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  cursor: pointer;
}

/* Main content */
.dashboard-main {
  padding: 30px 24px;
  width: 100%;
  box-sizing: border-box;
}

.dashboard-content {
  display: grid;
  gap: 28px;
  grid-template-columns: 1fr;
}

/* Features grid */
.features-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 20px;
}

@media (min-width: 768px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.feature-card {
  background: rgba(255,255,255,0.06);
  padding: 20px;
  border-radius: 12px;
  color: #222;
}

.feature-icon {
  font-size: 2.4rem;
  margin-bottom: 12px;
}

.user-details {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(255, 107, 157, 0.06);
}

.user-details h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.details-grid {
  display: grid;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
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

/* Responsive adjustments */
@media (max-width: 480px) {
  .dashboard-redirect {
    padding: 16px;
  }

  .spinner {
    width: 50px;
    height: 50px;
    border-width: 3px;
  }

  .loading-title {
    font-size: 20px;
  }

  .loading-text {
    font-size: 14px;
  }
}

@media (min-width: 481px) and (max-width: 768px) {
  .spinner {
    width: 55px;
    height: 55px;
  }

  .loading-title {
    font-size: 22px;
  }

  .loading-text {
    font-size: 15px;
  }
}

@media (min-width: 769px) {
  .spinner {
    width: 70px;
    height: 70px;
    border-width: 5px;
  }

  .loading-title {
    font-size: 28px;
  }

  .loading-text {
    font-size: 18px;
  }
}

/* Landscape orientation adjustments */
@media (max-height: 600px) and (orientation: landscape) {
  .dashboard-redirect {
    padding: 10px;
  }

  .loading-spinner {
    margin-bottom: 16px;
  }

  .spinner {
    width: 40px;
    height: 40px;
  }

  .loading-title {
    font-size: 18px;
  }

  .loading-text {
    font-size: 14px;
  }
}
</style>
