<template>
  <div class="pricing-plan-container">
    <section class="quick-actions">
      <h2 class="section-title">Available Plans</h2>

      <div v-if="loading" class="loading">Loading pricing plans...</div>
      <div v-else-if="error" class="error-message">
        <p>⚠️ Unable to load pricing plans. The backend service may be unavailable.</p>
        <p style="font-size: 0.9rem; margin-top: 0.5rem;">Please check your connection or try again later.</p>
      </div>
      <div v-else-if="pricingPlans.length === 0" class="empty-message">
        <p>No pricing plans available at this time.</p>
      </div>
      <div v-else class="plans-grid">
            <div
              v-for="plan in pricingPlans"
              :key="plan.pricingPlanId || plan.id"
              class="plan-card"
              :class="{ selected: isPlanSelected(plan), 'view-only': !isLoggedIn }"
              @click="isLoggedIn ? selectPlan(plan) : null"
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
                class="guest-notice"
                :class="{ selected: isPlanSelected(plan) }"
              >
                {{
                  isPlanSelected(plan)
                    ? 'Select Plan'
                    : 'Choose Plan'
                }}
              </button>
              <div v-else class="guest-notice">
                <p>Login to select this plan</p>
              </div>
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
</template>

<script>
import apiClient from '../lib/api';

export default {
  name: 'PricingPlans',
  props: {
    guestMode: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      pricingPlans: [],
      loading: true,
      error: false,
      selectedPlan: null,
      userPlan: null,
      isLoggedIn: false,
      user: null
    }
  },
  async created() {
    try {
        // If in guest mode, explicitly set isLoggedIn to false
        if (this.guestMode) {
          this.isLoggedIn = false
          this.user = null
        } else {
          // First check localStorage for user (consistent with other views)
          const userData = localStorage.getItem('user')
          if (userData) {
            try {
              this.user = JSON.parse(userData)
              this.isLoggedIn = !!this.user
            } catch (e) {
              console.warn('Failed to parse user from localStorage:', e)
            }
          }
          
          // Try to get current user from API (to get latest data including pricing plan)
          // This is a secondary check/update, not the primary auth check
          try {
            const response = await apiClient.getCurrentUser()
            // API might return user directly or wrapped in response.user
            if (response) {
              this.user = response.user || response
              this.isLoggedIn = !!this.user
            }
          } catch (apiError) {
            // If API call fails but we have localStorage user, still consider logged in
            if (!this.user && !userData) {
              this.isLoggedIn = false
              this.user = null
            }
            // Otherwise keep the user from localStorage
          }
        }

        console.log('user', this.user)
        // Set userPlan only if user has a plan
        if (this.user && this.user.pricingPlan && this.user.pricingPlan.pricingPlanId) {
        this.userPlan = {
            id: this.user.pricingPlan.pricingPlanId,
            name: this.user.pricingPlan.name,
            description: this.user.pricingPlan.description
        }
        this.selectedPlan = this.userPlan // mark current plan as selected
        }

        // Try to fetch pricing plans
        try {
          this.pricingPlans = await apiClient.getAllPricingPlans()
          console.log('Pricing plans loaded:', this.pricingPlans)
        } catch (planError) {
          console.error('Error fetching pricing plans:', planError)
          // Show error message for both guest and logged in users
          this.error = true
          this.pricingPlans = []
        }
    } catch (err) {
        console.error('Error in pricing plan component:', err)
        this.error = true
    } finally {
        this.loading = false
    }
  },
  methods: {
    // Helper method to check if a plan is selected
    // Handles both pricingPlanId (from API) and id (from userPlan)
    isPlanSelected(plan) {
      if (!this.selectedPlan || !plan) return false
      const planId = plan.pricingPlanId || plan.id
      const selectedId = this.selectedPlan.pricingPlanId || this.selectedPlan.id
      return planId === selectedId
    },
    async selectPlan(plan) {
      // Prevent selection for guests (check both guestMode prop and isLoggedIn)
      if (this.guestMode || !this.isLoggedIn) {
        console.log('Guest users cannot select pricing plans')
        return
      }
      
      const user = await apiClient.getCurrentUser().catch(() => null)
      if (!user || !this.isLoggedIn) {
        console.log('User not authenticated')
        return
      }
      
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
.pricing-plan-container {
  width: 100%;
}

.loading, .error-message, .empty-message {
  text-align: center;
  padding: 2rem;
  color: var(--text-secondary);
}

.error-message {
  color: #ef4444;
}

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

.plan-card.view-only {
  cursor: default;
  opacity: 0.9;
}

.plan-card.view-only:hover {
  transform: none;
  box-shadow: var(--card-shadow);
}

.guest-notice {
  margin-top: 12px;
  padding: 8px 12px;
  background: var(--surface-hover);
  border: 1px solid var(--border);
  border-radius: 8px;
}

.guest-notice p {
  margin: 0;
  font-size: 0.85rem;
  color: var(--text-secondary);
  text-align: center;
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

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text);
  margin: 0 0 16px 0;
}
</style>
