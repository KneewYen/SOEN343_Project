import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Toast from "vue3-toastify"
import "vue3-toastify/dist/index.css"

const app = createApp(App)
app.use(Toast)
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
