// Supabase client for direct database and authentication
import { createClient } from '@supabase/supabase-js'

const supabaseUrl = 'https://hpgawptoeiwdajfrqvvz.supabase.co'
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhwZ2F3cHRvZWl3ZGFqZnJxdnZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5MDEwOTYsImV4cCI6MjA3NTQ3NzA5Nn0.xpD8QznbSLADZU7kUyDFn_4cEJvGeCrG9fKO-LrXKJY'

export const supabase = createClient(supabaseUrl, supabaseAnonKey)

// Authentication functions
export const auth = {
  // Register a new user
  async register(userData) {
    try {
      console.log('Attempting registration with:', userData.email)
      
      const { data, error } = await supabase.auth.signUp({
        email: userData.email,
        password: userData.password,
        options: {
          emailRedirectTo: undefined, // Disable email confirmation
          data: {
            full_name: userData.fullName,
            username: userData.userName,
            address: userData.address,
            role: 'rider'
          }
        }
      })

      console.log('Registration response:', { data, error })

      if (error) {
        console.error('Registration error:', error)
        return { success: false, message: error.message }
      }

      // Also save user data to our custom users table
      if (data.user) {
        const { error: insertError } = await supabase
          .from('users')
          .insert({
            id: data.user.id,
            full_name: userData.fullName,
            username: userData.userName,
            email: userData.email,
            address: userData.address,
            role: 'rider',
            created_at: new Date().toISOString()
          })

        if (insertError) {
          console.error('Error saving user to custom table:', insertError)
        }
      }

      return { 
        success: true, 
        message: 'Registration successful',
        user: data.user,
        session: data.session
      }
    } catch (error) {
      return { success: false, message: error.message }
    }
  },

  // Login user
  async login(credentials) {
    try {
      console.log('Attempting login with:', credentials.usernameOrEmail)
      
      const { data, error } = await supabase.auth.signInWithPassword({
        email: credentials.usernameOrEmail,
        password: credentials.password
      })

      console.log('Login response:', { data, error })

      if (error) {
        console.error('Login error:', error)
        
        // Handle specific error cases
        if (error.message.includes('Email not confirmed')) {
          return { 
            success: false, 
            message: 'Please check your email and click the confirmation link before logging in.',
            needsConfirmation: true
          }
        }
        
        return { success: false, message: error.message }
      }

      return { 
        success: true, 
        message: 'Login successful',
        user: data.user,
        session: data.session
      }
    } catch (error) {
      console.error('Login exception:', error)
      return { success: false, message: error.message }
    }
  },

  // Logout user
  async logout() {
    try {
      const { error } = await supabase.auth.signOut()
      if (error) {
        return { success: false, message: error.message }
      }
      return { success: true, message: 'Logout successful' }
    } catch (error) {
      return { success: false, message: error.message }
    }
  },

  // Get current user
  async getCurrentUser() {
    try {
      const { data: { user }, error } = await supabase.auth.getUser()
      if (error) {
        return { success: false, message: error.message }
      }
      return { success: true, user }
    } catch (error) {
      return { success: false, message: error.message }
    }
  },

  // Get user session
  async getSession() {
    try {
      const { data: { session }, error } = await supabase.auth.getSession()
      if (error) {
        return { success: false, message: error.message }
      }
      return { success: true, session }
    } catch (error) {
      return { success: false, message: error.message }
    }
  }
}

// Database functions
export const database = {
  // Get all users
  async getUsers() {
    try {
      const { data, error } = await supabase
        .from('users')
        .select('*')
        .order('created_at', { ascending: false })

      if (error) {
        return { success: false, message: error.message }
      }
      return { success: true, data }
    } catch (error) {
      return { success: false, message: error.message }
    }
  },

  // Get user by ID
  async getUserById(id) {
    try {
      const { data, error } = await supabase
        .from('users')
        .select('*')
        .eq('id', id)
        .single()

      if (error) {
        return { success: false, message: error.message }
      }
      return { success: true, data }
    } catch (error) {
      return { success: false, message: error.message }
    }
  },

  // Update user
  async updateUser(id, userData) {
    try {
      const { data, error } = await supabase
        .from('users')
        .update(userData)
        .eq('id', id)
        .select()
        .single()

      if (error) {
        return { success: false, message: error.message }
      }
      return { success: true, data }
    } catch (error) {
      return { success: false, message: error.message }
    }
  }
}

// Test function to create a test user
export const testAuth = {
  async createTestUser() {
    try {
      const testUser = {
        fullName: 'Test User',
        userName: 'testuser',
        email: 'test@example.com',
        address: '123 Test Street',
        password: 'password123'
      }
      
      console.log('Creating test user...')
      const result = await auth.register(testUser)
      console.log('Test user creation result:', result)
      return result
    } catch (error) {
      console.error('Test user creation failed:', error)
      return { success: false, message: error.message }
    }
  },

  async testLogin() {
    try {
      console.log('Testing login...')
      const result = await auth.login({
        usernameOrEmail: 'test@example.com',
        password: 'password123'
      })
      console.log('Test login result:', result)
      return result
    } catch (error) {
      console.error('Test login failed:', error)
      return { success: false, message: error.message }
    }
  }
}

export default supabase