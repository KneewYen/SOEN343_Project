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
          <tr v-for="ride in filteredRides" :key="ride.tripId">
            <td>{{ ride.tripId }}</td>
            <td>{{ ride.userName }}</td>
            <td>{{ ride.startStationName }} → {{ ride.endStationName }}</td>
            <td>{{ ride.bikeType }}</td>
            <td>{{ 0 }}</td>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import apiClient from '../lib/api'
import ThemeToggle from './ThemeToggle.vue'

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

</script>

<style scoped>
.ride-history {
  padding: 2rem;
  border-radius: 16px;             
  background: var(--bg-primary);
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
  background: white;
  padding: 20px;
  border-radius: 16px;                 
  border: 1px solid #e2e8f0;           
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
  margin-bottom: 1.5rem;
}

.search-input {
  padding: 0.7rem 1rem;
  border-radius: 16px;   
  border: 1px solid var(--border-color);
  background: var(--surface);
  color: var(--text-primary);
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
  background: var(--accent-color);
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