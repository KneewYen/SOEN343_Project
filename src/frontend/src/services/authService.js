import { supabase } from '@/lib/supabase'

class AuthService {
  constructor() {
    this.user = null
    this.session = null
    this.setupAuthListener()
  }

  // Set up authentication state listener
  setupAuthListener() {
    supabase.auth.onAuthStateChange((event, session) => {
      this.session = session
      this.user = session?.user || null
      
      // Emit custom events for components to listen to
      window.dispatchEvent(new CustomEvent('auth-state-changed', {
        detail: { user: this.user, session: this.session, event }
      }))
    })
  }

  // Get current user
  async getCurrentUser() {
    const { data: { user }, error } = await supabase.auth.getUser()
    if (error) throw error
    this.user = user
    return user
  }

  // Get current session
  async getCurrentSession() {
    const { data: { session }, error } = await supabase.auth.getSession()
    if (error) throw error
    this.session = session
    return session
  }

  // Sign up with email and password
  async signUp(email, password, userData = {}) {
    const { data, error } = await supabase.auth.signUp({
      email,
      password,
      options: {
        data: userData
      }
    })
    
    if (error) throw error
    
    // Create user profile in database if signup successful
    if (data.user) {
      try {
        await this.createUserProfile(data.user, userData)
      } catch (profileError) {
        console.warn('Profile creation failed:', profileError)
        // Don't throw here as auth was successful
      }
    }
    
    return data
  }

  // Sign in with email and password
  async signIn(email, password) {
    const { data, error } = await supabase.auth.signInWithPassword({
      email,
      password
    })
    
    if (error) throw error
    return data
  }

  // Sign out
  async signOut() {
    const { error } = await supabase.auth.signOut()
    if (error) throw error
    this.user = null
    this.session = null
    return true
  }

  // Create user profile in database
  async createUserProfile(authUser, userData) {
    const { error } = await supabase
      .from('users')
      .insert([{
        id: authUser.id,
        name: userData.name || `${userData.first_name || ''} ${userData.last_name || ''}`.trim(),
        email: authUser.email,
        phone: userData.phone || null,
        user_type: userData.user_type || 'PASSENGER',
        is_active: true
      }])

    if (error) throw error
    return true
  }

  // Update user profile
  async updateUserProfile(userId, updates) {
    const { data, error } = await supabase
      .from('users')
      .update(updates)
      .eq('id', userId)
      .select()
      .single()

    if (error) throw error
    return data
  }

  // Get user profile
  async getUserProfile(userId) {
    const { data, error } = await supabase
      .from('users')
      .select('*')
      .eq('id', userId)
      .single()

    if (error) throw error
    return data
  }

  // Reset password
  async resetPassword(email) {
    const { error } = await supabase.auth.resetPasswordForEmail(email, {
      redirectTo: `${window.location.origin}/reset-password`
    })
    
    if (error) throw error
    return true
  }

  // Update password
  async updatePassword(newPassword) {
    const { error } = await supabase.auth.updateUser({
      password: newPassword
    })
    
    if (error) throw error
    return true
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

  // Sign in with Google (if configured)
  async signInWithGoogle() {
    const { data, error } = await supabase.auth.signInWithOAuth({
      provider: 'google',
      options: {
        redirectTo: `${window.location.origin}/dashboard`
      }
    })
    
    if (error) throw error
    return data
  }

  // Sign in with Facebook (if configured)
  async signInWithFacebook() {
    const { data, error } = await supabase.auth.signInWithOAuth({
      provider: 'facebook',
      options: {
        redirectTo: `${window.location.origin}/dashboard`
      }
    })
    
    if (error) throw error
    return data
  }
}

// Create and export a singleton instance
export const authService = new AuthService()
export default authService

