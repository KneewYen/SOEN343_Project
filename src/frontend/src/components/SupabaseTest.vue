<template>
  <div class="supabase-test">
    <h2>🔧 Connection Tests</h2>
    
    <!-- Supabase Connection Test -->
    <div class="test-section">
      <h3>Supabase Connection</h3>
      <div v-if="supabaseLoading" class="loading">
        <span class="spinner"></span> Testing Supabase connection...
      </div>
      <div v-else-if="supabaseError" class="error">
        ❌ Supabase Error: {{ supabaseError }}
      </div>
      <div v-else class="success">
        ✅ Supabase connected successfully!
        <div class="details">
          <p><strong>URL:</strong> {{ config.supabaseUrl }}</p>
          <p><strong>Status:</strong> Connected</p>
        </div>
      </div>
    </div>

    <!-- Backend API Test -->
    <div class="test-section">
      <h3>Backend API Connection</h3>
      <div v-if="apiLoading" class="loading">
        <span class="spinner"></span> Testing backend API...
      </div>
      <div v-else-if="apiError" class="error">
        ❌ Backend API Error: {{ apiError }}
      </div>
      <div v-else class="success">
        ✅ Backend API is working!
        <div class="details">
          <p><strong>URL:</strong> {{ config.apiBaseUrl }}</p>
          <p><strong>Response:</strong> {{ apiResponse }}</p>
        </div>
      </div>
    </div>

    <!-- Configuration Display -->
    <div class="test-section">
      <h3>Configuration</h3>
      <div class="config-display">
        <div class="config-item">
          <strong>App Name:</strong> {{ config.appName }}
        </div>
        <div class="config-item">
          <strong>App Version:</strong> {{ config.appVersion }}
        </div>
        <div class="config-item">
          <strong>Environment:</strong> {{ config.mode }}
        </div>
        <div class="config-item">
          <strong>Development Mode:</strong> {{ config.isDevelopment ? 'Yes' : 'No' }}
        </div>
      </div>
    </div>

    <!-- Test Actions -->
    <div class="test-actions">
      <button @click="runTests" :disabled="supabaseLoading || apiLoading" class="test-button">
        🔄 Run Tests Again
      </button>
      <button @click="testSupabaseOperations" :disabled="supabaseLoading" class="test-button">
        🧪 Test Supabase Operations
      </button>
    </div>

    <!-- Supabase Operations Test Results -->
    <div v-if="operationsResult" class="test-section">
      <h3>Supabase Operations Test</h3>
      <div v-if="operationsResult.success" class="success">
        ✅ Supabase operations working correctly!
        <div class="details">
          <p><strong>Users Count:</strong> {{ operationsResult.usersCount }}</p>
          <p><strong>Rides Count:</strong> {{ operationsResult.ridesCount }}</p>
        </div>
      </div>
      <div v-else class="error">
        ❌ Supabase operations failed: {{ operationsResult.error }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { supabase, supabaseHelpers } from '@/lib/supabase'
import { apiClient } from '@/lib/api'
import { config } from '@/lib/config'

const supabaseLoading = ref(true)
const supabaseError = ref(null)
const apiLoading = ref(true)
const apiError = ref(null)
const apiResponse = ref('')
const operationsResult = ref(null)

const runTests = async () => {
  supabaseLoading.value = true
  apiLoading.value = true
  supabaseError.value = null
  apiError.value = null
  operationsResult.value = null

  await Promise.all([
    testSupabaseConnection(),
    testBackendAPI()
  ])
}

const testSupabaseConnection = async () => {
  try {
    // Test basic connection by trying to access a table
    const { data, error } = await supabase
      .from('users')
      .select('count')
      .limit(1)
    
    if (error) {
      // If users table doesn't exist, that's okay - just means we need to create it
      if (error.code === 'PGRST116') {
        console.log('Users table not found - this is expected if not created yet')
        supabaseError.value = null
      } else {
        throw error
      }
    } else {
      supabaseError.value = null
    }
  } catch (err) {
    supabaseError.value = err.message
  } finally {
    supabaseLoading.value = false
  }
}

const testBackendAPI = async () => {
  try {
    const response = await apiClient.healthCheck()
    apiResponse.value = response
    apiError.value = null
  } catch (err) {
    apiError.value = err.message
  } finally {
    apiLoading.value = false
  }
}

const testSupabaseOperations = async () => {
  try {
    // Test if we can perform basic operations
    const usersResult = await supabaseHelpers.getUsers()
    const ridesResult = await supabaseHelpers.getRides()
    
    operationsResult.value = {
      success: true,
      usersCount: usersResult?.length || 0,
      ridesCount: ridesResult?.length || 0
    }
  } catch (error) {
    operationsResult.value = {
      success: false,
      error: error.message
    }
  }
}

onMounted(() => {
  runTests()
})
</script>

<style scoped>
.supabase-test {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #f9f9f9;
}

.test-section h3 {
  margin-top: 0;
  color: #333;
}

.loading {
  display: flex;
  align-items: center;
  color: #666;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  color: #e74c3c;
  background: #fdf2f2;
  padding: 10px;
  border-radius: 4px;
  border-left: 4px solid #e74c3c;
}

.success {
  color: #27ae60;
  background: #f2fdf2;
  padding: 10px;
  border-radius: 4px;
  border-left: 4px solid #27ae60;
}

.details {
  margin-top: 10px;
  font-size: 0.9em;
  color: #666;
}

.details p {
  margin: 5px 0;
}

.config-display {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
}

.config-item {
  padding: 8px;
  background: white;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.test-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.test-button {
  padding: 10px 20px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.test-button:hover:not(:disabled) {
  background: #2980b9;
}

.test-button:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}
</style>
