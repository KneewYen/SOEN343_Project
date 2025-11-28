<template>
  <div class="modal-backdrop" @click.self="close">
    <div class="modal">
      <header class="modal-header">
        <h2>Billing Details — #{{ bill.tripId }}</h2>
        <button class="close-btn" @click="close">×</button>
      </header>

      <section class="details">
        <!-- Trip Summary -->
        <div class="trip-summary">
          <h3>Trip Summary</h3>
          <div class="summary-card">
            <div class="summary-row">
              <span class="summary-label">Trip ID:</span>
              <span class="summary-value">#{{ bill.tripId }}</span>
            </div>
            <div class="summary-row">
              <span class="summary-label">Date & Time:</span>
              <span class="summary-value">{{ formatDateTime(bill.startTime) }}</span>
            </div>
            <div class="summary-row">
              <span class="summary-label">Bike ID:</span>
              <span class="summary-value">{{ getBikeId(bill) }}</span>
            </div>
            <div class="summary-row">
              <span class="summary-label">Bike Type:</span>
              <span class="summary-value">{{ bill.bikeType }}</span>
            </div>
            <div class="summary-row">
              <span class="summary-label">Origin Station:</span>
              <span class="summary-value">{{ bill.startStationId || 'N/A' }}</span>
            </div>
            <div class="summary-row">
              <span class="summary-label">Destination Station:</span>
              <span class="summary-value">{{ bill.endStationId || 'N/A' }}</span>
            </div>
            <div class="summary-row">
              <span class="summary-label">Duration:</span>
              <span class="summary-value">{{ cost.duration }} minutes</span>
            </div>
          </div>
        </div>

        <h3>Summary of Charges</h3>
       <div v-for="charge in bill.charges" :key="charge.name" class="cost-row">
          <span>{{ charge.name }}:</span>
          <span>{{ formatCurrency(charge.cost) }}</span>
        </div>
        <div v-if="bill.flexDollarDiscount && bill.flexDollarDiscount > 0" class="cost-row flex-dollar-discount">
          <span>Flex Dollar Discount:</span>
          <span class="discount">-${{ formatCurrency(bill.flexDollarDiscount) }}</span>
        </div>
        <div class="cost-row total">
          <strong>Total:</strong><strong>{{ formatCurrency(bill.totalAmount) }}</strong>
        </div>
        <div v-if="bill.finalAmount !== undefined && bill.finalAmount !== bill.totalAmount" class="cost-row final-amount">
          <strong>Final Amount (after discount):</strong><strong class="final">${{ formatCurrency(bill.finalAmount) }}</strong>
        </div>

        <h3>Event timeline</h3>
        <ol class="timeline">
          <li>
            <strong>Checkout:</strong>
            <span>{{ formatDateTime(bill.startTime) }}</span>
          </li>
          <li>
            <strong>Return:</strong>
            <span>{{ formatDateTime(bill.endTime) }}</span>
          </li>
        </ol>
      </section>

      <footer class="modal-footer">
        <button class="payment-btn" @click="openPayment" :disabled="paymentStatus === 'paid'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          {{ paymentStatus === 'paid' ? 'Paid' : 'Process Payment' }}
        </button>
        <button class="close-action" @click="close">Close</button>
      </footer>

      <!-- Payment Modal -->
      <PaymentService
        v-if="showPayment"
        :bill="bill"
        :amount="cost.total"
        @payment-success="handlePaymentSuccess"
        @close="closePayment"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import PaymentService from './PaymentService.vue'

const props = defineProps({
  bill: { type: Object, required: true }
})
const emit = defineEmits(['close'])

const showPayment = ref(false)

// All bills start as pending
const paymentStatus = ref('pending')

// Pricing must match BillingHistory
const BASE_FEE = 1.0
const PER_MINUTE = 0.15
const EBIKE_SURCHARGE_PER_MINUTE = 0.10

function safeDate(d) {
  try { return new Date(d) } catch { return null }
}

function formatDateTime(d) {
  const dt = safeDate(d)
  if (!dt || isNaN(dt)) return '—'
  return dt.toLocaleString()
}

function getBikeId(bill) {
  if (bill.bikeId) return bill.bikeId
  if (bill.bike && bill.bike.id) return bill.bike.id
  return 'N/A'
}

function getDurationMinutes(bill) {
  const s = safeDate(bill.startTime)
  const e = safeDate(bill.endTime)
  if (!s || !e) return 0
  return Math.max(0, Math.round((e - s) / 60000))
}

const cost = computed(() => {
  const duration = getDurationMinutes(props.bill)
  const base = BASE_FEE
  const perMin = +(duration * PER_MINUTE).toFixed(2)
  const ebike = props.bill.bikeType === 'e-bike' ? +(duration * EBIKE_SURCHARGE_PER_MINUTE).toFixed(2) : 0
  const total = +(base + perMin + ebike).toFixed(2)
  return { base, perMin, ebike, total, duration }
})

function formatCurrency(v) {
  return typeof v === 'number' ? v.toFixed(2) : v
}

function close() {
  emit('close')
}

function openPayment() {
  showPayment.value = true
}

function closePayment() {
  showPayment.value = false
}

function handlePaymentSuccess() {
  paymentStatus.value = 'paid'
  showPayment.value = false
  // Notify parent to mark this trip as Paid
  emit('payment-success', props.bill.tripId)
}
</script>


<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal {
  background: var(--surface);
  color: var(--text-primary);
  border-radius: 12px;
  width: 680px;
  max-width: calc(100% - 32px);
  padding: 1rem;
  box-shadow: 0 10px 40px rgba(0,0,0,0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 1.6rem;
  cursor: pointer;
}

.details {
  margin-top: 0.75rem;
  display: grid;
  gap: 0.5rem;
}

.row {
  display: flex;
  gap: 0.5rem;
}

.cost-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
}

.cost-row.flex-dollar-discount {
  color: #059669;
  font-weight: 600;
}

.cost-row.flex-dollar-discount .discount {
  color: #059669;
}

.cost-row.final-amount {
  border-top: 1px solid var(--border-color);
  margin-top: 0.5rem;
  padding-top: 0.75rem;
}

.cost-row.final-amount .final {
  color: var(--primary);
  font-size: 1.1rem;
}

.cost-row.total {
  border-top: 1px solid var(--border-color);
  margin-top: 0.5rem;
  padding-top: 0.6rem;
}

.timeline {
  margin-top: 0.5rem;
  padding-left: 1.2rem;
}

.modal-footer {
  display:flex;
  justify-content:flex-end;
  margin-top:1rem;
}

.trip-summary {
  margin-bottom: 1.5rem;
}

.summary-card {
  background: var(--surface-hover);
  border-radius: 8px;
  padding: 1rem;
  border: 1px solid var(--border-color);
  margin-top: 0.5rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--border-color);
}

.summary-row:last-child {
  border-bottom: none;
}

.summary-label {
  color: var(--text-secondary);
  font-weight: 500;
}

.summary-value {
  color: var(--text-primary);
  font-weight: 600;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
  gap: 0.5rem;
}

.payment-btn {
  background: #22c55e;
  color: white;
  border: none;
  border-radius: 0.5rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  transition: background 0.3s ease;
}

.payment-btn:hover:not(:disabled) {
  background: #16a34a;
}

.payment-btn:disabled {
  background: #6b7280;
  cursor: not-allowed;
  opacity: 0.7;
}

.close-action {
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 0.5rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
}
</style>

