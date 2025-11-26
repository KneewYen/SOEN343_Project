// API Client for backend communication
const API_BASE_URL = 'http://localhost:8080/api'
const API_TIMEOUT = 30000 // Increased to 30 seconds for database operations

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

  // Authentication API methods
  async register(userData) {
    return this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
      credentials: 'include' // Include cookies for session
    })
  }

  async login(credentials) {
    return this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
      credentials: 'include' // Include cookies for session
    })
  }

  async logout() {
    return this.request('/auth/logout', {
      method: 'POST',
      credentials: 'include'
    })
  }

  async getCurrentUser() {
    return this.request('/auth/me', {
      credentials: 'include'
    })
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

  // Station API methods
  async getAllStations() {
    return this.request('/station/allStations')
  }

  async getNumberOfAvailableBikes(stationId) {
    return this.request(`/station/numberOfAvailableBikes/${stationId}`)
  }

  async getNumberOfFreeDocks(stationId) {
    return this.request(`/station/numberOfFreeDocks/${stationId}`)
  }

  async getAvailableBikes(stationId) {
    return this.request(`/station/availableBikes/${stationId}`)
  }

  async getFreeDocks(stationId) {
    return this.request(`/station/freeDocks/${stationId}`)
  }

  // Reservation API methods
  async getReservation(reservationId) {
    return this.request(`/reservation/${reservationId}`)
  }

  async getUserReservations(userId) {
    return this.request(`/reservation/user/${userId}`)
  }

  async isReservationValid(reservationId) {
    return this.request(`/reservation/valid/${reservationId}`)
  }

  async createReservation(bikeId, userId) {
    return this.request(`/reservation/createReservation/${bikeId}/${userId}`, {
      method: 'POST'
    })
  }

  async deleteReservation(reservationId) {
    return this.request(`/reservation/delete/${reservationId}`, {
      method: 'DELETE'
    })
  }

  // Trip API methods
  async getUserTrips(userId) {
    return this.request(`/trip/user/${userId}`)
  }

  async getAllTrips(page = 0, size = 3) {
    return this.request(`/trip/AllTrips?page=${page}&size=${size}`)
  }
  
  async getUserTrips(userId, page = 0, size = 3) {
    return this.request(`/trip/paginate/user/${userId}?page=${page}&size=${size}`)
  }

  async getIncompleteUserTrips(userId) {
    return this.request(`/trip/incomplete/${userId}`)
  }

  async startTrip(reservationId) {
    return this.request(`/trip/${reservationId}`, {
      method: 'POST'
    })
  }

  async endTrip(tripId, stationId) {
    return this.request(`/trip/${tripId}/${stationId}`, {
      method: 'PUT'
    })
  }

  // Operator API methods
  async toggleBike(bikeId, userId) {
    return this.request(`/operator/bike/${bikeId}/toggle?userId=${userId}`, {
      method: 'POST'
    })
  }

  async toggleDock(dockId, userId) {
    return this.request(`/operator/dock/${dockId}/toggle?userId=${userId}`, {
      method: 'POST'
    })
  }

  async toggleStation(stationId, userId) {
    return this.request(`/operator/station/${stationId}/toggle?userId=${userId}`, {
      method: 'POST'
    })
  }

  async moveBike(bikeId, sourceStationId, destinationStationId, userId) {
    const params = new URLSearchParams({
      bikeId,
      sourceStationId,
      destinationStationId,
      userId
    })
    return this.request(`/operator/moveBike?${params}`, {
      method: 'POST'
    })
  }

  async rebalanceBikes(sourceStationId, destinationStationId, numberOfBikes, userId) {
    const params = new URLSearchParams({
      sourceStationId,
      destinationStationId,
      numberOfBikes,
      userId
    })
    return this.request(`/operator/rebalanceBikes?${params}`, {
      method: 'POST'
    })
  }

  async resetSystem(){
    return this.request('/import/reset', {
      method: 'POST'
    })
  }

  //Event API methods
  async getRecentEvents() {
    return this.request('/events/recent', {
      method: 'GET'
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

  // PRICING
  async getAllPricingPlans() {
    try {
      return await this.request('/pricing/plans', {
        method: 'GET'
      })
    } catch (error) {
      console.error('Error loading pricing plans:', error)
      throw new Error('Failed to load pricing plans')
    }
  }

  async selectPricingPlan(userId, planId) {
    return this.request(`/pricing/select/${userId}/${planId}` ,{
      method: 'POST'
    })
  }

  async calculatePrice(tripId) {
    return this.request(`/trip/calculate/${tripId}` ,{
      method: 'POST'
    })
  }

  // Payment API methods
  async processPayment(paymentData) {
    return this.request('/payment/process', {
      method: 'POST',
      body: JSON.stringify(paymentData)
    })
  }

  async getPaymentStatus(tripId) {
    return this.request(`/payment/status/${tripId}`)
  }

  // Billing API methods
  async createBilling(tripId) {
    return this.request(`/billing/create/${tripId}`, {
      method: 'POST'
    })
  }

  async getBillingByTripId(tripId) {
    return this.request(`/billing/trip/${tripId}`)
  }

  async getBillingsByUserId(userId) {
    return this.request(`/billing/user/${userId}`)
  }

  async submitBikeRating(bikeId, rating){
    return this.request(`/bike/rating/${bikeId}/${rating}`, {
      method: 'POST'
    })
  }

}

const apiClient = new ApiClient()
export default apiClient
