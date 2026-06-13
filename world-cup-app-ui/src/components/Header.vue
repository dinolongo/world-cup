<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const navigateTo = (path) => {
  router.push(path)
}

const isActive = (path) => {
  return route.path === path
}
</script>

<template>
  <header class="header">
    <div class="header-content">
      <div class="logo">
        <img src="https://crests.football-data.org/wm26.png" alt="World Cup" class="world-cup-logo">
        <span class="app-title">World Cup 2026</span>
      </div>
      <nav class="nav">
        <button 
          @click="navigateTo('/')" 
          :class="['nav-button', { active: isActive('/') }]"
        >
          Matches
        </button>
        <button 
          @click="navigateTo('/standings')" 
          :class="['nav-button', { active: isActive('/standings') }]"
        >
          Current Standings
        </button>
        <button 
          @click="navigateTo('/prediction')" 
          :class="['nav-button', { active: isActive('/prediction') }]"
        >
          Prediction
        </button>
        <button 
          @click="navigateTo('/knockouts')" 
          :class="['nav-button', { active: isActive('/knockouts') }]"
        >
          Knockouts
        </button>
        <button 
          @click="navigateTo('/leaderboard')" 
          :class="['nav-button', { active: isActive('/leaderboard') }]"
        >
          Leaderboard
        </button>
        <button 
          @click="navigateTo('/about')" 
          :class="['nav-button', { active: isActive('/about') }]"
        >
          About
        </button>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.header {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.world-cup-logo {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.app-title {
  color: white;
  font-size: 20px;
  font-weight: 700;
}

.nav {
  display: flex;
  gap: 8px;
}

.nav-button {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.nav-button:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-button.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    padding: 12px 16px;
  }
  
  .app-title {
    font-size: 16px;
  }
  
  .nav {
    width: 100%;
    justify-content: center;
  }
  
  .nav-button {
    padding: 8px 12px;
    font-size: 12px;
  }
}
</style>
