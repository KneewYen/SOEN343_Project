import { ref, computed } from 'vue'

// Current active mode for dual users: 'rider' or 'operator'
const currentMode = ref(localStorage.getItem('dualMode') || 'rider')

export function useDualMode() {
  const setMode = (mode) => {
    if (mode === 'rider' || mode === 'operator') {
      currentMode.value = mode
      localStorage.setItem('dualMode', mode)
    }
  }

  const getMode = () => {
    return currentMode.value
  }

  const isRiderMode = computed(() => currentMode.value === 'rider')
  const isOperatorMode = computed(() => currentMode.value === 'operator')

  return {
    currentMode: computed(() => currentMode.value),
    setMode,
    getMode,
    isRiderMode,
    isOperatorMode
  }
}

