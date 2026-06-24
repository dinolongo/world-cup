<script setup>
import { teamCrests } from '../util/constants'

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
  team1Id: {
    type: [String, null],
    required: true
  },
  team2Id: {
    type: [String, null],
    required: true
  },
  stadium: {
    type: Object,
    default: null
  },
  matchNum: {
    type: Number,
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

const getTeamCrest = (teamName) => teamCrests[teamName] ?? null


const handleTeamClick = (teamId) => {
  if (!teamId) {
    return
  }
  emit('select-winner', teamId)
}
</script>

<template>
  <div class="knockout-match-card">
    <div class="match-info">
      <span class="match-number" v-if="matchNum">#{{ matchNum }}</span>
      <span class="date-time">{{ formatDate(match.date) }} • {{ formatTime(match.time) }}</span>
      <span class="stadium">{{ stadium?.name || match.ground }}</span>
      <span class="city">{{ stadium?.city || '' }}</span>
    </div>
    <div class="teams">
      <button 
        class="team-button"
        @click="handleTeamClick(team1Id)"
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
        @click="handleTeamClick(team2Id)"
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
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  border-radius: 8px;
  padding: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  min-width: 130px;
  max-width: 144px;
}

@media (max-width: 960px) {
  .knockout-match-card {
    min-width: unset;
    max-width: unset;
    width: 100%;
  }
}

.match-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-bottom: 6px;
  font-size: 10px;
  color: rgba(255, 255, 255, 0.8);
}

.match-number {
  font-weight: 700;
  color: #ffd700;
  font-size: 11px;
}

.date-time {
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

.stadium {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.city {
  color: rgba(255, 255, 255, 0.6);
  font-size: 11px;
}

.teams {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.team-button {
  background: #3d3d3d;
  border: 2px solid #4d4d4d;
  border-radius: 6px;
  padding: 6px 8px;
  font-size: 11px;
  font-weight: 500;
  color: #e0e0e0;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.team-button:hover {
  background: #4d4d4d;
  border-color: #5d5d5d;
  color: white;
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
