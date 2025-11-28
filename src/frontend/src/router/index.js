import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import RiderDashboard from '../views/RiderDashboard.vue'
import OperatorDashboard from '../views/OperatorDashboard.vue'
import GuestDashboard from '../views/GuestDashboard.vue'
import ExternalPaymentView from '../views/ExternalPaymentView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/dashboard/rider',
      name: 'rider-dashboard',
      component: RiderDashboard,
      meta: { requiresAuth: true, role: 'rider' }
    },
    {
      path: '/dashboard/operator',
      name: 'operator-dashboard',
      component: OperatorDashboard,
      meta: { requiresAuth: true, role: 'operator' }
    },
    {
      path: '/dashboard/guest',
      name: 'guest-dashboard',
      component: GuestDashboard,
      meta: { requiresAuth: false }
    },
    {
      path: '/payment/external/:tripId?',
      name: 'external-payment',
      component: ExternalPaymentView,
      meta: { requiresAuth: true }
    }
  ]
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  // Allow guest dashboard access without authentication
  if (to.name === 'guest-dashboard') {
    next()
    return
  }

  if (requiresAuth && !user) {
    // Redirect to login if authentication is required
    next('/login')
  } else if (to.meta.role && user && user.role?.toLowerCase() !== 'dual') {
    // Allow dual users to access both dashboards
    // For non-dual users, redirect if role doesn't match
    if (user.role !== to.meta.role) {
      if (user.role === 'operator') {
        next('/dashboard/operator')
      } else {
        next('/dashboard/rider')
      }
    } else {
      next()
    }
  } else {
    // Allow access for dual users or if no role restriction
    next()
  }
})

export default router

