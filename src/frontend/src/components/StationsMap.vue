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
            <p><strong>Available Bikes:</strong> {{ getAvailableBikesCount(station) }} | <strong>Free Docks:</strong> {{ getFreeDocksCount(station) }}</p>
            <p><strong>Status:</strong> {{ station.status }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

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

const selectedStation = ref(null)
const error = ref(null)

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
</style>
