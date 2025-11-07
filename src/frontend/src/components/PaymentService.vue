<template>
  <div class="payment-backdrop" @click.self="close">
    <div class="payment-modal">
      <header class="payment-header">
        <h2>Process Payment</h2>
        <button class="close-btn" @click="close">×</button>
      </header>

      <section class="payment-content">
        <!-- Payment Summary -->
        <div class="payment-summary">
          <div class="summary-item">
            <span>Trip ID:</span>
            <strong>#{{ bill.tripId }}</strong>
          </div>
          <div class="summary-item">
            <span>Amount Due:</span>
            <strong class="amount">${{ formatCurrency(amount) }}</strong>
          </div>
        </div>

        <!-- Payment Method Selection -->
        <div class="payment-methods">
          <h3>Select Payment Method</h3>
          <div class="method-options">
            <label 
              v-for="method in paymentMethods" 
              :key="method.id"
              class="method-option"
              :class="{ selected: selectedMethod === method.id }"
            >
              <input 
                type="radio" 
                :value="method.id" 
                v-model="selectedMethod"
                class="method-radio"
              />
              <div class="method-info">
                <span class="method-name">{{ method.name }}</span>
                <span class="method-icon">{{ method.icon }}</span>
              </div>
            </label>
          </div>
        </div>

        <!-- Payment Form (for card payments) -->
        <div v-if="selectedMethod === 'card'" class="payment-form">
          <div class="form-group">
            <label>Card Number</label>
            <input 
              v-model="cardNumber" 
              type="text" 
              placeholder="1234 5678 9012 3456"
              maxlength="19"
              @input="formatCardNumber"
            />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Expiry Date</label>
              <input 
                v-model="expiryDate" 
                type="text" 
                placeholder="MM/YY"
                maxlength="5"
                @input="formatExpiry"
              />
            </div>
            <div class="form-group">
              <label>CVV</label>
              <input 
                v-model="cvv" 
                type="text" 
                placeholder="123"
                maxlength="4"
              />
            </div>
          </div>
          <div class="form-group">
            <label>Cardholder Name</label>
            <input 
              v-model="cardholderName" 
              type="text" 
              placeholder="John Doe"
            />
          </div>
        </div>

        <!-- External Payment Service Integration -->
        <div v-if="selectedMethod === 'external'" class="external-payment">
          <p class="info-text">
            You will be redirected to our secure payment processor to complete your payment.
          </p>
          <button class="external-btn" @click="processExternalPayment">
            Continue to Payment Gateway
          </button>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <!-- Processing Indicator -->
        <div v-if="processing" class="processing">
          <div class="spinner"></div>
          <p>Processing payment...</p>
        </div>
      </section>

      <footer class="payment-footer">
        <button class="cancel-btn" @click="close" :disabled="processing">Cancel</button>
        <button 
          class="submit-btn" 
          @click="processPayment" 
          :disabled="processing || !canSubmit"
        >
          {{ processing ? 'Processing...' : `Pay $${formatCurrency(amount)}` }}
        </button>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '../lib/api'

const router = useRouter()

const props = defineProps({
  bill: { type: Object, required: true },
  amount: { type: Number, required: true }
})

const emit = defineEmits(['payment-success', 'close'])

const selectedMethod = ref('card')
const cardNumber = ref('')
const expiryDate = ref('')
const cvv = ref('')
const cardholderName = ref('')
const processing = ref(false)
const error = ref('')

const paymentMethods = [
  { id: 'card', name: 'Credit/Debit Card', icon: '💳' },
  { id: 'paypal', name: 'PayPal', icon: '🔵' },
  { id: 'external', name: 'External Payment Service', icon: '🌐' }
]

const canSubmit = computed(() => {
  if (selectedMethod.value === 'card') {
    return cardNumber.value.length >= 16 && 
           expiryDate.value.length === 5 && 
           cvv.value.length >= 3 &&
           cardholderName.value.length > 0
  }
  return true
})

function formatCardNumber(event) {
  let value = event.target.value.replace(/\s/g, '')
  if (value.length > 16) value = value.slice(0, 16)
  cardNumber.value = value.match(/.{1,4}/g)?.join(' ') || value
}

function formatExpiry(event) {
  let value = event.target.value.replace(/\D/g, '')
  if (value.length >= 2) {
    value = value.slice(0, 2) + '/' + value.slice(2, 4)
  }
  expiryDate.value = value
}

function formatCurrency(val) {
  return typeof val === 'number' ? val.toFixed(2) : val
}

async function processPayment() {
  if (!canSubmit.value) return

  processing.value = true
  error.value = ''

  try {
    // Call payment API endpoint
    const response = await apiClient.processPayment({
      tripId: props.bill.tripId,
      amount: props.amount,
      paymentMethod: selectedMethod.value,
      paymentDetails: selectedMethod.value === 'card' ? {
        cardNumber: cardNumber.value.replace(/\s/g, ''),
        expiryDate: expiryDate.value,
        cvv: cvv.value,
        cardholderName: cardholderName.value
      } : {}
    })

    if (response.success) {
      // Mark the trip as paid in localStorage
      const paidTrips = JSON.parse(localStorage.getItem('paidTrips') || '[]')
      if (!paidTrips.includes(props.bill.tripId)) {
        paidTrips.push(props.bill.tripId)
        localStorage.setItem('paidTrips', JSON.stringify(paidTrips))
      }
      emit('payment-success', {
        tripId: props.bill.tripId,
        amount: props.amount,
        transactionId: response.transactionId
      })
    } else {
      error.value = response.message || 'Payment failed. Please try again.'
    }
  } catch (err) {
    console.error('Payment error:', err)
    error.value = err.message || 'An error occurred while processing your payment.'
  } finally {
    processing.value = false
  }
}

function processExternalPayment() {
  // Navigate to external payment page
  const billData = encodeURIComponent(JSON.stringify(props.bill))
  router.push({
    name: 'external-payment',
    params: { tripId: props.bill.tripId },
    query: { billData: billData }
  })
  // Close the payment modal
  close()
}

function close() {
  if (!processing.value) {
    emit('close')
  }
}
</script>

<style scoped>
.payment-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.payment-modal {
  background: var(--surface);
  color: var(--text-primary);
  border-radius: 12px;
  width: 500px;
  max-width: calc(100% - 32px);
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0,0,0,0.2);
}

.payment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.payment-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: var(--text-secondary);
}

.payment-content {
  padding: 1.5rem;
}

.payment-summary {
  background: var(--surface-hover);
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.5rem;
  border: 1px solid var(--border-color);
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
}

.summary-item:first-child {
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 0.75rem;
  margin-bottom: 0.75rem;
}

.amount {
  font-size: 1.5rem;
  color: var(--primary);
}

.payment-methods h3 {
  margin: 0 0 1rem 0;
  font-size: 1.1rem;
}

.method-options {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.method-option {
  display: flex;
  align-items: center;
  padding: 1rem;
  border: 2px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.method-option:hover {
  border-color: var(--primary);
  background: var(--surface-hover);
}

.method-option.selected {
  border-color: var(--primary);
  background: rgba(236, 72, 153, 0.1);
}

.method-radio {
  margin-right: 1rem;
}

.method-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.method-name {
  font-weight: 500;
}

.method-icon {
  font-size: 1.5rem;
}

.payment-form {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  background: var(--surface);
  color: var(--text-primary);
  font-size: 1rem;
}

.form-group input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(236, 72, 153, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.external-payment {
  text-align: center;
  padding: 1.5rem;
  background: var(--surface-hover);
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

.info-text {
  margin-bottom: 1rem;
  color: var(--text-secondary);
}

.external-btn {
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s ease;
}

.external-btn:hover {
  background: var(--accent-hover);
}

.error-message {
  background: #fee2e2;
  color: #991b1b;
  padding: 0.75rem;
  border-radius: 6px;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.processing {
  text-align: center;
  padding: 2rem;
}

.spinner {
  border: 3px solid var(--border-color);
  border-top: 3px solid var(--primary);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.payment-footer {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.5rem;
  border-top: 1px solid var(--border-color);
}

.cancel-btn,
.submit-btn {
  flex: 1;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: var(--surface-hover);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.cancel-btn:hover:not(:disabled) {
  background: var(--border-color);
}

.submit-btn {
  background: var(--primary);
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background: var(--accent-hover);
}

.submit-btn:disabled,
.cancel-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

