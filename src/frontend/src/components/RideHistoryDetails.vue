<template>
  <div class="modal-backdrop" @click.self="close">
    <div class="modal">
      <header class="modal-header">
        <h2>Trip Details — #{{ ride.tripId }}</h2>
        <button class="close-btn" @click="close">×</button>
      </header>

      <section class="details">
        <div class="row">
          <strong>Rider:</strong> <span>{{ ride.userName }}</span>
        </div>

        <div class="row">
          <strong>Bike type:</strong> <span>{{ ride.bikeType }}</span>
        </div>

        <div class="row">
          <strong>Start:</strong>
          <span>{{ formatDate(ride.startTime) }} — {{ ride.startStationName }}</span>
        </div>

        <div class="row">
          <strong>End:</strong>
          <span>{{ formatDate(ride.endTime) }} — {{ ride.endStationName }}</span>
        </div>

        <div class="row">
          <strong>Duration:</strong> <span>{{ cost.duration }} min</span>
        </div>

        <h3>Cost breakdown</h3>
        <div class="cost-row">
          <span>Base:</span><span>{{ formatCurrency(cost.base) }}</span>
        </div>
        <div class="cost-row">
          <span>Per-minute ({{ cost.duration }} min):</span><span>{{ formatCurrency(cost.perMin) }}</span>
        </div>
        <div class="cost-row" v-if="cost.ebike > 0">
          <span>E-bike surcharge:</span><span>{{ formatCurrency(cost.ebike) }}</span>
        </div>
        <div class="cost-row total">
          <strong>Total:</strong><strong>{{ formatCurrency(cost.total) }}</strong>
        </div>

        <h3>Event timeline</h3>
        <ol class="timeline">
          <li>
            <strong>Checkout:</strong>
            <span>{{ formatDate(ride.startTime) }}</span>
          </li>
          <li>
            <strong>Return:</strong>
            <span>{{ formatDate(ride.endTime) }}</span>
          </li>
        </ol>
      </section>

      <footer class="modal-footer">
        <button class="close-action" @click="close">Close</button>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  ride: { type: Object, required: true }
})
const emit = defineEmits(['close'])

// pricing must match the values used in RideHistory
const BASE_FEE = 1.0
const PER_MINUTE = 0.15
const EBIKE_SURCHARGE_PER_MINUTE = 0.10

function safeDate(d) {
  try { return new Date(d) } catch { return null }
}

function formatDate(d) {
  const dt = safeDate(d)
  if (!dt || isNaN(dt)) return '—'
  return dt.toLocaleString()
}

function getDurationMinutes(ride) {
  const s = safeDate(ride.startTime)
  const e = safeDate(ride.endTime)
  if (!s || !e) return 0
  return Math.max(0, Math.round((e - s) / 60000))
}

const cost = computed(() => {
  const duration = getDurationMinutes(props.ride)
  const base = BASE_FEE
  const perMin = +(duration * PER_MINUTE).toFixed(2)
  const ebike = props.ride.bikeType === 'e-bike' ? +(duration * EBIKE_SURCHARGE_PER_MINUTE).toFixed(2) : 0
  const total = +(base + perMin + ebike).toFixed(2)
  return { base, perMin, ebike, total, duration }
})

function formatCurrency(v) {
  return typeof v === 'number' ? v.toFixed(2) : v
}

function close() {
  emit('close')
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
  padding: 0.35rem 0;
  border-bottom: 1px dashed var(--border-color);
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

.close-action {
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 0.5rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
}
</style>