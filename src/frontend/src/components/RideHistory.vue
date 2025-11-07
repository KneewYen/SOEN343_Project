<template>
  <div class="ride-history">
    <!-- Theme Toggle -->
    <ThemeToggle />

    <!-- Header -->
    <header class="header">
      <h1>Ride History</h1>
      <p class="subtitle">Find and review your past rides easily</p>
    </header>

    <!-- Search and Filters -->
    <section class="filters">
      <input
        v-model="searchId"
        type="text"
        placeholder="Search by Trip ID"
        class="search-input"
      />

      <div class="filter-row">
        <div class="filter-group">
          <label>Start Date</label>
          <input type="date" v-model="startDate" />
        </div>
        <div class="filter-group">
          <label>End Date</label>
          <input type="date" v-model="endDate" />
        </div>

        <div class="filter-group">
          <label>Bike Type</label>
          <select v-model="bikeType">
            <option value="">All</option>
            <option value="standard">Standard</option>
            <option value="e-bike">E-Bike</option>
          </select>
        </div>

        <button class="filter-btn" @click="clearFilters">Clear Filters</button>
      </div>
    </section>

    <!-- Results -->
    <section v-if="filteredRides.length" class="results">
      <table class="rides-table">
        <thead>
          <tr>
            <th>Trip ID</th>
            <th>Username</th>
            <th>Route</th>
            <th>Bike Type</th>
            <th>Cost ($)</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="ride in filteredRides" :key="ride.tripId"
          class="ride-row"@click="openRide(ride)"tabindex="0"@keydown.enter="openRide(ride)">
            <td>{{ ride.tripId }}</td>
            <td>{{ ride.userName }}</td>
            <td>{{ ride.startStationName }} → {{ ride.endStationName }}</td>
            <td>{{ ride.bikeType }}</td>
            <td>{{ formatCurrency(getTripCost(ride).total) }}</td>
          </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button
          class="load-more"
          v-if="hasMore"
          @click="loadMore"
        >
        Load More
        </button>
      </div>
    </section>

    <!-- No Results -->
    <section v-else-if="errors.date || errors.tripId" class="error">
      <p v-if="errors.date">{{ errors.date }}</p> 
      <p v-if="errors.tripId">{{ errors.tripId }}</p>
    </section>

    <section v-else class="no-results">
      <p>No rides found. Try clearing filters or adjusting your search.</p>
    </section>

    <!-- Ride details -->
    <RideHistoryDetails
      v-if="selectedRide"
      :ride="selectedRide"
      @close="closeModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import apiClient from '../lib/api'
import ThemeToggle from './ThemeToggle.vue'
import RideHistoryDetails from './RideHistoryDetails.vue'

const props = defineProps({
  user: {
    type: Object,
    default: null
  }
})

const rides = computed(() => rideHistory.value)

const rideHistory = ref([])

const searchId = ref('')
const startDate = ref('')
const endDate = ref('')
const bikeType = ref('')
const selectedRide = ref(null)

const clearFilters = () => {
  searchId.value = ''
  startDate.value = ''
  endDate.value = ''
  bikeType.value = ''
  currentPage.value = 1
}

// Computed: Filtered rides
const filteredRides = computed(() => {

  if (errors.value.date || errors.value.tripId) {
    return []
  }

  return rides.value.filter((r) => {
    const matchesTrip = !searchId.value || String(r.tripId).trim() === String(searchId.value).trim()
    const matchesType = !bikeType.value || r.bikeType === bikeType.value
    const matchesStart = !startDate.value || r.startTime >= startDate.value
    const matchesEnd = !endDate.value || r.endTime <= endDate.value
    return matchesTrip && matchesType && matchesStart && matchesEnd && r.tripComplete
  })
})

const currentPage = ref(0)
const pageSize = 3
const hasMore = ref(true)

// Change this to connect with backend when Pricing is implemented
//Start Here
const BASE_FEE = 5.0
const PER_MINUTE = 1.00
const EBIKE_SURCHARGE_PER_MINUTE = 0.10

function getDurationMinutes(ride) {
  try {
    if (!ride.startTime || !ride.endTime) return 0
    const start = new Date(ride.startTime)
    const end = new Date(ride.endTime)
    const mins = Math.max(0, Math.round((end - start) / 60000))
    return mins
  } catch (e) {
    return 0
  }
}

function getTripCost(ride) {
  const duration = getDurationMinutes(ride)
  const base = BASE_FEE
  const perMin = +(duration * PER_MINUTE).toFixed(2)
  const ebike = ride.bikeType === 'e-bike' ? +(duration * EBIKE_SURCHARGE_PER_MINUTE).toFixed(2) : 0
  const total = +(base + perMin + ebike).toFixed(2)
  return { base, perMin, ebike, total, duration }
}

function formatCurrency(val) {
  return typeof val === 'number' ? val.toFixed(2) : val
}

//End here

const loadRides = async () => {
  try {
    let response;
    if (props.user.role === 'operator') {
      response = await apiClient.getAllTrips(currentPage.value, pageSize);
    } else {
      response = await apiClient.getUserTrips(props.user.id, currentPage.value, pageSize);
    }

    const pageData = response.trips.content
    const lastPage = response.trips.last

    rideHistory.value.push(...pageData)

    hasMore.value = !lastPage

    console.log(rideHistory.value)

  } catch (error) {
    console.error("Error loading ride history:", error)
  } 
}

onMounted(() => {
  loadRides()
})

const loadMore = () => {
  currentPage.value++;
  loadRides();
}

const errors = ref({
  date: '',
  tripId: ''
})

const validateFilters = () => {
  errors.value.date = ''
  errors.value.tripId = ''

  if (searchId.value && isNaN(Number(searchId.value))) {
    errors.value.tripId = 'Trip ID must be a valid number. Try clearing filters or adjusting your search.'
  }

  if (startDate.value && endDate.value) {
    if (startDate.value > endDate.value) {
      errors.value.date = 'Start date cannot be after end date. Try clearing filters or adjusting your search.'
    }
  }
}

watch([searchId, startDate, endDate], validateFilters)

// open/close ride details
const openRide = (ride) => {
  selectedRide.value = ride
}

const closeModal = () => {
  selectedRide.value = null
}

</script>

<style scoped>
.ride-history {
  padding: 2rem;
  border-radius: 16px;             
  background: var(--surface);
  min-height: 100vh;
  color: var(--text-primary);
}

.header {
  text-align: center;
  margin-bottom: 2rem;
}

.header h1 {
  font-size: 2rem;
  color: var(--primary);
}

.subtitle {
  color: var(--text-secondary);
  margin-top: 0.5rem;
}

.filters {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background: var(--surface);
  padding: 20px;
  border-radius: 16px;                 
  border: 1px solid var(--border-color);           
  box-shadow: var(--card-shadow);
  margin-bottom: 1.5rem;
  color: var(--text-primary);
}

.search-input {
  padding: 0.7rem 1rem;
  border-radius: 16px;   
  border: 1px solid var(--border-color);
  background: var(--surface);
  color: var(--text-primary);
}

.search-input::placeholder {
  color: var(--text-secondary);
  opacity: 1;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.filter-group label {
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-bottom: 0.3rem;
}

.filter-group input,
.filter-group select {
  padding: 0.5rem;
  border-radius: 0.4rem;
  border: 1px solid var(--border-color);
  background: var(--surface);
  color: var(--text-primary);
}

.filter-btn {
  align-self: flex-end;
  background: var(--primary);
  color: white;
  border: none;
  padding: 0.6rem 1rem;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background 0.3s ease;
}

.filter-btn:hover {
  background: var(--accent-hover);
}


.results {
  background: var(--surface);
  border-radius: 1rem;
  box-shadow: var(--card-shadow);
  overflow-x: auto;
}

.rides-table {
  width: 100%;
  border-collapse: collapse;
}

.rides-table th,
.rides-table td {
  padding: 0.8rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.rides-table th {
  color: var(--text-secondary);
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.85rem;
}

.rides-table tbody .ride-row {
  cursor: pointer;
  transition: transform 180ms cubic-bezier(.2,.9,.2,1), box-shadow 180ms ease, background-color 160ms;
  transform: translateY(0);
  will-change: transform, box-shadow, background-color;
  position: relative;
  z-index: 0;
}

.rides-table tbody .ride-row:hover,
.rides-table tbody .ride-row:focus {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(2,6,23,0.12);
  background: rgba(0,0,0,0.02); 
  z-index: 2;
}


.pagination {
  display: flex;
  justify-content: center;
  padding: 1rem;
}

.load-more {
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 0.5rem;
  padding: 0.7rem 1.5rem;
  cursor: pointer;
  transition: background 0.3s ease;
}

.load-more:hover {
  background: var(--primary-hover);
}

.no-results {
  text-align: center;
  padding: 2rem;
  color: var(--text-secondary);
}

.error {
  padding: 2rem;
  text-align: center;
  color: #d9534f;
  font-size: 0.85rem;
  margin-top: -0.5rem;
}

</style>