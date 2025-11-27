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
            <span class="role-badge">Rider</span>
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
              <button class="action-btn secondary" @click="showPricing" :class="{ selected: showPricingList }">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <path d="M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    <circle cx="8" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                    <circle cx="16" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="btn-text">Pricing</span>
              </button>
              <button class="action-btn secondary" @click="showStations" :class="{ selected: showStationsList }">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="btn-text">Nearby Stations</span>
              </button>
              <button class="action-btn secondary" @click="showRideHistory" :class="{ selected: showRideHistoryList }">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M18 20V10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M12 20V4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="btn-text">Ride History
                </span>
              </button>
              <button class="action-btn secondary" @click="showBillingHistory" :class="{ selected: showBillingHistoryList }">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="btn-text">Billing History</span>
              </button>
            </div>
          </section>

          <section v-if="showPricingList" class="pricing-list">
                    <PricingPlan :user="user" />
          </section>

          <!-- Nearby Stations -->
          <section v-if="showStationsList" class="nearby-stations">
            <StationsMap 
              :stations="stations" 
              :loading="loading" 
              @bikeReserved="handleBikeReserved"
            />
          </section>


          <!-- Ride History -->
          <section v-if="showRideHistoryList" class="ride-history">
            <RideHistory 
              :user="user" 
            />
          </section>

          <!-- Billing History -->
          <section v-if="showBillingHistoryList" class="billing-history">
            <BillingHistory 
              :user="user" 
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

          <section v-if="tripSummary" class="recent-trips">
              <TripSummary :trip="selectedTrip" />

              <!-- Billing Information (added here) -->
              <div v-if="billing" class="billing-info">
                <h3 class="section-title">Billing Summary</h3>

                <div class="billing-details">
                  <p><strong>Billing ID:</strong> {{ billing.billingId }}</p>
                  <p><strong>Trip ID:</strong> {{ billing.tripId }}</p>
                  <p><strong>Total Cost:</strong> ${{ billing.totalAmount.toFixed(2) }}</p>
                </div>

                <div class="billing-charges">
                  <h4>Charges</h4>
                  <ul>
                    <li v-for="charge in billing.charges" :key="charge.name">
                      <strong>{{ charge.name }}</strong> — {{ charge.description }}
                      <span class="charge-cost">(${{ charge.cost.toFixed(2) }})</span>
                    </li>
                  </ul>
                </div>
              </div>

              <div v-else class="no-billing">
                <p>No billing information available for this trip.</p>
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
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import ThemeToggle from '../components/ThemeToggle.vue'
import StationsMap from '../components/StationsMap.vue'
import apiClient from '../lib/api'
import RideHistory from '@/components/RideHistory.vue'
import BillingHistory from '@/components/BillingHistory.vue'
import PricingPlan from '@/components/PricingPlan.vue'
import { toast } from "vue3-toastify"


const router = useRouter()
const user = ref(null)
const showStationsList = ref(false)
const showRideHistoryList = ref(false)
const showBillingHistoryList = ref(false)
const stations = ref([])
const loading = ref(false)
const currentReservation = ref(null)
const currentTrip = ref(null)
const selectedReturnStation = ref('')
let reservationInterval = null
const prevReservationId = ref(null)
const showPricingList = ref(false)
const tripSummary = ref(null)
const billing = ref(null)

const TierMap = {
    0: "ENTRY",
    1: "BRONZE",
    2: "SILVER",
    3: "GOLD"
  };

  const getLoyaltyStatus = async (user) => {
    try {
      const response = await apiClient.getUserLoyaltyTierUpdate(user.id)
      if (response.success && response.tier != null) {
        user.loyaltyTier = response.tier
      }
    } catch (error) {
      console.error(`Error fetching user's tier`)
      console.error("Prev:", user.prevLoyaltyTier, "New:", user.loyaltyTier)
    }
    console.log("Prev:", user.prevLoyaltyTier, "New:", user.loyaltyTier);

    if (user.loyaltyTier !== user.prevLoyaltyTier) {
      if (user.loyaltyTier > user.prevLoyaltyTier) {
        toast.success("You have been promoted to " + TierMap[user.loyaltyTier] + " tier!");
      } else if (user.loyaltyTier < user.prevLoyaltyTier) {
        toast.error("You have been demoted to " + TierMap[user.loyaltyTier] + " tier!");
      }
      user.prevLoyaltyTier = user.loyaltyTier 
      console.log("AfterPrev:", user.prevLoyaltyTier, "New:", user.loyaltyTier);     
    }
  }

  const getLoyaltyStatusTwoArgs = (user, tier) => {  
      if (user.value.loyaltyTier < tier) {
        toast.success("You have been promoted to " + TierMap[tier] + " tier. it will apply to your next trip!")
      } else if (user.value.loyaltyTier > tier) {
        toast.error("You have been demoted to " + TierMap[tier] + " tier!");
      }
      user.value.prevLoyaltyTier = user.value.loyaltyTier

      let storedUser = JSON.parse(localStorage.getItem('user'))
      storedUser.prevLoyaltyTier = user.value.loyaltyTier

      localStorage.setItem('user', JSON.stringify(storedUser))
  }

onMounted(() => {
  // Load user data from localStorage
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
    getLoyaltyStatus(user.value)
  }
  // Initialize with clean state
  currentReservation.value = null
  currentTrip.value = null
  // Load stations and check for active trip only
  loadStations()
  checkActiveTrip()
  // start polling reservations so UI stays in sync with backend expiry
  checkActiveReservation()
  reservationInterval = setInterval(checkActiveReservation, 15000) // every 15s
})

onBeforeUnmount(() => {
  if (reservationInterval) {
    clearInterval(reservationInterval)
    reservationInterval = null
  }
})

const handleLogout = () => {
  console.log('🚪 Logout button clicked')
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  console.log('🚪 Navigating to /login')
  // Use replace instead of push to avoid navigation guard issues
  router.replace('/login')
}

// Load station counts from API
const stationCounts = ref({})

const loadStationCounts = async () => {
  for (const station of stations.value) {
    if (!stationCounts.value[station.id]) {
      try {
        const [freeDocksResponse, availableBikesResponse] = await Promise.all([
          apiClient.getNumberOfFreeDocks(station.id),
          apiClient.getNumberOfAvailableBikes(station.id)
        ])
        // Handle both number response and object response
        const freeDocks = typeof freeDocksResponse === 'number' ? freeDocksResponse : (freeDocksResponse?.count || freeDocksResponse || 0)
        const availableBikes = typeof availableBikesResponse === 'number' ? availableBikesResponse : (availableBikesResponse?.count || availableBikesResponse || 0)
        
        stationCounts.value[station.id] = {
          freeDocks: freeDocks,
          availableBikes: availableBikes
        }
      } catch (error) {
        console.error(`Error loading counts for station ${station.id}:`, error)
        // Fallback to local calculation if API fails
        stationCounts.value[station.id] = {
          freeDocks: station.dockIds ? station.dockIds.filter(dock => dock.status === 'EMPTY').length : 0,
          availableBikes: station.dockIds ? station.dockIds.filter(dock => dock.bikeId && dock.status === 'OCCUPIED').length : 0
        }
      }
    }
  }
}

const loadStations = async () => {
  try {
    console.log('loadStations called')
    loading.value = true
    const stationsData = await apiClient.getAllStations()
    console.log('Stations data received:', stationsData)
    stations.value = stationsData
    console.log('Stations set to:', stations.value)
    // Load station counts from API
    await loadStationCounts()
  } catch (error) {
    console.error('Error loading stations:', error)
  } finally {
    loading.value = false
  }
}

const showStations = () => {
  // toggle stations and ensure ride history is closed
  showStationsList.value = !showStationsList.value
  if (showStationsList.value) {
    showRideHistoryList.value = false
    showPricingList.value = false
    showBillingHistoryList.value = false
    if (stations.value.length === 0) loadStations()
  }
}

const showRideHistory = () => {
  // toggle ride history and ensure stations list is closed
  showRideHistoryList.value = !showRideHistoryList.value
  if (showRideHistoryList.value) {
    showStationsList.value = false
    showPricingList.value = false
    showBillingHistoryList.value = false
  }
}

const showBillingHistory = () => {
  // toggle billing history and ensure other sections are closed
  showBillingHistoryList.value = !showBillingHistoryList.value
  if (showBillingHistoryList.value) {
    showStationsList.value = false
    showPricingList.value = false
    showRideHistoryList.value = false
  }
}

const showPricing = () => {
  // toggle pricing and ensure other sections are closed
  showPricingList.value = !showPricingList.value
  if (showPricingList.value) {
    showStationsList.value = false
    showRideHistoryList.value = false
    showBillingHistoryList.value = false
  }
}
// check if user currently has an incomplete trip and populate currentTrip
const checkActiveTrip = async () => {
  try {
    if (!user.value?.id) return
    if (!apiClient.getUserTrips) {
      currentTrip.value = null
      return
    }
    const resp = await apiClient.getUserTrips(user.value.id)
    if (resp && resp.success && Array.isArray(resp.trips)) {
      const incomplete = resp.trips.find(t => !t.tripComplete)
      if (incomplete) {
        currentTrip.value = {
          id: incomplete.tripId || incomplete.id,
          bikeId: incomplete.bikeId || incomplete.bike?.id,
          startTime: incomplete.startTime || incomplete.startDateTime
        }
        return
      }
    }
    currentTrip.value = null
  } catch (err) {
    console.error('Error checking active trip:', err)
    currentTrip.value = null
  }
}

// Poll reservations and clear expired ones
const checkActiveReservation = async () => {  
    
    try {
      if (!user.value?.id) return

      const response = await apiClient.getUserReservations(user.value.id)
      const hasReservations = response && response.success && response.reservations && response.reservations.length > 0

      // Backend removed reservation (expired) — notify user
      if (!hasReservations) {
        if (prevReservationId.value) {
          prevReservationId.value = null
          currentReservation.value = null
          console.log('Reservation expired (backend removed it) — notifying user')
          alert('Your bike reservation has expired.')
          await loadStations()
        } else {
          currentReservation.value = null
        }
        return
      }
      
      const reservation = response.reservations[0]
      const reservationId = reservation.reservationId || reservation.id
      const expiryRaw = reservation.expiryDateTime || reservation.expiryTime
      const expiry = expiryRaw ? new Date(expiryRaw) : null
      const now = new Date()

      // remember this reservation so we can detect deletion next poll
      prevReservationId.value = reservationId

      // If expiry present and passed — expired locally
      if (expiry && expiry <= now) {
        prevReservationId.value = null
        currentReservation.value = null
        console.log('Reservation expired locally — notifying user')
        alert('Your bike reservation has expired.')
        loadStations().catch(e => console.warn('Failed to reload stations after expiry:', e))
        return
      }
      
      let stationName = null
      if (reservation.station) {
        stationName = reservation.station.name || reservation.stationName || null
      } else {
        const full = await fetchReservationDetails(reservationId, user.value?.id)
        stationName = resolveStationNameFromFullReservation(full) || resolveStationNameFromReservation(reservation) || currentReservation.value?.stationName || 'Station'
      }

      currentReservation.value = {
        id: reservationId,
        bikeId: reservation.bike?.id || reservation.bikeId,
        stationName,
        expiryTime: expiryRaw
      }
    } catch (error) {
      console.error('Error checking reservations:', error)
      currentReservation.value = null
    }
  }

  const fetchReservationDetails = async (reservationId, userId) => {
    try {
      if (reservationId && apiClient.getReservation) {
        const full = await apiClient.getReservation(reservationId)
        if (full) return full
      }
    } catch (e) {
      console.warn('getReservation failed:', e)
    }
    return null
  }

  const resolveStationNameFromFullReservation = (reservation) => {
    if (!reservation) return null
    if (reservation.station && typeof reservation.station === 'object') {
      return reservation.station.name || reservation.station.stationName || null
    }
    if (reservation.stationName) return reservation.stationName
    const dockId = reservation.bike?.dockId ?? reservation.dockId ?? reservation.bike?.dock
    if (dockId != null) {
      // map dockId -> station using loaded stations
      for (const s of stations.value || []) {
        if ((s.dockIds || []).some(d => String(d.id) === String(dockId) || String(d.dockId) === String(dockId))) {
          return s.name
        }
      }
    }
    return null
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
          id: response.reservationId, 
          bikeId: response.bike.id,
          stationName: response.station, 
          expiryTime: response.expiryDateTime 
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

          prevReservationId.value = null
          currentReservation.value = null

          if (response.tier != null) {
            getLoyaltyStatusTwoArgs(user, response.tier)
          }

          const res = await apiClient.calculatePrice(currentTrip.value.id)
          console.log("Billing response:", res)
          tripSummary.value = res.billing
          billing.value = res.billing

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
      // Use API count if available, otherwise fallback to local calculation
      if (stationCounts.value[station.id]) {
        return stationCounts.value[station.id].availableBikes
      }
      // Fallback to local calculation
      if (!station.dockIds) return 0
      return station.dockIds.filter(dock => dock.bikeId && dock.status === 'OCCUPIED').length
    }

    const getFreeDocksCount = (station) => {
      // Use API count if available, otherwise fallback to local calculation
      if (stationCounts.value[station.id]) {
        return stationCounts.value[station.id].freeDocks
      }
      // Fallback to local calculation
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
  transition: all 0.25s ease;
}

.action-btn.secondary.selected {
  background: var(--gradient);
  color: white;
  border-color: transparent;
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
  transform: translateY(-2px);
}

.action-btn.secondary.selected .btn-icon,
.action-btn.secondary.selected svg {
  color: white;
  stroke: currentColor;
  fill: none;
}


.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.action-btn.secondary:not(.selected):hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.08);
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
.role-badge {
  background: var(--primary);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
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

/* Pricing List */
.pricing-list {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
}

@media (max-width: 768px) {
  .stations-grid {
    grid-template-columns: 1fr;
  }
}
</style>
