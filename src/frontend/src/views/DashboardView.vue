<template>
  <div class="dashboard-redirect">
    <div class="loading-container">
      <div class="loading-spinner">
        <div class="spinner"></div>
      </div>
      <h2 class="loading-title">Redirecting to your dashboard...</h2>
      <p class="loading-text">Please wait while we set up your personalized experience.</p>
    </div>
  </div>
</template>

<script>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'DashboardView',
  setup() {
    const router = useRouter()

    onMounted(() => {
      // Get user data from localStorage
      const userData = localStorage.getItem('user')
      
      if (userData) {
        const user = JSON.parse(userData)
        
        // Redirect based on user role
        setTimeout(() => {
          if (user.role === 'operator') {
            router.push('/dashboard/operator')
          } else {
            router.push('/dashboard/rider')
          }
        }, 1500) // Small delay for better UX
      } else {
        // No user data, redirect to login
        router.push('/login')
      }
    })

    return {}
  }
}
</script>

<style scoped>
.dashboard-redirect {
  min-height: 100vh;
  background: var(--gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.loading-container {
  text-align: center;
  color: white;
  max-width: 500px;
  width: 100%;
}

.loading-spinner {
  margin-bottom: 24px;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.loading-text {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
  line-height: 1.5;
}

/* Mobile-first responsive design */
@media (max-width: 480px) {
  .dashboard-redirect {
    padding: 16px;
  }
  
  .spinner {
    width: 50px;
    height: 50px;
    border-width: 3px;
  }
  
  .loading-title {
    font-size: 20px;
  }
  
  .loading-text {
    font-size: 14px;
  }
}

@media (min-width: 481px) and (max-width: 768px) {
  .spinner {
    width: 55px;
    height: 55px;
  }
  
  .loading-title {
    font-size: 22px;
  }
  
  .loading-text {
    font-size: 15px;
  }
}

@media (min-width: 769px) {
  .spinner {
    width: 70px;
    height: 70px;
    border-width: 5px;
  }
  
  .loading-title {
    font-size: 28px;
  }
  
  .loading-text {
    font-size: 18px;
  }
}

/* Landscape orientation adjustments */
@media (max-height: 600px) and (orientation: landscape) {
  .dashboard-redirect {
    padding: 10px;
  }
  
  .loading-spinner {
    margin-bottom: 16px;
  }
  
  .spinner {
    width: 40px;
    height: 40px;
  }
  
  .loading-title {
    font-size: 18px;
  }
  
  .loading-text {
    font-size: 14px;
  }
}
</style>
