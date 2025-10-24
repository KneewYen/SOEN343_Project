import { ref, computed } from 'vue'
import { authService } from '@/services/authService'

// Reactive state
const user = ref(null)
const session = ref(null)
const loading = ref(false)
const error = ref(null)

// Computed properties
const isAuthenticated = computed(() => !!user.value)
const userEmail = computed(() => user.value?.email || '')
const userId = computed(() => user.value?.id || '')
const userMetadata = computed(() => user.value?.user_metadata || {})
const userRole = computed(() => user.value?.role || 'rider')

// Actions
const setUser = (newUser) => {
  user.value = newUser
}

const setSession = (newSession) => {
  session.value = newSession
}

const setLoading = (isLoading) => {
  loading.value = isLoading
}

const setError = (errorMessage) => {
  error.value = errorMessage
}

const clearError = () => {
  error.value = null
}

// Authentication methods
const signUp = async (email, password, userData = {}) => {
  setLoading(true)
  clearError()
  
  try {
    const result = await authService.signUp(email, password, userData)
    setUser(result.user)
    setSession(result.session)
    return result
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

const signIn = async (email, password) => {
  setLoading(true)
  clearError()
  
  try {
    const result = await authService.signIn(email, password)
    setUser(result.user)
    setSession(result.session)
    return result
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

const signOut = async () => {
  setLoading(true)
  clearError()
  
  try {
    await authService.signOut()
    setUser(null)
    setSession(null)
    return true
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

const resetPassword = async (email) => {
  setLoading(true)
  clearError()
  
  try {
    await authService.resetPassword(email)
    return true
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

const updatePassword = async (newPassword) => {
  setLoading(true)
  clearError()
  
  try {
    await authService.updatePassword(newPassword)
    return true
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

const updateUserProfile = async (userId, updates) => {
  setLoading(true)
  clearError()
  
  try {
    const result = await authService.updateUserProfile(userId, updates)
    return result
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

const getUserProfile = async (userId) => {
  setLoading(true)
  clearError()
  
  try {
    const result = await authService.getUserProfile(userId)
    return result
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

const initializeAuth = async () => {
  setLoading(true)
  
  try {
    const currentUser = await authService.getCurrentUser()
    const currentSession = await authService.getCurrentSession()
    
    setUser(currentUser)
    setSession(currentSession)
    
    return { user: currentUser, session: currentSession }
  } catch (err) {
    console.warn('Auth initialization failed:', err)
    setUser(null)
    setSession(null)
  } finally {
    setLoading(false)
  }
}

// Social auth methods
const signInWithGoogle = async () => {
  setLoading(true)
  clearError()
  
  try {
    const result = await authService.signInWithGoogle()
    return result
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

const signInWithFacebook = async () => {
  setLoading(true)
  clearError()
  
  try {
    const result = await authService.signInWithFacebook()
    return result
  } catch (err) {
    setError(err.message)
    throw err
  } finally {
    setLoading(false)
  }
}

// Listen for auth state changes
const setupAuthListener = () => {
  window.addEventListener('auth-state-changed', (event) => {
    const { user: newUser, session: newSession } = event.detail
    setUser(newUser)
    setSession(newSession)
  })
}

// Initialize auth listener
setupAuthListener()

// Export the store
export const useAuthStore = () => {
  return {
    // State
    user: computed(() => user.value),
    session: computed(() => session.value),
    loading: computed(() => loading.value),
    error: computed(() => error.value),
    
    // Computed
    isAuthenticated,
    userEmail,
    userId,
    userMetadata,
    userRole,
    
    // Actions
    signUp,
    signIn,
    signOut,
    resetPassword,
    updatePassword,
    updateUserProfile,
    getUserProfile,
    initializeAuth,
    signInWithGoogle,
    signInWithFacebook,
    setUser,
    setSession,
    setLoading,
    setError,
    clearError
  }
}

export default useAuthStore

