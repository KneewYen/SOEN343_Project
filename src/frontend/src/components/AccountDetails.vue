<template>
  <div v-if="show" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="modal-title">Account Details</h2>
        <button @click="closeModal" class="close-btn" aria-label="Close">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
      <div class="modal-body">
        <div class="account-detail-item">
          <span class="detail-label">Full Name:</span>
          <span class="detail-value">{{ user?.fullName || 'N/A' }}</span>
        </div>
        <div class="account-detail-item">
          <span class="detail-label">Username:</span>
          <span class="detail-value">{{ user?.userName || 'N/A' }}</span>
        </div>
        <div class="account-detail-item">
          <span class="detail-label">Email:</span>
          <span class="detail-value">{{ user?.email || 'N/A' }}</span>
        </div>
        <div class="account-detail-item">
          <span class="detail-label">Pricing Plan:</span>
          <span class="detail-value">{{ pricingPlanName || 'No plan selected' }}</span>
        </div>
        <div class="account-detail-item">
          <span class="detail-label">Loyalty Status:</span>
          <span class="detail-value">TODO</span>
        </div>
        <div class="account-detail-item">
          <span class="detail-label">User Type:</span>
          <span class="detail-value">{{ userType }}</span>
        </div>
        
        <!-- Dual Mode Switcher -->
        <div v-if="isDualUser" class="dual-mode-section">
          <div class="account-detail-item">
            <span class="detail-label">Current Mode:</span>
            <span class="detail-value">{{ currentModeDisplay }}</span>
          </div>
          <div class="mode-switcher">
            <button 
              @click="switchMode('rider')" 
              class="mode-btn"
              :class="{ active: isRiderMode, inactive: !isRiderMode }"
            >
              Rider
            </button>
            <button 
              @click="switchMode('operator')" 
              class="mode-btn"
              :class="{ active: isOperatorMode, inactive: !isOperatorMode }"
            >
              Operator
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDualMode } from '@/composables/useDualMode'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  user: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'modeChanged'])

const { currentMode, setMode, isRiderMode, isOperatorMode } = useDualMode()

const closeModal = () => {
  emit('close')
}

const pricingPlanName = computed(() => {
  if (props.user?.pricingPlan?.name) {
    return props.user.pricingPlan.name
  }
  return null
})

const userType = computed(() => {
  if (props.user?.role) {
    return props.user.role.charAt(0).toUpperCase() + props.user.role.slice(1)
  }
  return 'N/A'
})

const isDualUser = computed(() => {
  return props.user?.role?.toLowerCase() === 'dual'
})

const currentModeDisplay = computed(() => {
  return currentMode.value.charAt(0).toUpperCase() + currentMode.value.slice(1)
})

const switchMode = (mode) => {
  setMode(mode)
  emit('modeChanged', mode)
  // Optionally close modal after switching
  // closeModal()
}
</script>

<style scoped>
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
  backdrop-filter: blur(4px);
}

.modal-content {
  background: var(--surface, #ffffff);
  border-radius: 16px;
  padding: 0;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  border: 2px solid var(--border, #e2e8f0);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 2px solid var(--border, #e2e8f0);
  background: var(--surface-hover, #f8fafc);
}

.modal-title {
  margin: 0;
  color: var(--text, #1e293b);
  font-size: 24px;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: var(--text-secondary, #64748b);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: var(--surface-hover, #f1f5f9);
  color: var(--text, #1e293b);
  transform: rotate(90deg);
}

.modal-body {
  padding: 24px;
}

.account-detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--border, #e2e8f0);
}

.account-detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 600;
  color: var(--text-secondary, #64748b);
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-weight: 500;
  color: var(--text, #1e293b);
  font-size: 16px;
  text-align: right;
}

.dual-mode-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 2px solid var(--border, #e2e8f0);
}

.mode-switcher {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.mode-btn {
  flex: 1;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid var(--border, #e2e8f0);
  background: var(--surface-hover, #f1f5f9);
  color: var(--text-secondary, #64748b);
}

.mode-btn.active {
  background: var(--primary, #ff6b9d);
  color: white;
  border-color: var(--primary, #ff6b9d);
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

.mode-btn.inactive {
  background: var(--surface-hover, #f1f5f9);
  color: var(--text-secondary, #64748b);
  border-color: var(--border, #e2e8f0);
  opacity: 0.6;
}

.mode-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.mode-btn.active:hover {
  box-shadow: 0 6px 16px rgba(255, 107, 157, 0.4);
}
</style>

