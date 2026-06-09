<script setup>
import { ref, onMounted } from 'vue'
import matchStadiumData from '../data/match-stadium.json'
import stadiumData from '../data/stadium-data.json'

// Reactive state
const knockoutMatches = ref([])
const stadiums = ref([])
const loading = ref(true)

// Load data
onMounted(() => {
  // Filter for knockout stage matches only
  knockoutMatches.value = matchStadiumData.matches.filter(match => 
    match.round === 'Round of 32' || 
    match.round === 'Round of 16' || 
    match.round === 'Quarter-final' || 
    match.round === 'Semi-final' || 
    match.round === 'Match for third place' || 
    match.round === 'Final'
  )
  
  stadiums.value = stadiumData.stadiums
  loading.value = false
})

// Get stadium details by name
const getStadium = (groundName) => {
  return stadiums.value.find(stadium => stadium.name === groundName) || null
}

// Format date for display
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

// Format time for display
const formatTime = (timeStr) => {
  return timeStr
}

// Format team name to show "Winner of Group X" instead of codes like "1A", "2B"
const formatTeamName = (teamCode) => {
  if (!teamCode) return 'TBD'
  
  // Handle group position codes (1A, 2B, 3C, etc.)
  const groupMatch = teamCode.match(/^(\d)([A-L])$/)
  if (groupMatch) {
    const position = groupMatch[1]
    const group = groupMatch[2]
    const positionText = position === '1' ? 'Winner' : position === '2' ? 'Runner-up' : '3rd Place'
    return `${positionText} of Group ${group}`
  }
  
  // Handle winner references (W74, W77, etc.)
  if (teamCode.startsWith('W')) {
    const matchNum = teamCode.substring(1)
    return `Winner of Match ${matchNum}`
  }
  
  // Handle loser references (L101, L102, etc.)
  if (teamCode.startsWith('L')) {
    const matchNum = teamCode.substring(1)
    return `Loser of Match ${matchNum}`
  }
  
  // Handle 3rd place team codes (3A/B/C/D/F, etc.)
  if (teamCode.startsWith('3')) {
    return 'Best 3rd Place Team'
  }
  
  return teamCode
}

// Handle team selection
const selectWinner = (match, team) => {
  // TODO: Implement auto-advance logic
  console.log(`Selected ${team} for match ${match.num}`)
}
</script>

<template>
  <div class="knockouts-page">
    <h1>World Cup 2026 - Knockout Stage</h1>
    
    <div v-if="loading" class="loading">
      Loading bracket...
    </div>
    
    <div v-else class="bracket-container">
      <!-- Round of 32 -->
      <div class="round">
        <h2>Round of 32</h2>
        <div class="matches">
          <div 
            v-for="match in knockoutMatches.filter(m => m.round === 'Round of 32')" 
            :key="match.num"
            class="match-card"
          >
            <div class="match-info">
              <span class="date-time">{{ formatDate(match.date) }} • {{ formatTime(match.time) }}</span>
              <span class="stadium">{{ getStadium(match.ground)?.name || match.ground }}</span>
              <span class="city">{{ getStadium(match.ground)?.city || '' }}</span>
            </div>
            <div class="teams">
              <button 
                class="team-button"
                @click="selectWinner(match, match.team1)"
              >
                {{ formatTeamName(match.team1) }}
              </button>
              <button 
                class="team-button"
                @click="selectWinner(match, match.team2)"
              >
                {{ formatTeamName(match.team2) }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Round of 16 -->
      <div class="round">
        <h2>Round of 16</h2>
        <div class="matches">
          <div 
            v-for="match in knockoutMatches.filter(m => m.round === 'Round of 16')" 
            :key="match.num"
            class="match-card"
          >
            <div class="match-info">
              <span class="date-time">{{ formatDate(match.date) }} • {{ formatTime(match.time) }}</span>
              <span class="stadium">{{ getStadium(match.ground)?.name || match.ground }}</span>
              <span class="city">{{ getStadium(match.ground)?.city || '' }}</span>
            </div>
            <div class="teams">
              <button 
                class="team-button"
                @click="selectWinner(match, match.team1)"
              >
                {{ formatTeamName(match.team1) }}
              </button>
              <button 
                class="team-button"
                @click="selectWinner(match, match.team2)"
              >
                {{ formatTeamName(match.team2) }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Quarter Finals -->
      <div class="round">
        <h2>Quarter Finals</h2>
        <div class="matches">
          <div 
            v-for="match in knockoutMatches.filter(m => m.round === 'Quarter-final')" 
            :key="match.num"
            class="match-card"
          >
            <div class="match-info">
              <span class="date-time">{{ formatDate(match.date) }} • {{ formatTime(match.time) }}</span>
              <span class="stadium">{{ getStadium(match.ground)?.name || match.ground }}</span>
              <span class="city">{{ getStadium(match.ground)?.city || '' }}</span>
            </div>
            <div class="teams">
              <button 
                class="team-button"
                @click="selectWinner(match, match.team1)"
              >
                {{ formatTeamName(match.team1) }}
              </button>
              <button 
                class="team-button"
                @click="selectWinner(match, match.team2)"
              >
                {{ formatTeamName(match.team2) }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Semi Finals -->
      <div class="round">
        <h2>Semi Finals</h2>
        <div class="matches">
          <div 
            v-for="match in knockoutMatches.filter(m => m.round === 'Semi-final')" 
            :key="match.num"
            class="match-card"
          >
            <div class="match-info">
              <span class="date-time">{{ formatDate(match.date) }} • {{ formatTime(match.time) }}</span>
              <span class="stadium">{{ getStadium(match.ground)?.name || match.ground }}</span>
              <span class="city">{{ getStadium(match.ground)?.city || '' }}</span>
            </div>
            <div class="teams">
              <button 
                class="team-button"
                @click="selectWinner(match, match.team1)"
              >
                {{ formatTeamName(match.team1) }}
              </button>
              <button 
                class="team-button"
                @click="selectWinner(match, match.team2)"
              >
                {{ formatTeamName(match.team2) }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Third Place Match -->
      <div class="round">
        <h2>Third Place Match</h2>
        <div class="matches">
          <div 
            v-for="match in knockoutMatches.filter(m => m.round === 'Match for third place')" 
            :key="match.num"
            class="match-card"
          >
            <div class="match-info">
              <span class="date-time">{{ formatDate(match.date) }} • {{ formatTime(match.time) }}</span>
              <span class="stadium">{{ getStadium(match.ground)?.name || match.ground }}</span>
              <span class="city">{{ getStadium(match.ground)?.city || '' }}</span>
            </div>
            <div class="teams">
              <button 
                class="team-button"
                @click="selectWinner(match, match.team1)"
              >
                {{ formatTeamName(match.team1) }}
              </button>
              <button 
                class="team-button"
                @click="selectWinner(match, match.team2)"
              >
                {{ formatTeamName(match.team2) }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Final -->
      <div class="round final">
        <h2>Final</h2>
        <div class="matches">
          <div 
            v-for="match in knockoutMatches.filter(m => m.round === 'Final')" 
            :key="match.num"
            class="match-card final-card"
          >
            <div class="match-info">
              <span class="date-time">{{ formatDate(match.date) }} • {{ formatTime(match.time) }}</span>
              <span class="stadium">{{ getStadium(match.ground)?.name || match.ground }}</span>
              <span class="city">{{ getStadium(match.ground)?.city || '' }}</span>
            </div>
            <div class="teams">
              <button 
                class="team-button"
                @click="selectWinner(match, match.team1)"
              >
                {{ formatTeamName(match.team1) }}
              </button>
              <button 
                class="team-button"
                @click="selectWinner(match, match.team2)"
              >
                {{ formatTeamName(match.team2) }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.knockouts-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

h1 {
  color: white;
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin: 0 0 40px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.loading {
  color: white;
  font-size: 18px;
}

.bracket-container {
  display: flex;
  gap: 40px;
  width: 100%;
  max-width: 1600px;
  overflow-x: auto;
  padding: 20px;
}

.round {
  flex: 1;
  min-width: 200px;
}

.round h2 {
  color: white;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 20px;
}

.matches {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.match-card {
  background: white;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.match-card.final-card {
  border: 3px solid #ffd700;
  box-shadow: 0 4px 16px rgba(255, 215, 0, 0.3);
}

.match-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
  font-size: 12px;
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
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
}

.team-button:hover {
  background: #e8f4e8;
  border-color: #4caf50;
  color: #2e7d32;
}

.team-button:active {
  transform: scale(0.98);
}

.final .team-button:hover {
  background: #fff8e1;
  border-color: #ffc107;
  color: #f57c00;
}

@media (max-width: 1024px) {
  .bracket-container {
    flex-direction: column;
    align-items: center;
  }
  
  .round {
    width: 100%;
    max-width: 400px;
  }
}
</style>
