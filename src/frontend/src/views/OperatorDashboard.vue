<template>
  <div class="operator-dashboard">
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
            <span class="role-badge">Operator</span>
          </div>
          <div class="user-info">
            <span class="welcome-text">Welcome, {{ user?.fullName || 'Operator' }}!</span>
            <button @click="handleLogout" class="logout-btn">Logout</button>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <main class="dashboard-main">
        <div class="dashboard-grid">
          <!-- System Overview -->
          <section class="system-overview">
            <h2 class="section-title">System Overview</h2>
            <div class="overview-cards">
              <div class="overview-card">
                <div class="card-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <path d="M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    <circle cx="8" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                    <circle cx="16" cy="16" r="2" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </div>
                <div class="card-content">
                  <div class="card-value">{{ systemStats.availableBikes }}</div>
                  <div class="card-label">Available Bikes</div>
                </div>
              </div>
              <div class="overview-card">
                <div class="card-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </div>
                <div class="card-content">
                  <div class="card-value">{{ systemStats.activeStations }}</div>
                  <div class="card-label">Active Stations</div>
                </div>
              </div>
              <div class="overview-card">
                <div class="card-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <div class="card-content">
                  <div class="card-value">{{ systemStats.activeUsers }}</div>
                  <div class="card-label">Active Users</div>
                </div>
              </div>
              <div class="overview-card">
                <div class="card-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <div class="card-content">
                  <div class="card-value">{{ systemStats.maintenance }}</div>
                  <div class="card-label">Maintenance</div>
                </div>
              </div>
            </div>
          </section>

          <!-- Quick Actions -->
          <section class="quick-actions">
            <h2 class="section-title">Quick Actions</h2>
            <div class="action-buttons">
              <button @click="showRebalanceModal = true" class="action-btn secondary">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M18 20V10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M12 20V4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M6 20v-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="btn-text">Rebalance Bikes</span>
              </button>
              <button @click="showStationManagement = true" class="action-btn secondary">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </span>
                <span class="btn-text">Manage Stations</span>
              </button>
              <button @click="showBikeManagement = true" class="action-btn secondary">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="btn-text">Bike Maintenance</span>
              </button>
              <button class="action-btn secondary">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="btn-text">View Analytics</span>
              </button>
              <button @click="showDockManagement = true" class="action-btn secondary">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="btn-text">Dock Maintenance</span>
              </button>
              <button @click="resetSystem" class="action-btn secondary">
                <span class="btn-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                     <path d="M21 12a9 9 0 10-9 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
+                    <path d="M21 3v6h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <span class="btn-text">Reset System</span>
              </button>
            </div>
          </section>

          <!-- Recent Activity -->
          <section class="recent-activity">
            <h2 class="section-title">Recent Activity</h2>
            <div class="activity-list">
              <div class="activity-item" v-for="event in events" :key="event.id">
                <!-- <div class="activity-icon">Add icons here</div> -->
                <div class="activity-content">
                  <div class="activity-title">{{ event.description }}</div>
                  <div class="activity-time">{{ formatTimestamp(event.timestamp) }}</div>
                </div>
              </div>
            </div>
          </section>


          <!-- Station Status -->
          <section class="station-status">
            <h2 class="section-title">Station Status</h2>
            <div class="stations-grid">
              <div v-for="station in stations" :key="station.id" class="station-card">
                <div class="station-header">
                  <div class="station-info">
                    <h3 class="station-name">{{ station.name }}</h3>
                    <p class="station-address">{{ station.address }}</p>
                  </div>
                  <div class="station-summary">
                    <div class="bike-count">
                      <span class="count-number">{{ getAvailableBikesCount(station) }}</span>
                      <span class="count-total">/{{ station.capacity }}</span>
                      <span class="count-label">bikes</span>
                    </div>
                    <div class="status-badge" :class="getStationStatusClass(station)">
                      {{ station.status }}
                    </div>
                  </div>
                </div>
                
                <div class="docks-grid">
                  <div v-for="dock in station.dockIds" :key="dock.id" class="dock-card" :class="getDockStatusClass(dock)">
                    <div class="dock-header">
                      <span class="dock-id">Dock {{ dock.id }}</span>
                      <span class="dock-status">{{ dock.status }}</span>
                    </div>
                    <div v-if="dock.bike" class="bike-info">
                      <span class="bike-id">Bike #{{ dock.bike.id }}</span>
                      <span class="bike-status" :class="getBikeStatusClass(dock.bike.status)">
                        {{ dock.bike.status || 'N/A' }}
                      </span>
                    </div>
                    <div v-else class="empty-dock">
                      <span class="empty-text">Empty</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>
      </main>
    </div>

    <!-- Rebalance Bikes Modal -->
    <div v-if="showRebalanceModal" class="modal-overlay" @click="showRebalanceModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Rebalance Bikes</h3>
          <button @click="showRebalanceModal = false" class="close-btn">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Source Station:</label>
            <select v-model="rebalanceForm.sourceStationId" class="form-select">
              <option value="">Select source station</option>
              <option v-for="station in stations" :key="station.id" :value="station.id">
                {{ station.name }} ({{ getAvailableBikesCount(station) }} bikes)
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Destination Station:</label>
            <select v-model="rebalanceForm.destinationStationId" class="form-select">
              <option value="">Select destination station</option>
              <option v-for="station in stations" :key="station.id" :value="station.id">
                {{ station.name }} ({{ getFreeDocksCount(station) }} free docks)
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Number of Bikes:</label>
            <input 
              v-model.number="rebalanceForm.numberOfBikes" 
              type="number" 
              min="1" 
              max="10" 
              class="form-input"
            />
          </div>
          <div class="modal-actions">
            <button @click="executeRebalance" class="action-btn primary" :disabled="loading">
              <span v-if="loading">Rebalancing...</span>
              <span v-else>Rebalance Bikes</span>
            </button>
            <button @click="showRebalanceModal = false" class="action-btn secondary">
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Station Management Modal -->
    <div v-if="showStationManagement" class="modal-overlay" @click="showStationManagement = false">
      <div class="modal-content large" @click.stop>
        <div class="modal-header">
          <h3>Station Management</h3>
          <button @click="showStationManagement = false" class="close-btn">&times;</button>
        </div>
        <div class="modal-body">
          <div class="stations-management">
            <div v-for="station in stations" :key="station.id" class="station-management-item">
              <div class="station-info">
                <h4>{{ station.name }}</h4>
                <p>{{ station.address }}</p>
                <p>Available bikes: {{ getAvailableBikesCount(station) }} | Free docks: {{ getFreeDocksCount(station) }}</p>
              </div>
              <div class="station-actions">
                <button 
                  @click="toggleStationStatus(station.id)" 
                  class="action-btn"
                  :class="station.status === 'ACTIVE' ? 'danger' : 'success'"
                >
                  {{ station.status === 'ACTIVE' ? 'Set Out of Service' : 'Set Active' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bike Management Modal -->
    <div v-if="showBikeManagement" class="modal-overlay" @click="showBikeManagement = false">
      <div class="modal-content large" @click.stop>
        <div class="modal-header">
          <h3>Bike Management</h3>
          <button @click="showBikeManagement = false" class="close-btn">&times;</button>
        </div>
        <div class="modal-body">
          <div class="bikes-management">
            <div v-for="station in stations" :key="station.id" class="station-bikes">
              <h4>{{ station.name }}</h4>
              <div class="bikes-list">
                <div v-for="dock in station.dockIds" :key="dock.id" class="bike-item">
                  <div class="bike-info">
                    <span v-if="dock.bike">Bike #{{ dock.bike.id }}</span>
                    <span v-else>Empty Dock</span>
                    <span class="dock-status" :class="(dock.bike?.status || dock.status).toLowerCase().replace('_', '-')">{{ dock.bike?.status || dock.status }}</span>
                  </div>
                  <div v-if="dock.bike" class="bike-actions">
                    <button 
                      @click="toggleBikeStatus(dock.bike.id)" 
                      class="action-btn small"
                    >
                      Toggle Status
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Dock Management Modal -->
    <div v-if="showDockManagement" class="modal-overlay" @click="showDockManagement = false">
      <div class="modal-content large" @click.stop>
        <div class="modal-header">
          <h3>Dock Management</h3>
          <button @click="showDockManagement = false" class="close-btn">&times;</button>
        </div>
        <div class="modal-body">
          <div class="bikes-management">
            <div v-for="station in stations" :key="station.id" class="station-bikes">
              <h4>{{ station.name }}</h4>
              <div class="bikes-list">
                <div v-for="dock in station.dockIds" :key="dock.id" class="bike-item">
                  <div class="bike-info">
                    <span>Dock #{{ dock.id }}</span>
                    <span class="dock-status" :class="(dock.status).toLowerCase().replace('_', '-')">{{dock.status }}</span>
                  </div>
                  <div v-if="dock.id" class="bike-actions">
                    <button 
                      @click="toggleDockStatus(dock.id)" 
                      class="action-btn small"
                    >
                      Toggle Status
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ThemeToggle from '../components/ThemeToggle.vue'
import apiClient from '../lib/api'

export default {
  name: 'OperatorDashboard',
  components: {
    ThemeToggle
  },
  setup() {
    const router = useRouter()
    const user = ref(null)
    const stations = ref([])
    const loading = ref(false)
    const showRebalanceModal = ref(false)
    const showStationManagement = ref(false)
    const showBikeManagement = ref(false)
    const showDockManagement = ref(false)
    const events = ref([])
    const rebalanceForm = ref({
      sourceStationId: '',
      destinationStationId: '',
      numberOfBikes: 1
    })

    // System statistics
    const systemStats = ref({
      availableBikes: 0,
      activeStations: 0,
      activeUsers: 0,
      stationsInMaintenance: 0,
      bikesInMaintenance: 0,
      docksOutOfService: 0
    })

    onMounted(() => {
      // Load user data from localStorage
      const userData = localStorage.getItem('user')
      if (userData) {
        user.value = JSON.parse(userData)
      }
      // Load stations data
      loadStations()
      loadEvents()
    })

    const handleLogout = () => {
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      router.push('/login')
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
        loading.value = true
        const stationsData = await apiClient.getAllStations()
        stations.value = stationsData
        // Load station counts from API
        await loadStationCounts()
        calculateSystemStats()
      } catch (error) {
        console.error('Error loading stations:', error)
      } finally {
        loading.value = false
      }
    }

    const normalize = (s) => String(s || '').toLowerCase().replace(/[_\s]+/g, '-')

    const calculateSystemStats = () => {
      let availableBikes = 0
      let activeStations = 0
      let maintenance = 0

      for (const station of stations.value || []) {
        const stationStatus = normalize(station.status)
        if (stationStatus === 'active') {
          activeStations++
        } else {
          maintenance++
        }

        const docks = station.dockIds || []
        for (const d of docks) {
          const dockStatus = normalize(d.status)
          if (dockStatus === 'out-of-service' || dockStatus === 'maintenance') {
            maintenance++
          }

          if (d.bike && (d.bike.id || d.bike.id === 0)) {
            // count available bikes by dock.status === 'OCCUPIED'
            if (normalize(d.status) === 'occupied') availableBikes++

            const bikeStatus = normalize(d.bike.status)
            if (bikeStatus === 'maintenance' || bikeStatus.includes('maint')) {
              maintenance++
            }
          }
        }
    }

      systemStats.value = {
        availableBikes,
        activeStations,
        activeUsers: 2, // Hardcoded for now - in real app would fetch from API
        maintenance
      }
    }
    
    const loadEvents = async () => {
      try {
        const resp = await apiClient.getRecentEvents()

        let items = []
        if (!resp) {
          items = []
        } else if (Array.isArray(resp)) {
          items = resp
        } else if (Array.isArray(resp.events)) {
          items = resp.events
        } else if (Array.isArray(resp.data)) {
          items = resp.data
        } else if (resp.event) {
          items = [resp.event]
        } else {
          items = [resp]
        }

        events.value = items.map(e => ({
          id: e.id ?? e.eventId ?? e._id ?? Math.random().toString(36).slice(2),
          description: e.description ?? e.message ?? e.msg ?? JSON.stringify(e),
          timestamp: e.timestamp ?? e.time ?? e.createdAt ?? e.date ?? null
        }))
      } catch(e) {
        console.error('Failed loading events:', e)
        events.value = []
      }
    }

    const getAvailableBikesCount = (station) => {
      // Use API count if available, otherwise fallback to local calculation
      if (stationCounts.value[station.id]) {
        return stationCounts.value[station.id].availableBikes
      }
      // Fallback to local calculation
      if (!station.dockIds) return 0
      return station.dockIds.filter(dock => dock.bike?.id && dock.status === 'OCCUPIED').length
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

    const executeRebalance = async () => {
      try {
        loading.value = true
        const result = await apiClient.rebalanceBikes(
          rebalanceForm.value.sourceStationId,
          rebalanceForm.value.destinationStationId,
          rebalanceForm.value.numberOfBikes,
          user.value.id
        )
        alert(result.message || result)
        showRebalanceModal.value = false
        rebalanceForm.value = {
          sourceStationId: '',
          destinationStationId: '',
          numberOfBikes: 1
        }
        // Reload stations to reflect changes
        await loadStations()
        await loadEvents()
      } catch (error) {
        alert('Failed to rebalance bikes: ' + error.message)
      } finally {
        loading.value = false
      }
    }

    const toggleStationStatus = async (stationId) => {
      try {
        const result = await apiClient.toggleStation(stationId, user.value.id)
        alert(result.message || result)
        await loadStations()
        await loadEvents()
      } catch (error) {
        alert('Failed to toggle station status: ' + error.message)
      }
    }

    const toggleBikeStatus = async (bikeId) => {
      try {
        const result = await apiClient.toggleBike(bikeId, user.value.id)
        if (result.success) {
          alert(result.message || 'Bike status updated successfully')
          // Reload stations to get the latest data from backend
          await loadStations()
          await loadEvents()
        } else {
          alert(result.message || 'Failed to toggle bike status')
        }
      } catch (error) {
        alert('Failed to toggle bike status: ' + error.message)
      }
    }

    const getStationStatusClass = (station) => {
      if (station.status !== 'ACTIVE') return 'inactive'
      if (station.count === 0) return 'empty'
      if (station.count < station.capacity * 0.3) return 'low'
      return 'available'
    }

    const getDockStatusClass = (dock) => {
      if (dock.status === 'OUT_OF_SERVICE') return 'out-of-service'
      if (dock.status === 'OCCUPIED') return 'occupied'
      return 'empty'
    }

    const getBikeStatusClass = (bikeStatus) => {
      if (bikeStatus === 'MAINTENANCE') return 'maintenance'
      if (bikeStatus === 'ON_TRIP') return 'on-trip'
      if (bikeStatus === 'RESERVED') return 'reserved'
      return 'available'
    }

    const toggleDockStatus = async (dockIds) => {
      try {
        const result = await apiClient.toggleDock(dockIds, user.value.id)
        if (result.success) {
          alert(result.message || 'Dock status updated successfully')
          // Reload stations to get the latest data from backend
          await loadStations()
          await loadEvents()
        } else {
          alert(result.message || 'Failed to toggle dock status')
        }
      } catch (error) {
        alert('Failed to toggle dock status: ' + error.message)
      }
    }

    const formatTimestamp = (ts) => {
      if (!ts) return ''
      const d = new Date(ts)
      if (Number.isNaN(d.getTime())) return String(ts)
      return new Intl.DateTimeFormat(undefined, {
        dateStyle: 'medium',
        timeStyle: 'short'
      }).format(d)
    }

    const resetSystem = async () => {
      if (!confirm('Are you sure you want to reset the entire system?')) {
        return
      }
      try {
        const result = await apiClient.resetSystem()
        if(result.success){
          alert(result.message || 'System has been reset successfully')
          // Reload stations and events to reflect changes
          await loadStations()
          await loadEvents()
        } else {
          alert(result.message || 'Failed to reset system')
        } 
      } catch (error) {
        alert('Failed to reset system: ' + error.message)
      }
  
    }

    return {
      user,
      handleLogout,
      stations,
      loading,
      showRebalanceModal,
      showStationManagement,
      showBikeManagement,
      showDockManagement,
      rebalanceForm,
      systemStats,
      events,
      formatTimestamp,
      getAvailableBikesCount,
      getFreeDocksCount,
      executeRebalance,
      toggleStationStatus,
      toggleBikeStatus,
      toggleDockStatus,
      getStationStatusClass,
      getDockStatusClass,
      getBikeStatusClass,
      resetSystem
    }
  }
}
</script>

<style scoped>
.operator-dashboard {
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

.role-badge {
  background: var(--primary);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
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

.system-overview {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.overview-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: var(--surface-hover);
  border-radius: 12px;
  border: 1px solid var(--border);
}

.card-icon {
  font-size: 32px;
}

.card-content {
  flex: 1;
}

.card-value {
  font-size: 24px;
  font-weight: 800;
  color: var(--primary);
  margin-bottom: 4px;
}

.card-label {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 600;
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

.recent-activity {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--surface-hover);
  border-radius: 8px;
  border: 1px solid var(--border);
}

.activity-icon {
  font-size: 20px;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.activity-time {
  font-size: 14px;
  color: var(--text-secondary);
}

.station-status {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
}

.stations-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.station-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--surface-hover);
  border-radius: 12px;
  border: 1px solid var(--border);
}

.station-info {
  flex: 1;
}

.station-name {
  font-weight: 700;
  color: var(--text);
  margin-bottom: 4px;
}

.station-location {
  font-size: 14px;
  color: var(--text-secondary);
}

.station-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bike-count {
  font-weight: 600;
  color: var(--text);
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.status-indicator.available {
  background: var(--success);
}

.status-indicator.low {
  background: #f59e0b;
}

.status-indicator.maintenance {
  background: var(--error);
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
  
  .system-overview,
  .quick-actions,
  .recent-activity,
  .station-status {
    padding: 16px;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
  }
  
  .overview-card {
    padding: 12px;
  }
  
  .card-icon {
    font-size: 24px;
  }
  
  .card-value {
    font-size: 20px;
  }
}

@media (min-width: 481px) and (max-width: 768px) {
  .dashboard-main {
    padding: 20px;
  }
  
  .overview-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .system-overview {
    grid-column: 1 / -1;
  }
  
  .overview-cards {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .action-buttons {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1025px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .system-overview {
    grid-column: 1 / -1;
  }
  
  .overview-cards {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .action-buttons {
    grid-template-columns: repeat(2, 1fr);
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

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  border: 2px solid var(--border);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.modal-content.large {
  max-width: 800px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
  color: var(--text);
  font-size: 20px;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: var(--surface-hover);
  color: var(--text);
}

/* Form Styles */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--text);
}

.form-select, .form-input {
  width: 100%;
  padding: 12px;
  border: 2px solid var(--border);
  border-radius: 8px;
  background: var(--surface);
  color: var(--text);
  font-size: 16px;
  transition: all 0.3s ease;
}

.form-select:focus, .form-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

/* Station Management Styles */
.stations-management {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.station-management-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--surface-hover);
  border-radius: 12px;
  border: 2px solid var(--border);
}

.station-info h4 {
  margin: 0 0 8px 0;
  color: var(--text);
  font-size: 16px;
  font-weight: 600;
}

.station-info p {
  margin: 4px 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.station-actions {
  display: flex;
  gap: 8px;
}

/* Bike Management Styles */
.bikes-management {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.station-bikes h4 {
  margin: 0 0 12px 0;
  color: var(--text);
  font-size: 16px;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--border);
}

.bikes-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.bike-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: var(--surface-hover);
  border-radius: 8px;
  border: 1px solid var(--border);
}

.bike-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dock-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.dock-status.occupied {
  background: #dcfce7;
  color: #166534;
}

.dock-status.empty {
  background: #f3f4f6;
  color: #6b7280;
}

.dock-status.out-of-service {
  background: #fef2f2;
  color: #991b1b;
}

.dock-status.available {
  background: #dcfce7;
  color: #166534;
}

.dock-status.maintenance {
  background: #fef3c7;
  color: #92400e;
}

.dock-status.reserved {
  background: #dbeafe;
  color: #1e40af;
}

.dock-status.on-trip {
  background: #e0e7ff;
  color: #3730a3;
}

.bike-actions {
  display: flex;
  gap: 8px;
}

.action-btn.small {
  padding: 6px 12px;
  font-size: 14px;
}

.action-btn.danger {
  background: #fee2e2;
  color: #991b1b;
  border: 2px solid #fecaca;
}

.action-btn.danger:hover {
  background: #fecaca;
}

.action-btn.success {
  background: #dcfce7;
  color: #166534;
  border: 2px solid #bbf7d0;
}

.action-btn.success:hover {
  background: #bbf7d0;
}

/* Station Actions */
.station-actions {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.dock-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f3f4f6;
}

.dock-item:last-child {
  border-bottom: none;
}

.dock-info {
  font-size: 14px;
  color: #6b7280;
}

.action-btn.small {
  padding: 6px 12px;
  font-size: 14px;
}

/* Enhanced Station Status Styles */
.stations-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.station-card {
  background: var(--surface);
  border-radius: 16px;
  padding: 20px;
  border: 2px solid var(--border);
  box-shadow: var(--card-shadow);
  transition: all 0.3s ease;
}

.station-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.station-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--border);
}

.station-info h3 {
  margin: 0 0 8px 0;
  color: var(--text);
  font-size: 20px;
  font-weight: 700;
}

.station-address {
  margin: 0;
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.4;
}

.station-summary {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.bike-count {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.count-number {
  font-size: 24px;
  font-weight: 800;
  color: var(--primary);
}

.count-total {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-secondary);
}

.count-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.status-badge.available {
  background: #dcfce7;
  color: #166534;
}

.status-badge.low {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.empty {
  background: #fef2f2;
  color: #991b1b;
}

.status-badge.inactive {
  background: #f3f4f6;
  color: #6b7280;
}

.docks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.dock-card {
  background: var(--surface-hover);
  border-radius: 12px;
  padding: 16px;
  border: 2px solid var(--border);
  transition: all 0.3s ease;
}

.dock-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.dock-card.occupied {
  border-color: #22c55e;
  background: linear-gradient(135deg, #dcfce7 0%, #f0fdf4 100%);
}

.dock-card.empty {
  border-color: #e5e7eb;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
}

.dock-card.out-of-service {
  border-color: #ef4444;
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
}

.dock-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.dock-id {
  font-weight: 700;
  color: var(--text);
  font-size: 14px;
}

.dock-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}

.dock-status.OCCUPIED {
  background: #dcfce7;
  color: #166534;
}

.dock-status.EMPTY {
  background: #f3f4f6;
  color: #6b7280;
}

.dock-status.OUT_OF_SERVICE {
  background: #fef2f2;
  color: #991b1b;
}

.bike-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.bike-id {
  font-weight: 600;
  color: var(--text);
  font-size: 13px;
}

.bike-status {
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  display: inline-block;
  width: fit-content;
}

.bike-status.available {
  background: #dcfce7;
  color: #166534;
}

.bike-status.maintenance {
  background: #fef3c7;
  color: #92400e;
}

.bike-status.reserved {
  background: #dbeafe;
  color: #1e40af;
}

.bike-status.on-trip {
  background: #e0e7ff;
  color: #3730a3;
}

.empty-dock {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
}

.empty-text {
  color: var(--text-secondary);
  font-style: italic;
  font-size: 12px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .station-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .station-summary {
    align-items: flex-start;
  }
  
  .docks-grid {
    grid-template-columns: 1fr;
  }
  
  .dock-card {
    padding: 12px;
  }
}
</style>

