import { ref, watch } from 'vue'

const isDarkMode = ref(false)

// Initialize from localStorage
if (typeof window !== 'undefined') {
  const savedTheme = localStorage.getItem('theme')
  isDarkMode.value = savedTheme === 'dark'
}

// Watch for changes and save to localStorage
watch(isDarkMode, (newValue) => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('theme', newValue ? 'dark' : 'light')
    document.documentElement.classList.toggle('dark', newValue)
  }
})

export const useTheme = () => {
  const toggleTheme = () => {
    isDarkMode.value = !isDarkMode.value
  }

  return {
    isDarkMode,
    toggleTheme
  }
}

