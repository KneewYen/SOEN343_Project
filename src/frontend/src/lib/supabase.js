import { createClient } from '@supabase/supabase-js'

// Supabase configuration
const supabaseUrl = 'https://hpgawptoeiwdajfrqvvz.supabase.co'
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhwZ2F3cHRvZWl3ZGFqZnJxdnZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5MDEwOTYsImV4cCI6MjA3NTQ3NzA5Nn0.xpD8QznbSLADZU7kUyDFn_4cEJvGeCrG9fKO-LrXKJY'

if (!supabaseUrl || !supabaseAnonKey) {
  throw new Error('Missing Supabase environment variables')
}

export const supabase = createClient(supabaseUrl, supabaseAnonKey, {
  auth: {
    autoRefreshToken: true,
    persistSession: true,
    detectSessionInUrl: true
  }
})

// Helper functions for common operations
export const supabaseHelpers = {
  // User operations
  async getUsers() {
    const { data, error } = await supabase
      .from('users')
      .select('*')
      .order('created_at', { ascending: false })
    
    if (error) throw error
    return data
  },

  async getUserById(id) {
    const { data, error } = await supabase
      .from('users')
      .select('*')
      .eq('id', id)
      .single()
    
    if (error) throw error
    return data
  },

  async createUser(userData) {
    const { data, error } = await supabase
      .from('users')
      .insert([userData])
      .select()
      .single()
    
    if (error) throw error
    return data
  },

  async updateUser(id, userData) {
    const { data, error } = await supabase
      .from('users')
      .update(userData)
      .eq('id', id)
      .select()
      .single()
    
    if (error) throw error
    return data
  },

  async deleteUser(id) {
    const { error } = await supabase
      .from('users')
      .delete()
      .eq('id', id)
    
    if (error) throw error
    return true
  },

  // Ride operations
  async getRides() {
    const { data, error } = await supabase
      .from('rides')
      .select(`
        *,
        driver:users!rides_driver_id_fkey(*),
        passenger:users!rides_passenger_id_fkey(*)
      `)
      .order('created_at', { ascending: false })
    
    if (error) throw error
    return data
  },

  async getRideById(id) {
    const { data, error } = await supabase
      .from('rides')
      .select(`
        *,
        driver:users!rides_driver_id_fkey(*),
        passenger:users!rides_passenger_id_fkey(*)
      `)
      .eq('id', id)
      .single()
    
    if (error) throw error
    return data
  },

  async createRide(rideData) {
    const { data, error } = await supabase
      .from('rides')
      .insert([rideData])
      .select(`
        *,
        driver:users!rides_driver_id_fkey(*),
        passenger:users!rides_passenger_id_fkey(*)
      `)
      .single()
    
    if (error) throw error
    return data
  },

  async updateRide(id, rideData) {
    const { data, error } = await supabase
      .from('rides')
      .update(rideData)
      .eq('id', id)
      .select(`
        *,
        driver:users!rides_driver_id_fkey(*),
        passenger:users!rides_passenger_id_fkey(*)
      `)
      .single()
    
    if (error) throw error
    return data
  },

  // Authentication helpers
  async signUp(email, password, userData = {}) {
    const { data, error } = await supabase.auth.signUp({
      email,
      password,
      options: {
        data: userData
      }
    })
    
    if (error) throw error
    return data
  },

  async signIn(email, password) {
    const { data, error } = await supabase.auth.signInWithPassword({
      email,
      password
    })
    
    if (error) throw error
    return data
  },

  async signOut() {
    const { error } = await supabase.auth.signOut()
    if (error) throw error
    return true
  },

  async getCurrentUser() {
    const { data: { user }, error } = await supabase.auth.getUser()
    if (error) throw error
    return user
  }
}
