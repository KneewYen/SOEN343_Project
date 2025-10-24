<template>
  <div class="map-container">
    <h3>Station Locations</h3>
    <div v-if="loading" class="map-loading">Loading map...</div>
    <div v-else-if="error" class="map-fallback">
      <div class="fallback-content">
        <h4>📍 Station Locations</h4>
        <p>Google Maps API key not configured. Showing station list below.</p>
        <div class="coordinates-info">
          <p><strong>Map Center:</strong> Montreal, QC (45.5017, -73.5673)</p>
          <p><strong>Stations with coordinates:</strong> {{ stationsWithCoords.length }}</p>
        </div>
      </div>
    </div>
    <div v-else class="map-wrapper">
      <GMapMap
        :center="mapCenter"
        :zoom="13"
        map-type-id="roadmap"
        style="width: 100%; height: 400px"
        :options="mapOptions"
      >
        <GMapMarker
          v-for="station in stationsWithCoords"
          :key="station.id"
          :position="{ lat: station.latitude, lng: station.longitude }"
          :clickable="true"
          @click="selectStation(station)"
        >
          <GMapInfoWindow v-if="selectedStation?.id === station.id">
            <div class="info-window">
              <h4>{{ station.name }}</h4>
              <p><strong>Address:</strong> {{ station.address }}</p>
              <p><strong>Available Bikes:</strong> {{ getAvailableBikesCount(station) }}</p>
              <p><strong>Free Docks:</strong> {{ getFreeDocksCount(station) }}</p>
              <p><strong>Status:</strong> {{ station.status }}</p>
              <div v-if="canReserveBike(station)" class="reservation-actions">
                <button @click="showBikeSelection(station)" class="reserve-btn">
                  Reserve Bike
                </button>
              </div>
            </div>
          </GMapInfoWindow>
        </GMapMarker>
      </GMapMap>
    </div>
    
    <!-- Station List Under Map -->
    <div class="station-list-section">
      <h4>All Stations ({{ stations.length }})</h4>
      <div class="station-list">
        <div v-for="station in stations" :key="station.id" class="station-item" :class="{ 'selected': selectedStation?.id === station.id }" @click="selectStation(station)">
          <div class="station-header">
            <strong>{{ station.name }}</strong>
            <span v-if="station.latitude && station.longitude" class="map-indicator">📍</span>
            <span v-else class="no-coords">⚠️ No coordinates</span>
          </div>
          <div class="station-details">
            <p><strong>Address:</strong> {{ station.address }}</p>
            <div class="station-stats">
              <div class="stat-item">
                <span class="stat-label">Available Bikes:</span>
                <span class="stat-value">{{ getAvailableBikesCount(station) }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Free Docks:</span>
                <span class="stat-value">{{ getFreeDocksCount(station) }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Status:</span>
                <span class="stat-value status" :class="station.status.toLowerCase()">{{ station.status }}</span>
              </div>
            </div>
            
            <!-- Detailed Dock View -->
            <div class="dock-grid" v-if="station.dockIds && station.dockIds.length > 0">
              <h5>Dock Status</h5>
              <div class="docks-container">
                <div 
                  v-for="dock in station.dockIds" 
                  :key="dock.id" 
                  class="dock-item"
                  :class="getDockStatusClass(dock)"
                >
                  <div class="dock-header">
                    <span class="dock-number">Dock {{ dock.id }}</span>
                    <span class="dock-status">{{ getDockStatusText(dock) }}</span>
                  </div>
                  <div v-if="dock.bikeId" class="bike-info">
                    <span class="bike-id">Bike #{{ dock.bikeId }}</span>
                    <span class="bike-status" :class="getBikeStatusClass(dock)">{{ getBikeStatusText(dock) }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-if="canReserveBike(station)" class="station-actions">
              <button @click="showBikeSelection(station)" class="reserve-btn">
                Reserve Bike
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bike Selection Modal -->
    <div v-if="showBikeModal" class="modal-overlay" @click="closeBikeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Select a Bike</h3>
          <button @click="closeBikeModal" class="close-btn">&times;</button>
        </div>
        <div class="modal-body">
          <p>Choose a bike to reserve at {{ selectedStationForBike?.name }}:</p>
          <div class="bikes-list">
            <div 
              v-for="bike in availableBikes" 
              :key="bike.id"
              class="bike-option"
              @click="selectBike(bike)"
            >
              <div class="bike-info">
                <h4>Bike #{{ bike.id }}</h4>
                <p>Type: {{ bike.type || 'Standard' }}</p>
                <p>Status: {{ bike.status }}</p>
              </div>
              <div class="bike-status" :class="getBikeStatusClass(bike)">
                {{ getBikeStatusText(bike) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import apiClient from '../lib/api'

const props = defineProps({
  stations: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['bikeReserved'])

const selectedStation = ref(null)
const error = ref(null)
const showBikeModal = ref(false)
const selectedStationForBike = ref(null)
const availableBikes = ref([])

// Map center on Montreal
const mapCenter = computed(() => ({
  lat: 45.5017,
  lng: -73.5673
}))

// Filter stations that have coordinates for map markers
const stationsWithCoords = computed(() => {
  return props.stations.filter(station => 
    station.latitude && station.longitude && 
    !isNaN(station.latitude) && !isNaN(station.longitude)
  )
})

// Map styling options to match RideWithUs theme
const mapOptions = {
  styles: [
    {
      featureType: 'poi',
      elementType: 'labels',
      stylers: [{ visibility: 'off' }]
    },
    {
      featureType: 'transit',
      elementType: 'labels',
      stylers: [{ visibility: 'off' }]
    }
  ],
  disableDefaultUI: false,
  zoomControl: true,
  mapTypeControl: false,
  scaleControl: true,
  streetViewControl: false,
  rotateControl: false,
  fullscreenControl: true
}

const selectStation = (station) => {
  selectedStation.value = station
}

const getFreeDocksCount = (station) => {
  if (!station.dockIds) return 0
  return station.dockIds.filter(dock => dock.status === 'EMPTY').length
}

const getAvailableBikesCount = (station) => {
  if (!station.dockIds) return 0
  return station.dockIds.filter(dock => dock.status === 'OCCUPIED' && dock.bikeId).length
}

const canReserveBike = (station) => {
  return station.status === 'ACTIVE' && getAvailableBikesCount(station) > 0
}

const showBikeSelection = async (station) => {
  try {
    selectedStationForBike.value = station
    const bikes = await apiClient.getAvailableBikes(station.id)
    availableBikes.value = bikes
    showBikeModal.value = true
  } catch (error) {
    console.error('Error loading bikes:', error)
    alert('Failed to load available bikes')
  }
}

const closeBikeModal = () => {
  showBikeModal.value = false
  selectedStationForBike.value = null
  availableBikes.value = []
}

const selectBike = (bike) => {
  emit('bikeReserved', bike, selectedStationForBike.value)
  closeBikeModal()
}

const getBikeStatusClass = (bike) => {
  if (bike.status === 'AVAILABLE') return 'available'
  if (bike.status === 'RESERVED') return 'reserved'
  if (bike.status === 'ON_TRIP') return 'on-trip'
  if (bike.status === 'MAINTENANCE') return 'maintenance'
  return 'unknown'
}

const getBikeStatusText = (bike) => {
  if (bike.status === 'AVAILABLE') return 'Available'
  if (bike.status === 'RESERVED') return 'Reserved'
  if (bike.status === 'ON_TRIP') return 'On Trip'
  if (bike.status === 'MAINTENANCE') return 'Maintenance'
  return 'Unknown'
}

const getDockStatusClass = (dock) => {
  if (dock.status === 'EMPTY') return 'empty'
  if (dock.status === 'OCCUPIED') return 'occupied'
  if (dock.status === 'MAINTENANCE') return 'maintenance'
  return 'unknown'
}

const getDockStatusText = (dock) => {
  if (dock.status === 'EMPTY') return 'Empty'
  if (dock.status === 'OCCUPIED') return 'Occupied'
  if (dock.status === 'MAINTENANCE') return 'Maintenance'
  return 'Unknown'
}

// Handle map load errors and debugging
onMounted(() => {
  // Check if Google Maps API key is available
  if (!import.meta.env.VITE_GOOGLE_MAPS_API_KEY) {
    error.value = 'Google Maps API key not configured'
  }
  
  // Debug: Log station data
  console.log('Stations data:', props.stations)
  console.log('Stations with coordinates:', stationsWithCoords.value)
  console.log('Total stations:', props.stations.length)
  console.log('Stations with coords:', stationsWithCoords.value.length)
})
</script>

<style scoped>
.map-container {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(255, 107, 157, 0.1);
  margin-bottom: 20px;
}

.map-container h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.2rem;
}

.map-wrapper {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.map-loading, .map-error {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 8px;
  color: #666;
  text-align: center;
}

.map-error {
  color: #e74c3c;
}

.map-fallback {
  background: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  margin-bottom: 20px;
}

.fallback-content h4 {
  color: #1e293b;
  margin-bottom: 10px;
  font-size: 18px;
}

.fallback-content p {
  color: #64748b;
  margin-bottom: 15px;
}

.coordinates-info {
  background: white;
  border-radius: 8px;
  padding: 15px;
  border: 1px solid #e2e8f0;
}

.coordinates-info p {
  margin: 5px 0;
  font-size: 14px;
}

.info-window {
  padding: 10px;
  min-width: 200px;
}

.info-window h4 {
  color: #ff6b9d;
  margin-bottom: 8px;
  font-size: 1.1rem;
}

.info-window p {
  margin: 4px 0;
  font-size: 0.9rem;
  color: #333;
}

/* Station List Styles */
.station-list-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid #f0f0f0;
}

.station-list-section h4 {
  color: #ff6b9d;
  margin-bottom: 15px;
  font-size: 1.1rem;
}

.station-list {
  max-height: 300px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.station-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
}

.station-item:hover {
  background: #fff;
  border-color: #ff6b9d;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.1);
}

.station-item.selected {
  background: #fff;
  border-color: #ff6b9d;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.2);
}

.station-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.station-header strong {
  color: #ff6b9d;
  font-size: 1rem;
}

.map-indicator {
  font-size: 1.2rem;
}

.no-coords {
  color: #e74c3c;
  font-size: 0.9rem;
}

.station-details p {
  margin: 4px 0;
  font-size: 0.9rem;
  color: #666;
}

.station-details strong {
  color: #333;
}

/* Reservation Button Styles */
.reservation-actions, .station-actions {
  margin-top: 12px;
}

.reserve-btn {
  background: #ff6b9d;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reserve-btn:hover {
  background: #e55a8a;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 24px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  border: 2px solid #e2e8f0;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
  color: #1e293b;
  font-size: 20px;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #64748b;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.modal-body p {
  margin-bottom: 16px;
  color: #64748b;
}

.bikes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bike-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.bike-option:hover {
  border-color: #ff6b9d;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.bike-info h4 {
  margin: 0 0 4px 0;
  color: #1e293b;
  font-size: 16px;
  font-weight: 600;
}

.bike-info p {
  margin: 4px 0;
  color: #64748b;
  font-size: 14px;
}

.bike-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.bike-status.available {
  background: #dcfce7;
  color: #166534;
}

.bike-status.reserved {
  background: #fef3c7;
  color: #92400e;
}

.bike-status.on-trip {
  background: #dbeafe;
  color: #1e40af;
}

.bike-status.maintenance {
  background: #fee2e2;
  color: #991b1b;
}

.bike-status.unknown {
  background: #f3f4f6;
  color: #6b7280;
}

/* Dock Status Styles */
.dock-grid {
  margin-top: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.dock-grid h5 {
  margin: 0 0 12px 0;
  color: #1e293b;
  font-size: 14px;
  font-weight: 600;
}

.docks-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}

.dock-item {
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: white;
  transition: all 0.2s ease;
}

.dock-item.empty {
  border-color: #22c55e;
  background: #f0fdf4;
}

.dock-item.occupied {
  border-color: #3b82f6;
  background: #eff6ff;
}

.dock-item.maintenance {
  border-color: #f59e0b;
  background: #fffbeb;
}

.dock-item.unknown {
  border-color: #6b7280;
  background: #f9fafb;
}

.dock-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.dock-number {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
}

.dock-status {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 500;
  text-transform: uppercase;
}

.dock-item.empty .dock-status {
  background: #dcfce7;
  color: #166534;
}

.dock-item.occupied .dock-status {
  background: #dbeafe;
  color: #1e40af;
}

.dock-item.maintenance .dock-status {
  background: #fef3c7;
  color: #92400e;
}

.dock-item.unknown .dock-status {
  background: #f3f4f6;
  color: #6b7280;
}

.bike-info {
  font-size: 10px;
  color: #6b7280;
}

.bike-id {
  font-weight: 500;
  color: #374151;
}

.bike-status {
  font-size: 9px;
  padding: 1px 4px;
  border-radius: 8px;
  font-weight: 500;
  text-transform: uppercase;
  margin-top: 2px;
  display: inline-block;
}
</style>
