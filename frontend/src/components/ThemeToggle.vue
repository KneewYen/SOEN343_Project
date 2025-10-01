<template>
  <button @click="toggleTheme" class="theme-toggle">
    <div class="icon">
      <div class="sun" v-if="!isDark"></div>
      <div class="moon" v-if="isDark"></div>
    </div>
  </button>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const isDark = ref(false)

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.body.classList.toggle('dark-theme', isDark.value)
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    isDark.value = true
    document.body.classList.add('dark-theme')
  }
})
</script>

<style scoped>
.theme-toggle {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 60px;
  height: 60px;
  border: 2px solid var(--primary-red);
  background: var(--white);
  border-radius: 50%;
  cursor: pointer;
  z-index: 1000;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.theme-toggle:hover {
  transform: scale(1.1) translateY(-2px);
  box-shadow: 0 8px 25px rgba(220, 53, 69, 0.3);
}

.theme-toggle:active {
  transform: scale(0.95);
}

.dark-theme .theme-toggle {
  background: var(--grey-800);
  border-color: var(--primary-red);
}

.dark-theme .theme-toggle:hover {
  background: var(--grey-700);
  box-shadow: 0 8px 25px rgba(220, 53, 69, 0.4);
}

/* Icon container */
.icon {
  position: relative;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Sun icon - simple circle with rays */
.sun {
  width: 20px;
  height: 20px;
  background: var(--primary-red);
  border-radius: 50%;
  position: relative;
  animation: rotate 20s linear infinite;
}

.sun::before,
.sun::after {
  content: '';
  position: absolute;
  background: var(--primary-red);
  border-radius: 2px;
}

.sun::before {
  width: 2px;
  height: 8px;
  top: -4px;
  left: 50%;
  transform: translateX(-50%);
}

.sun::after {
  width: 2px;
  height: 8px;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
}

/* Moon icon - crescent shape */
.moon {
  width: 20px;
  height: 20px;
  background: var(--white);
  border-radius: 50%;
  position: relative;
  box-shadow: -4px 0 0 var(--primary-red);
}

.moon::before {
  content: '';
  position: absolute;
  width: 16px;
  height: 16px;
  background: var(--grey-800);
  border-radius: 50%;
  top: 2px;
  left: 2px;
}

/* Dark theme moon */
.dark-theme .moon {
  background: var(--primary-red);
  box-shadow: -4px 0 0 var(--white);
}

.dark-theme .moon::before {
  background: var(--grey-800);
}

/* Animations */
@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .theme-toggle {
    bottom: 16px;
    right: 16px;
    width: 50px;
    height: 50px;
  }
  
  .icon {
    width: 20px;
    height: 20px;
  }
  
  .sun, .moon {
    width: 16px;
    height: 16px;
  }
  
  .sun::before,
  .sun::after {
    height: 6px;
  }
  
  .moon {
    box-shadow: -3px 0 0 var(--primary-red);
  }
  
  .dark-theme .moon {
    box-shadow: -3px 0 0 var(--white);
  }
}
</style>
