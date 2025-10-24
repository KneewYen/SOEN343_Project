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
                <path d="M26 44h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
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
            <StationsMap 
              :stations="stations" 
              :loading="loading" 
              @bikeReserved="handleBikeReserved"
            />
          </section>

          <!-- Current Trip -->
          <section class="current-trip">
            <h2 class="section-title">Current Trip</h2>
            
            <!-- Active Reservation -->
            <div v-if="currentReservation" class="trip-card reservation">
              <div class="trip-status">
                <span class="status-icon reservation">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <path d="M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    <circle cx="8" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                    <circle cx="16" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="status-text">Reservation Active</span>
              </div>
              <div class="trip-details">
                <p class="trip-info">Bike #{{ currentReservation.bikeId }}</p>
                <p class="trip-info">Station: {{ currentReservation.stationName }}</p>
                <p class="trip-info">Expires: {{ formatTime(currentReservation.expiryTime) }}</p>
              </div>
              <div class="trip-actions">
                <button @click="startTrip" class="action-btn primary">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <polygon points="5,3 19,12 5,21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Start Trip
                </button>
                <button @click="cancelReservation" class="action-btn secondary">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Cancel Reservation
                </button>
              </div>
            </div>

            <!-- Active Trip -->
            <div v-else-if="currentTrip" class="trip-card active">
              <div class="trip-status">
                <span class="status-icon active">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="status-text">Trip in Progress</span>
              </div>
              <div class="trip-details">
                <p class="trip-info">Bike #{{ currentTrip.bikeId }}</p>
                <p class="trip-info">Started: {{ formatTime(currentTrip.startTime) }}</p>
                <p class="trip-info">Duration: {{ getTripDuration(currentTrip.startTime) }}</p>
              </div>
              <div class="trip-actions">
                <select v-model="selectedReturnStation" class="station-select">
                  <option value="">Select return station</option>
                  <option v-for="station in stations" :key="station.id" :value="station.id">
                    {{ station.name }}
                  </option>
                </select>
                <div class="trip-actions">
                  <button 
                    @click="endTrip" 
                    :disabled="!selectedReturnStation"
                    class="action-btn danger"
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <rect x="6" y="6" width="12" height="12" rx="2" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    End Trip
                  </button>
                  <button 
                    @click="forceEndTrip" 
                    class="action-btn secondary"
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    Force End
                  </button>
                </div>
              </div>
            </div>

            <!-- No Active Trip/Reservation -->
            <div v-else class="trip-card">
              <div class="trip-status">
                <span class="status-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="status-text">No active trip</span>
              </div>
              <p class="trip-description">Find a bike to start your journey!</p>
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

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ThemeToggle from '../components/ThemeToggle.vue'
import StationsMap from '../components/StationsMap.vue'
import apiClient from '../lib/api'

const router = useRouter()
const user = ref(null)
const showStationsList = ref(false)
const stations = ref([])
const loading = ref(false)
const currentReservation = ref(null)
const currentTrip = ref(null)
const selectedReturnStation = ref('')

onMounted(() => {
  // Load user data from localStorage
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
  }
  // Initialize with clean state
  currentReservation.value = null
  currentTrip.value = null
  // Load stations and check for active trip only
  loadStations()
  checkActiveTrip()
})

const handleLogout = () => {
  console.log('🚪 Logout button clicked')
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  console.log('🚪 Navigating to /login')
  // Use replace instead of push to avoid navigation guard issues
  router.replace('/login')
}

const loadStations = async () => {
  try {
    console.log('loadStations called')
    loading.value = true
    const stationsData = await apiClient.getAllStations()
    console.log('Stations data received:', stationsData)
    stations.value = stationsData
    console.log('Stations set to:', stations.value)
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

    // Manual reservation check - only called when needed
    const checkActiveReservation = async () => {
      try {
        if (!user.value?.id) return
        
        // First check if there's an active trip - if so, don't show reservation
        const tripResponse = await apiClient.getUserTrips(user.value.id)
        if (tripResponse.success && tripResponse.trips && tripResponse.trips.length > 0) {
          const incompleteTrip = tripResponse.trips.find(trip => !trip.tripComplete)
          if (incompleteTrip) {
            currentReservation.value = null
            return
          }
        }
        
        const response = await apiClient.getUserReservations(user.value.id)
        if (response.success && response.reservations && response.reservations.length > 0) {
          const reservation = response.reservations[0]
          currentReservation.value = {
            id: reservation.reservationId || reservation.id,
            bikeId: reservation.bike?.id || reservation.bikeId,
            stationName: reservation.station?.name || 'Station',
            expiryTime: reservation.expiryDateTime || reservation.expiryTime
          }
        } else {
          currentReservation.value = null
        }
      } catch (error) {
        console.error('Error checking reservations:', error)
        currentReservation.value = null
      }
    }

    const checkActiveTrip = async () => {
      try {
        if (!user.value?.id) return
        const response = await apiClient.getUserTrips(user.value.id)
        if (response.success && response.trips && response.trips.length > 0) {
          // Find incomplete trip
          const incompleteTrip = response.trips.find(trip => !trip.tripComplete)
          if (incompleteTrip) {
            currentTrip.value = {
              id: incompleteTrip.tripId || incompleteTrip.id,
              bikeId: incompleteTrip.bikeId || 'Bike',
              startTime: incompleteTrip.startTime,
              startStationId: incompleteTrip.startStationId
            }
            // Clear any reservation since we have an active trip
            currentReservation.value = null
          } else {
            currentTrip.value = null
          }
        } else {
          currentTrip.value = null
        }
      } catch (error) {
        console.error('Error checking trips:', error)
        currentTrip.value = null
      }
    }

    const reserveBike = async (bikeId) => {
      try {
        if (!user.value?.id) {
          alert('Please log in to reserve a bike')
          return
        }
        
        // Check if user already has an active reservation
        if (currentReservation.value) {
          const cancel = confirm('You already have an active reservation. Do you want to cancel it and create a new one?')
          if (cancel) {
            await cancelReservation()
          } else {
            return
          }
        }
        
        const response = await apiClient.createReservation(bikeId, user.value.id)
        if (response.success) {
          alert('Bike reserved successfully!')
          // Set the current reservation state
          currentReservation.value = { 
            id: response.reservationId || response.id, 
            bikeId: bikeId,
            stationName: 'Station', // We'll get this from the station data
            expiryTime: new Date(Date.now() + 15 * 60000) // 15 minutes from now
          }
          await loadStations() // Refresh stations
        } else {
          alert('Failed to reserve bike: ' + (response.message || 'Unknown error'))
        }
      } catch (error) {
        console.error('Error reserving bike:', error)
        alert('Failed to reserve bike: ' + error.message)
      }
    }

    const startTrip = async () => {
      try {
        if (!currentReservation.value) {
          alert('No active reservation to start')
          return
        }
        
        // Check if user already has an active trip
        if (currentTrip.value) {
          alert('You already have an active trip. Please end it first before starting a new one.')
          return
        }
        
        const response = await apiClient.startTrip(currentReservation.value.id)
        if (response.success) {
          alert('Trip started!')
          currentTrip.value = { 
            id: response.tripId || response.id,
            bikeId: currentReservation.value.bikeId,
            startTime: new Date()
          }
          currentReservation.value = null
        } else {
          alert('Failed to start trip: ' + (response.message || 'Unknown error'))
        }
      } catch (error) {
        console.error('Error starting trip:', error)
        alert('Failed to start trip: ' + error.message)
      }
    }

    const cancelReservation = async () => {
      try {
        if (!currentReservation.value) {
          alert('No active reservation to cancel')
          return
        }
        
        // Check if there's an active trip associated with this reservation
        if (currentTrip.value) {
          alert('Cannot cancel reservation: You have an active trip. Please end the trip first.')
          return
        }
        
        if (confirm('Are you sure you want to cancel this reservation?')) {
          const response = await apiClient.deleteReservation(currentReservation.value.id)
          if (response.success) {
            alert('Reservation cancelled successfully!')
            currentReservation.value = null
            await loadStations() // Refresh stations
          } else {
            alert('Failed to cancel reservation: ' + (response.message || 'Unknown error'))
          }
        }
      } catch (error) {
        console.error('Error cancelling reservation:', error)
        if (error.message.includes('Referential integrity constraint violation')) {
          alert('Cannot cancel reservation: There is an active trip associated with this reservation. Please end the trip first.')
        } else {
          alert('Failed to cancel reservation: ' + error.message)
        }
      }
    }

    const endTrip = async () => {
      try {
        if (!currentTrip.value || !selectedReturnStation.value) {
          alert('Please select a return station')
          return
        }
        const response = await apiClient.endTrip(currentTrip.value.id, selectedReturnStation.value)
        if (response.success) {
          alert('Trip ended successfully!')
          currentTrip.value = null
          selectedReturnStation.value = ''
          await loadStations() // Refresh stations
        } else {
          alert('Failed to end trip: ' + response.message)
        }
      } catch (error) {
        console.error('Error ending trip:', error)
        alert('Failed to end trip: ' + error.message)
      }
    }

    const forceEndTrip = async () => {
      try {
        if (!currentTrip.value) {
          alert('No active trip to end')
          return
        }
        
        if (confirm('Are you sure you want to force end this trip? This will end the trip without selecting a return station.')) {
          // Try to end trip with a default station or handle it differently
          // For now, we'll just clear the local state
          alert('Trip force ended. Please contact support if you need assistance.')
          currentTrip.value = null
          selectedReturnStation.value = ''
          await loadStations() // Refresh stations
        }
      } catch (error) {
        console.error('Error force ending trip:', error)
        alert('Failed to force end trip: ' + error.message)
      }
    }

    const formatTime = (dateTime) => {
      if (!dateTime) return 'N/A'
      return new Date(dateTime).toLocaleString()
    }

    const getTripDuration = (startTime) => {
      if (!startTime) return 'N/A'
      const start = new Date(startTime)
      const now = new Date()
      const diffMs = now - start
      const diffMins = Math.floor(diffMs / 60000)
      return `${diffMins} minutes`
    }

    const getAvailableBikesCount = (station) => {
      if (!station.dockIds) return 0
      return station.dockIds.filter(dock => dock.bikeId && dock.status === 'OCCUPIED').length
    }

    const getFreeDocksCount = (station) => {
      if (!station.dockIds) return 0
      return station.dockIds.filter(dock => dock.status === 'EMPTY').length
    }

    const findAvailableBike = (station) => {
      const availableBike = station.dockIds.find(dock => 
        dock.bikeId && dock.bikeStatus === 'AVAILABLE'
      )
      if (availableBike) {
        reserveBike(availableBike.bikeId)
      } else {
        alert('No available bikes at this station')
      }
    }

    const handleBikeReserved = (bike, station) => {
      reserveBike(bike.id)
    }

    const clearReservationState = () => {
      currentReservation.value = null
      currentTrip.value = null
      selectedReturnStation.value = ''
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

.trip-card.reservation {
  border: 2px solid #f59e0b;
  background: rgba(245, 158, 11, 0.05);
}

.trip-card.active {
  border: 2px solid #22c55e;
  background: rgba(34, 197, 94, 0.05);
}

.trip-details {
  margin-bottom: 1rem;
}

.trip-info {
  margin: 0.25rem 0;
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.trip-actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.trip-actions:has(button:not(:only-child)) {
  flex-direction: row;
  gap: 0.5rem;
}

.trip-actions button {
  flex: 1;
}

.station-select {
  padding: 0.75rem;
  border: 1px solid var(--border);
  border-radius: 0.5rem;
  background: var(--surface);
  color: var(--text);
  font-size: 0.875rem;
}

.station-select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(236, 72, 153, 0.1);
}

.status-icon.reservation {
  color: #f59e0b;
}

.status-icon.active {
  color: #22c55e;
}

.action-btn.danger {
  background: #ef4444;
  color: white;
}

.action-btn.danger:hover {
  background: #dc2626;
}

.action-btn.danger:disabled {
  background: #6b7280;
  cursor: not-allowed;
}

.action-btn.full-width {
  width: 100%;
  justify-content: center;
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