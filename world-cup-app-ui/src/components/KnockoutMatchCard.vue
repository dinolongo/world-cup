<script setup>
import { computed } from 'vue'
import { teamCrests } from '../data/constants'

const props = defineProps({
  match: {
    type: Object,
    required: true
  },
  team1Name: {
    type: String,
    required: true
  },
  team2Name: {
    type: String,
    required: true
  },
  stadium: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['select-winner'])

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

const formatTime = (timeStr) => {
  return timeStr
}

const getTeamCrest = (teamName) => {
  return teamCrests[teamName] || null
}

const handleTeamClick = (team) => {
  emit('select-winner', team)
}
</script>

<template>
  <div class="knockout-match-card">
    <div class="match-info">
      <span class="date-time">{{ formatDate(match.date) }} • {{ formatTime(match.time) }}</span>
      <span class="stadium">{{ stadium?.name || match.ground }}</span>
      <span class="city">{{ stadium?.city || '' }}</span>
    </div>
    <div class="teams">
      <button 
        class="team-button"
        @click="handleTeamClick(team1Name)"
      >
        <img 
          v-if="getTeamCrest(team1Name)" 
          :src="getTeamCrest(team1Name)" 
          class="team-flag"
          alt=""
        />
        {{ team1Name }}
      </button>
      <button 
        class="team-button"
        @click="handleTeamClick(team2Name)"
      >
        <img 
          v-if="getTeamCrest(team2Name)" 
          :src="getTeamCrest(team2Name)" 
          class="team-flag"
          alt=""
        />
        {{ team2Name }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.knockout-match-card {
  background: white;
  border-radius: 8px;
  padding: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-width: 130px;   /* was 108px */
  max-width: 144px;   /* was 120px */
}

.match-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-bottom: 6px;
  font-size: 10px;
  color: #666;
}

.date-time {
  font-weight: 600;
  color: #333;
}

.stadium {
  color: #666;
  font-weight: 500;
}

.city {
  color: #999;
  font-size: 11px;
}

.teams {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.team-button {
  background: #f5f5f5;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  padding: 6px 8px;
  font-size: 11px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.team-button:hover {
  background: #e8f4e8;
  border-color: #4caf50;
  color: #2e7d32;
}

.team-button:active {
  transform: scale(0.98);
}

.team-flag {
  width: 18px;
  height: 18px;
  object-fit: contain;
  flex-shrink: 0;
}
</style>
