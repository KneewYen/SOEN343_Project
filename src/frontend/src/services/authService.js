import apiClient from '@/lib/api'

class AuthService {
  constructor() {
    this.user = null
    this.session = null
    this.initializeAuth()
  }

  // Initialize authentication state from localStorage
  async initializeAuth() {
    try {
      const storedUser = localStorage.getItem('user')
      const storedSession = localStorage.getItem('session')
      
      if (storedUser && storedSession) {
        this.user = JSON.parse(storedUser)
        this.session = JSON.parse(storedSession)
        
        // Verify session is still valid with backend
        try {
          const currentUser = await this.getCurrentUser()
          this.user = currentUser
        } catch (error) {
          // Session invalid, clear stored data
          this.clearAuth()
        }
      }
    } catch (error) {
      console.warn('Auth initialization failed:', error)
      this.clearAuth()
    }
  }

  // Clear authentication data
  clearAuth() {
    this.user = null
    this.session = null
    localStorage.removeItem('user')
    localStorage.removeItem('session')
    localStorage.removeItem('reservation')
    localStorage.removeItem('trip')
  }

  // Get current user
  async getCurrentUser() {
    try {
      const response = await apiClient.getCurrentUser()
      if (response.success) {
        this.user = response.user
        return response.user
      } else {
        throw new Error(response.message || 'Failed to get current user')
      }
    } catch (error) {
      this.clearAuth()
      throw error
    }
  }

  // Get current session
  async getCurrentSession() {
    return this.session
  }

  // Sign up with email and password
  async signUp(email, password, userData = {}) {
    try {
      const response = await apiClient.register({
        fullName: userData.fullName || userData.name || '',
        userName: userData.userName || userData.username || '',
        email: email,
        password: password,
        address: userData.address || '',
        paymentInfo: userData.paymentInfo || ''
      })
      
      if (response.success) {
        this.user = response.user
        this.session = { token: response.token }
        
        // Store in localStorage
        localStorage.setItem('user', JSON.stringify(response.user))
        localStorage.setItem('session', JSON.stringify({ token: response.token }))
        
        // Emit auth state change event
        window.dispatchEvent(new CustomEvent('auth-state-changed', {
          detail: { user: this.user, session: this.session, event: 'SIGNED_UP' }
        }))
        
        return { user: response.user, session: this.session }
      } else {
        throw new Error(response.message || 'Registration failed')
      }
    } catch (error) {
      throw error
    }
  }

  // Sign in with email and password
  async signIn(email, password) {
    try {
      const response = await apiClient.login({
        usernameOrEmail: email,
        password: password
      })
      
      if (response.success) {
        this.user = response.user
        this.session = { token: response.token }
        
        // Store in localStorage
        localStorage.setItem('user', JSON.stringify(response.user))
        localStorage.setItem('session', JSON.stringify({ token: response.token }))
        
        // Emit auth state change event
        window.dispatchEvent(new CustomEvent('auth-state-changed', {
          detail: { user: this.user, session: this.session, event: 'SIGNED_IN' }
        }))
        
        return { user: response.user, session: this.session }
      } else {
        throw new Error(response.message || 'Login failed')
      }
    } catch (error) {
      throw error
    }
  }

  // Sign out
  async signOut() {
    try {
      await apiClient.logout()
    } catch (error) {
      console.warn('Logout API call failed:', error)
    } finally {
      this.clearAuth()
      
      // Emit auth state change event
      window.dispatchEvent(new CustomEvent('auth-state-changed', {
        detail: { user: null, session: null, event: 'SIGNED_OUT' }
      }))
    }
    return true
  }

  // Update user profile
  async updateUserProfile(userId, updates) {
    try {
      const response = await apiClient.updateUser(userId, updates)
      return response
    } catch (error) {
      throw error
    }
  }

  // Get user profile
  async getUserProfile(userId) {
    try {
      const response = await apiClient.getUserById(userId)
      return response
    } catch (error) {
      throw error
    }
  }

  // Reset password (not implemented in backend yet)
  async resetPassword(email) {
    throw new Error('Password reset not implemented yet')
  }

  // Update password (not implemented in backend yet)
  async updatePassword(newPassword) {
    throw new Error('Password update not implemented yet')
  }

  // Check if user is authenticated
  isAuthenticated() {
    return !!this.user
  }

  // Get user ID
  getUserId() {
    return this.user?.id
  }

  // Get user email
  getUserEmail() {
    return this.user?.email
  }

  // Get user metadata
  getUserMetadata() {
    return this.user?.user_metadata
  }

  // Sign in with Google (not implemented in backend yet)
  async signInWithGoogle() {
    throw new Error('Google sign-in not implemented yet')
  }

  // Sign in with Facebook (not implemented in backend yet)
  async signInWithFacebook() {
    throw new Error('Facebook sign-in not implemented yet')
  }
}

// Create and export a singleton instance
export const authService = new AuthService()
export default authService

