<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<script>
import { RouterView } from 'vue-router'
import { useTheme } from './composables/useTheme'
import { onMounted, watch } from 'vue'

export default {
  name: 'App',
  components: {
    RouterView
  },
  setup() {
    const { isDarkMode } = useTheme()

    const updateTheme = (dark) => {
      const root = document.documentElement

      if (dark) {
        // Dark theme
        root.style.setProperty('--primary', '#ec4899')
        root.style.setProperty('--primary-hover', '#f472b6')
        root.style.setProperty('--primary-light', '#831843')
        root.style.setProperty('--secondary', '#f472b6')
        root.style.setProperty('--background', '#111827')
        root.style.setProperty('--surface', '#1f2937')
        root.style.setProperty('--surface-hover', '#374151')
        root.style.setProperty('--text', '#f9fafb')
        root.style.setProperty('--text-secondary', '#d1d5db')
        root.style.setProperty('--border', '#9d174d')
        root.style.setProperty('--error', '#f87171')
        root.style.setProperty('--error-bg', '#7f1d1d')
        root.style.setProperty('--success', '#34d399')
        root.style.setProperty('--success-bg', '#064e3b')
        root.style.setProperty('--gradient', 'linear-gradient(135deg, #ec4899 0%, #db2777 50%, #be185d 100%)')
        root.style.setProperty('--card-shadow', '0 10px 40px rgba(0, 0, 0, 0.5)')
      } else {
        // Light theme
        root.style.setProperty('--primary', '#ec4899')
        root.style.setProperty('--primary-hover', '#db2777')
        root.style.setProperty('--primary-light', '#fbcfe8')
        root.style.setProperty('--secondary', '#f472b6')
        root.style.setProperty('--background', '#ffffff')
        root.style.setProperty('--surface', '#fdf2f8')
        root.style.setProperty('--surface-hover', '#fce7f3')
        root.style.setProperty('--text', '#1f2937')
        root.style.setProperty('--text-secondary', '#6b7280')
        root.style.setProperty('--border', '#f9a8d4')
        root.style.setProperty('--error', '#ef4444')
        root.style.setProperty('--error-bg', '#fee2e2')
        root.style.setProperty('--success', '#10b981')
        root.style.setProperty('--success-bg', '#d1fae5')
        root.style.setProperty('--gradient', 'linear-gradient(135deg, #ec4899 0%, #db2777 50%, #be185d 100%)')
        root.style.setProperty('--card-shadow', '0 10px 40px rgba(236, 72, 153, 0.15)')
      }
    }

    onMounted(() => {
      updateTheme(isDarkMode.value)
    })

    watch(isDarkMode, (newValue) => {
      updateTheme(newValue)
    })

    return {}
  }
}
</script>

<style>
/* Global reset and base styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

body {
  width: 100%;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: var(--background);
  color: var(--text);
  transition: background-color 0.3s ease, color 0.3s ease;
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Smooth transitions for theme changes */
* {
  transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
}

/* Responsive design for different screen sizes */
@media (max-width: 480px) {
  /* Mobile phones */
  body {
    font-size: 14px;
  }
}

@media (min-width: 481px) and (max-width: 768px) {
  /* Large phones and small tablets */
  body {
    font-size: 15px;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  /* Tablets */
  body {
    font-size: 16px;
  }
}

@media (min-width: 1025px) and (max-width: 1440px) {
  /* Laptops and small desktops */
  body {
    font-size: 16px;
  }
}

@media (min-width: 1441px) {
  /* Large desktops */
  body {
    font-size: 18px;
  }
}

/* Ensure full viewport usage */
html, body, #app {
  height: 100%;
  width: 100%;
}
</style>

