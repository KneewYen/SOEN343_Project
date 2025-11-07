<template>
  <div class="external-payment-page">
    <ThemeToggle />
    
    <div class="payment-container">
      <header class="payment-page-header">
        <h1>External Payment Gateway</h1>
        <p class="subtitle">Complete your payment securely</p>
      </header>

      <!-- Bill Summary -->
      <section class="bill-summary-section">
        <h2>Bill Summary</h2>
        <div class="summary-card">
          <div class="summary-row">
            <span class="label">Trip ID:</span>
            <span class="value">#{{ bill?.tripId || 'N/A' }}</span>
          </div>
          <div class="summary-row">
            <span class="label">Date & Time:</span>
            <span class="value">{{ formatDateTime(bill?.startTime) }}</span>
          </div>
          <div class="summary-row">
            <span class="label">Bike ID:</span>
            <span class="value">{{ getBikeId(bill) }}</span>
          </div>
          <div class="summary-row">
            <span class="label">Bike Type:</span>
            <span class="value">{{ bill?.bikeType || 'N/A' }}</span>
          </div>
          <div class="summary-row">
            <span class="label">Origin Station:</span>
            <span class="value">{{ bill?.startStationName || 'N/A' }}</span>
          </div>
          <div class="summary-row">
            <span class="label">Destination Station:</span>
            <span class="value">{{ bill?.endStationName || 'N/A' }}</span>
          </div>
          <div class="summary-row">
            <span class="label">Duration:</span>
            <span class="value">{{ cost.duration }} minutes</span>
          </div>
          
          <div class="divider"></div>
          
          <div class="summary-row">
            <span class="label">Base Fee:</span>
            <span class="value">${{ formatCurrency(cost.base) }}</span>
          </div>
          <div class="summary-row">
            <span class="label">Per-minute ({{ cost.duration }} min):</span>
            <span class="value">${{ formatCurrency(cost.perMin) }}</span>
          </div>
          <div class="summary-row" v-if="cost.ebike > 0">
            <span class="label">E-bike surcharge:</span>
            <span class="value">${{ formatCurrency(cost.ebike) }}</span>
          </div>
          
          <div class="divider"></div>
          
          <div class="summary-row total-row">
            <span class="label">Total Amount:</span>
            <span class="value total-amount">${{ formatCurrency(cost.total) }}</span>
          </div>
        </div>
      </section>

      <!-- Payment Button -->
      <section class="payment-action-section">
        <button 
          class="paid-button" 
          @click="handlePaid"
          :disabled="processing"
        >
          <span v-if="!processing">✓ Paid</span>
          <span v-else class="processing-text">
            <div class="spinner-small"></div>
            Processing...
          </span>
        </button>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ThemeToggle from '../components/ThemeToggle.vue'
import apiClient from '../lib/api'

const router = useRouter()
const route = useRoute()

const bill = ref(null)
const processing = ref(false)

// Pricing constants
const BASE_FEE = 1.0
const PER_MINUTE = 0.15
const EBIKE_SURCHARGE_PER_MINUTE = 0.10

const cost = computed(() => {
  if (!bill.value) return { base: 0, perMin: 0, ebike: 0, total: 0, duration: 0 }
  
  const duration = getDurationMinutes(bill.value)
  const base = BASE_FEE
  const perMin = +(duration * PER_MINUTE).toFixed(2)
  const ebike = bill.value.bikeType === 'e-bike' ? +(duration * EBIKE_SURCHARGE_PER_MINUTE).toFixed(2) : 0
  const total = +(base + perMin + ebike).toFixed(2)
  return { base, perMin, ebike, total, duration }
})

onMounted(() => {
  // Get bill data from route params or query
  const tripId = route.params.tripId || route.query.tripId
  const billData = route.query.billData
  
  if (billData) {
      try {
        bill.value = JSON.parse(decodeURIComponent(billData))
      } catch (e) {
        console.error('Error parsing bill data:', e)
      }
    } else if (tripId) {
      // If only tripId is provided, fetch the trip data
      loadTripData(tripId)
    }
})

async function loadTripData(tripId) {
  try {
    // You might need to add an API endpoint to get trip by ID
    // For now, we'll use the existing getUserTrips and find the matching trip
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user.id) {
      const response = await apiClient.getUserTrips(user.id, 0, 100)
      if (response.trips && response.trips.content) {
        const trip = response.trips.content.find(t => t.tripId == tripId)
        if (trip) {
          bill.value = trip
        }
      }
    }
  } catch (err) {
    console.error('Error loading trip data:', err)
  }
}

function getDurationMinutes(bill) {
  if (!bill || !bill.startTime || !bill.endTime) return 0
  try {
    const start = new Date(bill.startTime)
    const end = new Date(bill.endTime)
    return Math.max(0, Math.round((end - start) / 60000))
  } catch (e) {
    return 0
  }
}

function formatDateTime(d) {
  if (!d) return 'N/A'
  try {
    return new Date(d).toLocaleString()
  } catch (e) {
    return 'N/A'
  }
}

function formatCurrency(val) {
  return typeof val === 'number' ? val.toFixed(2) : val
}

function getBikeId(bill) {
  if (!bill) return 'N/A'
  if (bill.bikeId) return bill.bikeId
  if (bill.bike && bill.bike.id) return bill.bike.id
  return 'N/A'
}

async function handlePaid() {
  if (!bill.value || !bill.value.tripId || processing.value) {
    return
  }

  processing.value = true

  try {
    // Create Billing and Charge objects in database
    // This will change the status to "Paid" when billing history reads from database
    const response = await apiClient.createBilling(bill.value.tripId)
    
    if (response.success) {
      // Wait a moment to show success, then navigate back
      setTimeout(() => {
        router.go(-1)
      }, 500)
    } else {
      console.error('Failed to create billing:', response.message)
      processing.value = false
      // Still navigate back even if there's an error
      setTimeout(() => {
        router.go(-1)
      }, 1000)
    }
  } catch (err) {
    console.error('Error creating billing:', err)
    processing.value = false
    // Still navigate back even if there's an error
    setTimeout(() => {
      router.go(-1)
    }, 1000)
  }
}
</script>

<style scoped>
.external-payment-page {
  min-height: 100vh;
  background: var(--gradient);
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.payment-container {
  max-width: 600px;
  width: 100%;
  background: var(--surface);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 40px rgba(0,0,0,0.15);
  color: var(--text-primary);
}

.payment-page-header {
  text-align: center;
  margin-bottom: 2rem;
}

.payment-page-header h1 {
  font-size: 2rem;
  color: var(--primary);
  margin: 0 0 0.5rem 0;
}

.subtitle {
  color: var(--text-secondary);
  margin: 0;
}

.bill-summary-section {
  margin-bottom: 2rem;
}

.bill-summary-section h2 {
  font-size: 1.5rem;
  margin: 0 0 1rem 0;
  color: var(--text-primary);
}

.summary-card {
  background: var(--surface-hover);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--border-color);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--border-color);
}

.summary-row:last-child {
  border-bottom: none;
}

.summary-row.total-row {
  border-top: 2px solid var(--primary);
  margin-top: 0.5rem;
  padding-top: 1rem;
  font-size: 1.2rem;
}

.label {
  color: var(--text-secondary);
  font-weight: 500;
}

.value {
  color: var(--text-primary);
  font-weight: 600;
}

.total-amount {
  font-size: 1.5rem;
  color: var(--primary);
}

.divider {
  height: 1px;
  background: var(--border-color);
  margin: 0.75rem 0;
}

.payment-action-section {
  text-align: center;
  margin-bottom: 1rem;
}

.paid-button {
  background: #22c55e;
  color: white;
  border: none;
  border-radius: 12px;
  padding: 1rem 3rem;
  font-size: 1.2rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
}

.paid-button:hover:not(:disabled) {
  background: #16a34a;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(34, 197, 94, 0.4);
}

.paid-button:disabled {
  background: #6b7280;
  cursor: not-allowed;
  opacity: 0.7;
}

.processing-text {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.spinner-small {
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .external-payment-page {
    padding: 1rem;
  }

  .payment-container {
    padding: 1.5rem;
  }

  .payment-page-header h1 {
    font-size: 1.5rem;
  }

  .paid-button {
    padding: 0.875rem 2rem;
    font-size: 1rem;
    width: 100%;
  }
}
</style>

