<template>
  <div class="rider-dashboard">
    <ThemeToggle />
    <div class="dashboard-container">
      <!-- Header -->
      <header class="dashboard-header">
        <div class="header-content">
          <div class="logo-section">
            <div class="logo-icon">
              <svg width="32" height="32" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="32" cy="32" r="30" stroke="currentColor" stroke-width="2"/>
                <path d="M20 32h8l4-8h8l4 8h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <circle cx="20" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
                <circle cx="44" cy="44" r="6" stroke="currentColor" stroke-width="2"/>
                <path d="M26 44h12" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <h1 class="app-title">RideWithUs</h1>
          </div>
          <div class="user-info">
            <span class="welcome-text">Welcome, {{ user?.fullName || 'Rider' }}!</span>
            <button @click="handleLogout" class="logout-btn">Logout</button>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <main class="dashboard-main">
        <div class="dashboard-grid">
          <!-- Quick Actions -->
          <section class="quick-actions">
            <h2 class="section-title">Quick Actions</h2>
            <div class="action-buttons">
              <button class="action-btn primary">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <path d="M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    <circle cx="8" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                    <circle cx="16" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="btn-text">Find Bike</span>
              </button>
              <button class="action-btn secondary" @click="showStations">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="btn-text">Nearby Stations</span>
              </button>
              <button class="action-btn secondary">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M18 20V10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M12 20V4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="btn-text">My Trips</span>
              </button>
            </div>
          </section>

          <!-- Nearby Stations -->
          <section v-if="showStationsList" class="nearby-stations">
            <h2 class="section-title">Nearby Stations</h2>
            <StationsMap :stations="stations" :loading="loading" />
          </section>

          <!-- Current Trip -->
          <section class="current-trip">
            <h2 class="section-title">Current Trip</h2>
            <div class="trip-card">
              <div class="trip-status">
                <span class="status-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="status-text">No active trip</span>
              </div>
              <p class="trip-description">Start a new trip to begin your journey!</p>
            </div>
          </section>

          <!-- Recent Trips -->
          <section class="recent-trips">
            <h2 class="section-title">Recent Trips</h2>
            <div class="trips-list">
              <div class="trip-item">
                <div class="trip-info">
                  <span class="trip-date">Today, 2:30 PM</span>
                  <span class="trip-duration">15 minutes</span>
                </div>
                <div class="trip-distance">2.3 km</div>
              </div>
              <div class="trip-item">
                <div class="trip-info">
                  <span class="trip-date">Yesterday, 5:45 PM</span>
                  <span class="trip-duration">22 minutes</span>
                </div>
                <div class="trip-distance">3.1 km</div>
              </div>
            </div>
          </section>

          <!-- Statistics -->
          <section class="statistics">
            <h2 class="section-title">Your Stats</h2>
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-value">12</div>
                <div class="stat-label">Total Trips</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">45.2</div>
                <div class="stat-label">Distance (km)</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">3.2</div>
                <div class="stat-label">CO₂ Saved (kg)</div>
              </div>
            </div>
          </section>

        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ThemeToggle from '../components/ThemeToggle.vue'
import StationsMap from '../components/StationsMap.vue'
import apiClient from '../lib/api'

export default {
  name: 'RiderDashboard',
  components: {
    ThemeToggle,
    StationsMap
  },
  setup() {
    const router = useRouter()
    const user = ref(null)
    const showStationsList = ref(false)
    const stations = ref([])
    const loading = ref(false)

    onMounted(() => {
      // Load user data from localStorage
      const userData = localStorage.getItem('user')
      if (userData) {
        user.value = JSON.parse(userData)
      }
    })

    const handleLogout = () => {
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      router.push('/login')
    }

    const loadStations = async () => {
      try {
        loading.value = true
        const stationsData = await apiClient.getAllStations()
        stations.value = stationsData
      } catch (error) {
        console.error('Error loading stations:', error)
      } finally {
        loading.value = false
      }
    }

    const showStations = () => {
      // Toggle stations visibility and load data if needed
      showStationsList.value = !showStationsList.value
      if (showStationsList.value && stations.value.length === 0) {
        loadStations()
      }
    }

    const getAvailableBikesCount = (station) => {
      if (!station.dockIds) return 0
      return station.dockIds.filter(dock => dock.bikeId && dock.status === 'OCCUPIED').length
    }

    const getFreeDocksCount = (station) => {
      if (!station.dockIds) return 0
      return station.dockIds.filter(dock => dock.status === 'EMPTY').length
    }

    return {
      user,
      handleLogout,
      showStations,
      showStationsList,
      stations,
      loading,
      getAvailableBikesCount,
      getFreeDocksCount
    }
  }
}
</script>

<style scoped>
.rider-dashboard {
  min-height: 100vh;
  background: var(--gradient);
  position: relative;
}

.dashboard-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.dashboard-header {
  background: var(--surface);
  border-bottom: 2px solid var(--border);
  padding: 16px 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 32px;
}

.app-title {
  font-size: 24px;
  font-weight: 800;
  background: var(--gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-text {
  color: var(--text);
  font-weight: 600;
}

.logout-btn {
  background: var(--primary);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: var(--primary-hover);
  transform: translateY(-2px);
}

.dashboard-main {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text);
  margin: 0 0 16px 0;
}

.quick-actions {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: var(--gradient);
  color: white;
}

.action-btn.secondary {
  background: var(--surface-hover);
  color: var(--text);
  border: 2px solid var(--border);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.btn-icon {
  font-size: 20px;
}

.current-trip {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
}

.trip-card {
  text-align: center;
}

.trip-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.status-icon {
  font-size: 24px;
}

.status-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
}

.trip-description {
  color: var(--text-secondary);
  margin: 0;
}

.recent-trips {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
}

.trips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trip-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: var(--surface-hover);
  border-radius: 8px;
  border: 1px solid var(--border);
}

.trip-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.trip-date {
  font-weight: 600;
  color: var(--text);
}

.trip-duration {
  font-size: 14px;
  color: var(--text-secondary);
}

.trip-distance {
  font-weight: 700;
  color: var(--primary);
}

.statistics {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-card {
  text-align: center;
  padding: 16px;
  background: var(--surface-hover);
  border-radius: 12px;
  border: 1px solid var(--border);
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: var(--primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 600;
}

/* Mobile-first responsive design */
@media (max-width: 480px) {
  .dashboard-header {
    padding: 12px 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .user-info {
    width: 100%;
    justify-content: space-between;
  }
  
  .app-title {
    font-size: 20px;
  }
  
  .logo-icon {
    font-size: 28px;
  }
  
  .dashboard-main {
    padding: 16px;
  }
  
  .dashboard-grid {
    gap: 16px;
  }
  
  .quick-actions,
  .current-trip,
  .recent-trips,
  .statistics {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}

@media (min-width: 481px) and (max-width: 768px) {
  .dashboard-main {
    padding: 20px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .quick-actions {
    grid-column: 1 / -1;
  }
  
  .action-buttons {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1025px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .quick-actions {
    grid-column: 1 / -1;
  }
  
  .action-buttons {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* Landscape orientation adjustments */
@media (max-height: 600px) and (orientation: landscape) {
  .dashboard-header {
    padding: 8px 16px;
  }
  
  .dashboard-main {
    padding: 12px;
  }
  
  .dashboard-grid {
    gap: 16px;
  }
}

/* Nearby Stations */
.nearby-stations {
  margin-top: 2rem;
}

.stations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.station-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

.station-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.station-header h3 {
  margin: 0;
  color: #1e293b;
  font-size: 1.1rem;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  text-transform: uppercase;
}

.status-badge.active {
  background: #dcfce7;
  color: #166534;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #991b1b;
}

.station-details p {
  margin: 0.5rem 0;
  color: #64748b;
  font-size: 0.9rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #64748b;
}

@media (max-width: 768px) {
  .stations-grid {
    grid-template-columns: 1fr;
  }
}
</style>
