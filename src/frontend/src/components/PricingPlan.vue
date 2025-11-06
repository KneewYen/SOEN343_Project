<template>
  <div class="rider-dashboard">
    <header class="dashboard-header">
      <div class="header-content">
        <div class="logo-section">
          <i class="logo-icon fas fa-bicycle"></i>
          <h1 class="app-title">RideWithUs</h1>
        </div>
        <div class="user-info">
          <span class="welcome-text">Pricing Plans</span>
        </div>
      </div>
    </header>

    <main class="dashboard-main">
      <div class="dashboard-grid">
        <section class="quick-actions">
          <h2 class="section-title">Available Plans</h2>

          <div v-if="loading" class="loading">Loading pricing plans...</div>
          <div v-else class="plans-grid">
            <div
              v-for="plan in pricingPlans"
              :key="plan.id"
              class="plan-card"
              :class="{ selected: selectedPlan && selectedPlan.id === plan.id }"
              @click="selectPlan(plan)"
            >
              <div class="plan-header">
                <h3 class="plan-name">{{ plan.name }}</h3>
                <!-- Show dot if user has no plan -->
                <span v-if="isLoggedIn && !userPlan" class="select-dot"></span>
              </div>

              <p class="plan-description">{{ plan.description }}</p>
              <p class="plan-price">{{ plan.price }} $ \ {{ plan.measurement }}</p>
              <p class="plan-price">base rate: {{ plan.baseFee }}$</p>

              <button
                v-if="isLoggedIn"
                class="action-btn secondary full-width"
                :class="{ selected: selectedPlan && selectedPlan.id === plan.id }"
              >
                {{
                  selectedPlan && selectedPlan.id === plan.id
                    ? 'Select Plan'
                    : 'Choose Plan'
                }}
              </button>
            </div>
          </div>

          <!-- Display user's current plan -->
          <div v-if="isLoggedIn" class="current-plan-section">
            <h3>Your Current Plan</h3>
            <p v-if="userPlan" class="current-plan">
              <strong>{{ userPlan.name }}</strong> — {{ userPlan.description }}
            </p>
            <p v-else class="no-plan">You don’t have a plan yet. Please select one above.</p>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script>
import apiClient from '../lib/api';

export default {
  name: 'PricingPlans',
  data() {
    return {
      pricingPlans: [],
      loading: true,
      selectedPlan: null,
      userPlan: null,
      isLoggedIn: false,
      user: null
    }
  },
  async created() {
    try {
        const response = await apiClient.getCurrentUser().catch(() => null)
        this.isLoggedIn = !!response?.user
        this.user = response?.user || null

        console.log('user', this.user)
        // Set userPlan only if user has a plan
        if (this.user && this.user.pricingPlan) {
        this.userPlan = {
            id: this.user.pricingPlan.id,
            name: this.user.pricingPlan.name,
            description: this.user.pricingPlan.description
        }
        this.selectedPlan = this.userPlan // mark current plan as selected
        }

        this.pricingPlans = await apiClient.getAllPricingPlans()
    } catch (err) {
        console.error('Error fetching pricing plans:', err)
    } finally {
        this.loading = false
    }
  },
  methods: {
    async selectPlan(plan) {
      const user = await apiClient.getCurrentUser().catch(() => null)
      if (!this.isLoggedIn) return
      this.selectedPlan = plan
      this.userPlan = plan
      console.log('Selected Plan:', plan)
     console.log('Saving plan:', plan.pricingPlanId, 'for user:', this.user.id)
     try {
      // Call backend to save the selected plan
      await apiClient.selectPricingPlan(this.user.id, plan.pricingPlanId)
      console.log('Plan saved successfully')
    } catch (err) {
      console.error('Failed to save plan:', err)
      // Optionally reset selection on failure
      this.selectedPlan = this.userPlan
    }

    }
  }
}
</script>

<style scoped>
.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.plan-card {
  background: var(--surface);
  border: 2px solid var(--border);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--card-shadow);
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.plan-card.selected {
  border-color: var(--primary);
  background: var(--surface-hover);
  box-shadow: 0 0 0 3px rgba(236, 72, 153, 0.2);
}

.plan-header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  position: relative;
}

.select-dot {
  width: 10px;
  height: 10px;
  background: var(--primary);
  border-radius: 50%;
  animation: pulse 1.2s infinite;
}

@keyframes pulse {
  0% { transform: scale(0.9); opacity: 0.7; }
  50% { transform: scale(1.2); opacity: 1; }
  100% { transform: scale(0.9); opacity: 0.7; }
}

.plan-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text);
}

.plan-description {
  color: var(--text-secondary);
  margin: 8px 0 12px;
  font-size: 0.9rem;
}

.plan-price {
  font-size: 1.1rem;
  font-weight: 800;
  color: var(--primary);
  margin-bottom: 12px;
}

.current-plan-section {
  margin-top: 2rem;
  background: var(--surface);
  border: 2px solid var(--border);
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: var(--card-shadow);
}

.current-plan-section h3 {
  margin-bottom: 8px;
  color: var(--text);
  font-size: 1.1rem;
}

.current-plan {
  color: var(--text-secondary);
}

.no-plan {
  color: #ef4444;
  font-weight: 600;
}
</style>
