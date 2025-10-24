import { ref, watch } from 'vue'
import { themes } from '../lib/theme.js'

const isDarkMode = ref(false)

// Initialize from localStorage
if (typeof window !== 'undefined') {
  const savedTheme = localStorage.getItem('theme')
  isDarkMode.value = savedTheme === 'dark'
}

// Apply theme variables to CSS
const applyTheme = (isDark) => {
  if (typeof window !== 'undefined') {
    const theme = isDark ? themes.dark : themes.light
    const root = document.documentElement
    
    // Apply dashboard-specific variables
    root.style.setProperty('--bg-primary', theme.bgPrimary)
    root.style.setProperty('--bg-secondary', theme.bgSecondary)
    root.style.setProperty('--bg-tertiary', theme.bgTertiary)
    root.style.setProperty('--bg-hover', theme.bgHover)
    root.style.setProperty('--text-primary', theme.textPrimary)
    root.style.setProperty('--text-secondary', theme.textSecondary)
    root.style.setProperty('--border-color', theme.borderColor)
    root.style.setProperty('--accent-color', theme.accentColor)
    root.style.setProperty('--accent-hover', theme.accentHover)
    
    // Apply new template variables
    root.style.setProperty('--gradient', theme.gradient)
    root.style.setProperty('--surface', theme.surface)
    root.style.setProperty('--surface-hover', theme.surfaceHover)
    root.style.setProperty('--text', theme.text)
    root.style.setProperty('--text-secondary', theme.textSecondary)
    root.style.setProperty('--border', theme.border)
    root.style.setProperty('--primary', theme.primary)
    root.style.setProperty('--primary-hover', theme.primaryHover)
    root.style.setProperty('--card-shadow', theme.cardShadow)
  }
}

// Apply initial theme
applyTheme(isDarkMode.value)

// Watch for changes and save to localStorage
watch(isDarkMode, (newValue) => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('theme', newValue ? 'dark' : 'light')
    document.documentElement.classList.toggle('dark', newValue)
    applyTheme(newValue)
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

