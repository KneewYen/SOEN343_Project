<template>
  <div class="billing-history">
    <!-- Theme Toggle -->
    <ThemeToggle />

    <!-- Header -->
    <header class="header">
      <h1>Billing History</h1>
      <p class="subtitle">View your billing records and charges</p>
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

    <!-- Results - Ledger View -->
    <section v-if="filteredBills.length" class="results">
      <div class="ledger-header">
        <h3>Billing Ledger</h3>
        <div class="ledger-summary">
          <span>Total Records: {{ filteredBills.length }}</span>
          <span>Total Amount: ${{ formatCurrency(totalAmount) }}</span>
        </div>
      </div>
      <table class="bills-table">
        <thead>
          <tr>
            <th>Date & Time</th>
            <th>Bike ID</th>
            <th>Origin Station</th>
            <th>Charges</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="bill in filteredBills" :key="bill.tripId"
          class="bill-row" @click="openBill(bill)" tabindex="0" @keydown.enter="openBill(bill)">
            <td>{{ formatDateTime(bill.startTime) }}</td>
            <td>{{ getBikeId(bill) }}</td>
            <td>{{ bill.startStationName || 'N/A' }}</td>
            <td class="charge-amount">${{ formatCurrency(getTripCost(bill).total) }}</td>
            <td>
              <span class="payment-status" :class="getPaymentStatusClass(bill)">
                {{ getPaymentStatus(bill) }}
              </span>
            </td>
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
      <p>No billing records found. Try clearing filters or adjusting your search.</p>
    </section>

    <!-- Bill details -->
    <BillingHistoryDetails
      v-if="selectedBill"
      :bill="selectedBill"
      @close="closeModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import apiClient from '../lib/api'
import ThemeToggle from './ThemeToggle.vue'
import BillingHistoryDetails from './BillingHistoryDetails.vue'

const props = defineProps({
  user: {
    type: Object,
    default: null
  }
})

const bills = computed(() => billingHistory.value)

const billingHistory = ref([])

const searchId = ref('')
const startDate = ref('')
const endDate = ref('')
const bikeType = ref('')
const selectedBill = ref(null)

const clearFilters = () => {
  searchId.value = ''
  startDate.value = ''
  endDate.value = ''
  bikeType.value = ''
  currentPage.value = 1
}

// Computed: Filtered bills
const filteredBills = computed(() => {

  if (errors.value.date || errors.value.tripId) {
    return []
  }

  return bills.value.filter((r) => {
    const matchesTrip = !searchId.value || String(r.tripId).trim() === String(searchId.value).trim()
    const matchesType = !bikeType.value || r.bikeType === bikeType.value
    const matchesStart = !startDate.value || r.startTime >= startDate.value
    const matchesEnd = !endDate.value || r.endTime <= endDate.value
    return matchesTrip && matchesType && matchesStart && matchesEnd && r.tripComplete
  })
})

const totalAmount = computed(() => {
  return filteredBills.value.reduce((sum, bill) => {
    return sum + getTripCost(bill).total
  }, 0)
})

const paymentStatuses = ref({}) // Map of tripId -> status

async function loadPaymentStatuses() {
  // Load payment statuses for all bills from database
  for (const bill of billingHistory.value) {
    try {
      const response = await apiClient.getBillingByTripId(bill.tripId)
      if (response.success && response.billing) {
        paymentStatuses.value[bill.tripId] = 'Paid'
      } else {
        paymentStatuses.value[bill.tripId] = 'Pending'
      }
    } catch (err) {
      paymentStatuses.value[bill.tripId] = 'Pending'
    }
  }
}

function getPaymentStatus(bill) {
  // Check database status first
  if (paymentStatuses.value[bill.tripId]) {
    return paymentStatuses.value[bill.tripId]
  }
  // Fall back to localStorage for backward compatibility
  const paidTrips = JSON.parse(localStorage.getItem('paidTrips') || '[]')
  if (paidTrips.includes(bill.tripId)) {
    return 'Paid'
  }
  return bill.paymentStatus || 'Pending'
}

function getPaymentStatusClass(bill) {
  const status = getPaymentStatus(bill).toLowerCase()
  if (status === 'paid' || status === 'completed') return 'paid'
  if (status === 'pending') return 'pending'
  if (status === 'failed') return 'failed'
  return 'pending'
}

const currentPage = ref(0)
const pageSize = 3
const hasMore = ref(true)

// Pricing calculation
const BASE_FEE = 1.0
const PER_MINUTE = 0.15
const EBIKE_SURCHARGE_PER_MINUTE = 0.10

function getDurationMinutes(bill) {
  try {
    if (!bill.startTime || !bill.endTime) return 0
    const start = new Date(bill.startTime)
    const end = new Date(bill.endTime)
    const mins = Math.max(0, Math.round((end - start) / 60000))
    return mins
  } catch (e) {
    return 0
  }
}

function getTripCost(bill) {
  const duration = getDurationMinutes(bill)
  const base = BASE_FEE
  const perMin = +(duration * PER_MINUTE).toFixed(2)
  const ebike = bill.bikeType === 'e-bike' ? +(duration * EBIKE_SURCHARGE_PER_MINUTE).toFixed(2) : 0
  const total = +(base + perMin + ebike).toFixed(2)
  return { base, perMin, ebike, total, duration }
}

function formatCurrency(val) {
  return typeof val === 'number' ? val.toFixed(2) : val
}

function formatDateTime(dateTime) {
  if (!dateTime) return 'N/A'
  try {
    const date = new Date(dateTime)
    return date.toLocaleString()
  } catch (e) {
    return 'N/A'
  }
}

function getBikeId(bill) {
  // Get bikeId from the trip data
  if (bill.bikeId) return bill.bikeId
  if (bill.bike && bill.bike.id) return bill.bike.id
  return 'N/A'
}

const loadBills = async () => {
  try {
    let response;
    if (props.user.role === 'operator') {
      response = await apiClient.getAllTrips(currentPage.value, pageSize);
    } else {
      response = await apiClient.getUserTrips(props.user.id, currentPage.value, pageSize);
    }

    const pageData = response.trips.content
    const lastPage = response.trips.last

    billingHistory.value.push(...pageData)

    hasMore.value = !lastPage

    // Load payment statuses for the new bills from database
    await loadPaymentStatuses()

    console.log(billingHistory.value)

  } catch (error) {
    console.error("Error loading billing history:", error)
  } 
}

onMounted(() => {
  loadBills()
  // Listen for storage changes to update payment status
  window.addEventListener('storage', handleStorageChange)
})

// Handle storage changes (for cross-tab updates)
function handleStorageChange(e) {
  if (e.key === 'paidTrips') {
    // Force reactivity update by triggering a re-render
    // The computed properties will automatically pick up the new status
  }
}

// Clean up event listener
onBeforeUnmount(() => {
  window.removeEventListener('storage', handleStorageChange)
})

const loadMore = () => {
  currentPage.value++;
  loadBills();
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

// open/close bill details
const openBill = (bill) => {
  selectedBill.value = bill
}

const closeModal = () => {
  selectedBill.value = null
}

</script>

<style scoped>
.billing-history {
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

.bills-table {
  width: 100%;
  border-collapse: collapse;
}

.bills-table th,
.bills-table td {
  padding: 0.8rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.bills-table th {
  color: var(--text-secondary);
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.85rem;
}

.bills-table tbody .bill-row {
  cursor: pointer;
  transition: transform 180ms cubic-bezier(.2,.9,.2,1), box-shadow 180ms ease, background-color 160ms;
  transform: translateY(0);
  will-change: transform, box-shadow, background-color;
  position: relative;
  z-index: 0;
}

.bills-table tbody .bill-row:hover,
.bills-table tbody .bill-row:focus {
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

.ledger-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding: 1rem;
  background: var(--surface-hover);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.ledger-header h3 {
  margin: 0;
  color: var(--text-primary);
}

.ledger-summary {
  display: flex;
  gap: 1.5rem;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.charge-amount {
  font-weight: 600;
  color: var(--primary);
}

.payment-status {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
  text-transform: uppercase;
}

.payment-status.paid {
  background: #dcfce7;
  color: #166534;
}

.payment-status.pending {
  background: #dcfce7;
  color: #166534;
}

.payment-status.failed {
  background: #fee2e2;
  color: #991b1b;
}

</style>

