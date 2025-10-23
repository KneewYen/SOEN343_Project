import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import RiderDashboard from '../views/RiderDashboard.vue'
import OperatorDashboard from '../views/OperatorDashboard.vue'

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
    }
  ]
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !user) {
    // Redirect to login if authentication is required
    next('/login')
  } else if (to.meta.role && user && user.role !== to.meta.role) {
    // Redirect to correct dashboard if role doesn't match
    if (user.role === 'operator') {
      next('/dashboard/operator')
    } else {
      next('/dashboard/rider')
    }
  } else {
    next()
  }
})

export default router

