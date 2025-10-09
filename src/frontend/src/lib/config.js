// Configuration utility for the application
export const config = {
  // App info
  appName: 'RideWithUs',
  appVersion: '1.0.0',
  
  // API configuration
  apiBaseUrl: 'http://localhost:8080/api',
  apiTimeout: 10000,
  
  // Supabase configuration
  supabaseUrl: 'https://hpgawptoeiwdajfrqvvz.supabase.co',
  supabaseAnonKey: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhwZ2F3cHRvZWl3ZGFqZnJxdnZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5MDEwOTYsImV4cCI6MjA3NTQ3NzA5Nn0.xpD8QznbSLADZU7kUyDFn_4cEJvGeCrG9fKO-LrXKJY',
  
  // Environment
  isDevelopment: import.meta.env.DEV,
  isProduction: import.meta.env.PROD,
  mode: import.meta.env.MODE,
}

// Validate required configuration
export function validateConfig() {
  const required = ['supabaseUrl', 'supabaseAnonKey']
  const missing = required.filter(key => !config[key])
  
  if (missing.length > 0) {
    throw new Error(`Missing required configuration: ${missing.join(', ')}`)
  }
  
  return true
}

// Get environment-specific configuration
export function getEnvironmentConfig() {
  return {
    development: {
      apiBaseUrl: 'http://localhost:8080/api',
      debug: true,
      logLevel: 'debug'
    },
    production: {
      apiBaseUrl: 'https://your-backend-domain.com/api',
      debug: false,
      logLevel: 'error'
    }
  }[config.mode] || {
    apiBaseUrl: 'http://localhost:8080/api',
    debug: true,
    logLevel: 'info'
  }
}
