import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(router)

// Only load Google Maps if API key is available
if (import.meta.env.VITE_GOOGLE_MAPS_API_KEY) {
  import('@fawmi/vue-google-maps').then(VueGoogleMaps => {
    app.use(VueGoogleMaps.default, {
      load: {
        key: import.meta.env.VITE_GOOGLE_MAPS_API_KEY,
        libraries: 'places'
      }
    })
  })
}

app.mount('#app')
