// API Client for backend communication
const API_BASE_URL = 'http://localhost:8080/api'
const API_TIMEOUT = 10000

class ApiClient {
  constructor() {
    this.baseURL = API_BASE_URL
    this.timeout = API_TIMEOUT
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    }

    try {
      const controller = new AbortController()
      const timeoutId = setTimeout(() => controller.abort(), this.timeout)
      
      const response = await fetch(url, {
        ...config,
        signal: controller.signal
      })
      
      clearTimeout(timeoutId)
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      if (error.name === 'AbortError') {
        throw new Error('Request timeout')
      }
      console.error('API request failed:', error)
      throw error
    }
  }

  // User API methods
  async getUsers() {
    return this.request('/users')
  }

  async getUserById(id) {
    return this.request(`/users/${id}`)
  }

  async getUserByEmail(email) {
    return this.request(`/users/email/${email}`)
  }

  async getUsersByType(userType) {
    return this.request(`/users/type/${userType}`)
  }

  async getAvailableDrivers() {
    return this.request('/users/drivers/available')
  }

  async createUser(userData) {
    return this.request('/users', {
      method: 'POST',
      body: JSON.stringify(userData),
    })
  }

  async updateUser(id, userData) {
    return this.request(`/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(userData),
    })
  }

  async deleteUser(id) {
    return this.request(`/users/${id}`, {
      method: 'DELETE',
    })
  }

  // Ride API methods (when you implement them in backend)
  async getRides() {
    return this.request('/rides')
  }

  async getRideById(id) {
    return this.request(`/rides/${id}`)
  }

  async createRide(rideData) {
    return this.request('/rides', {
      method: 'POST',
      body: JSON.stringify(rideData),
    })
  }

  async updateRide(id, rideData) {
    return this.request(`/rides/${id}`, {
      method: 'PUT',
      body: JSON.stringify(rideData),
    })
  }

  async deleteRide(id) {
    return this.request(`/rides/${id}`, {
      method: 'DELETE',
    })
  }

  // Health check
  async healthCheck() {
    return this.request('/users/health')
  }

  // Utility methods
  async testConnection() {
    try {
      await this.healthCheck()
      return { success: true, message: 'Backend API is accessible' }
    } catch (error) {
      return { success: false, message: error.message }
    }
  }
}

export const apiClient = new ApiClient()
