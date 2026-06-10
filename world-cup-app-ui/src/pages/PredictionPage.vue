<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import draggable from 'vuedraggable'
import { usePredictionStore } from '../stores/predictionStore'

const router = useRouter()
const predictionStore = usePredictionStore()

// Step management
const currentStep = ref('group-ranking') // 'group-ranking', 'third-place', 'knockout'

const loading = ref(true)

// Get 3rd place teams from each group
const getThirdPlaceTeams = () => {
  const thirdPlaceTeams = []
  for (const [groupName, teams] of Object.entries(predictionStore.groups)) {
    if (teams.length >= 3) {
      thirdPlaceTeams.push({
        team: teams[2],
        groupName: groupName
      })
    }
  }
  return thirdPlaceTeams
}

// Load group data
onMounted(async () => {
  try {
    const response = await fetch('https://world-cup-yzg0.onrender.com/api/groups')
    const data = await response.json()
    // Transform array into object keyed by group name
    const groupedData = {}
    data.forEach(team => {
      const groupName = team.groupName
      if (!groupedData[groupName]) {
        groupedData[groupName] = []
      }
      groupedData[groupName].push(team)
    })
    predictionStore.setGroups(groupedData)
    console.log(predictionStore.groups)
    loading.value = false
  } catch (error) {
    console.error('Failed to load groups:', error)
    loading.value = false
  }
})

// Team crest map (same as GroupsPage)
const teamCrests = {
  'Uruguay': 'https://crests.football-data.org/758.svg',
  'Germany': 'https://crests.football-data.org/759.svg',
  'Spain': 'https://crests.football-data.org/760.svg',
  'Paraguay': 'https://crests.football-data.org/761.svg',
  'Argentina': 'https://crests.football-data.org/762.png',
  'Ghana': 'https://crests.football-data.org/ghana.svg',
  'Brazil': 'https://crests.football-data.org/764.svg',
  'Portugal': 'https://crests.football-data.org/765.svg',
  'Japan': 'https://crests.football-data.org/766.svg',
  'Mexico': 'https://crests.football-data.org/769.svg',
  'England': 'https://crests.football-data.org/770.svg',
  'United States': 'https://crests.football-data.org/usa.svg',
  'South Korea': 'https://crests.football-data.org/772.png',
  'France': 'https://crests.football-data.org/773.svg',
  'South Africa': 'https://crests.football-data.org/9396.svg',
  'Canada': 'https://crests.football-data.org/canada.svg',
  'Qatar': 'https://crests.football-data.org/8030.svg',
  'Switzerland': 'https://crests.football-data.org/788.svg',
  'Morocco': 'https://crests.football-data.org/morocco.svg',
  'Australia': 'https://crests.football-data.org/779.svg',
  'Netherlands': 'https://crests.football-data.org/8601.svg',
  'Sweden': 'https://crests.football-data.org/792.svg',
  'Belgium': 'https://crests.football-data.org/805.svg',
  'Egypt': 'https://crests.football-data.org/825.svg',
  'Iran': 'https://crests.football-data.org/iran.svg',
  'New Zealand': 'https://crests.football-data.org/783.svg',
  'Cape Verde Islands': 'https://crests.football-data.org/cape_verde.svg',
  'Saudi Arabia': 'https://crests.football-data.org/saudi_arabia.svg',
  'Senegal': 'https://crests.football-data.org/senegal.svg',
  'Iraq': 'https://crests.football-data.org/iraq.svg',
  'Norway': 'https://crests.football-data.org/813.svg',
  'Algeria': 'https://crests.football-data.org/algeria.svg',
  'Austria': 'https://crests.football-data.org/816.svg',
  'Jordan': 'https://crests.football-data.org/8049.png',
  'Uzbekistan': 'https://crests.football-data.org/8070.png',
  'Colombia': 'https://crests.football-data.org/818.svg',
  'Congo DR': 'https://crests.football-data.org/congo_dr.svg',
  'Croatia': 'https://crests.football-data.org/799.svg',
  'Panama': 'https://crests.football-data.org/panama.svg',
  'Czechia': 'https://crests.football-data.org/798.svg',
  'Bosnia-Herzegovina': 'https://crests.football-data.org/bosnia.svg',
  'Haiti': 'https://crests.football-data.org/haiti.svg',
  'Scotland': 'https://crests.football-data.org/814.svg',
  'Turkey': 'https://crests.football-data.org/803.svg',
  'Curaçao': 'https://crests.football-data.org/curacao.svg',
  'Ivory Coast': 'https://crests.football-data.org/787.svg',
  'Ecuador': 'https://crests.football-data.org/791.svg',
  'Tunisia': 'https://crests.football-data.org/tunisia.svg'
}

// Format group name
const formatGroupName = (groupName) => {
  if (!groupName || typeof groupName !== 'string') return groupName
  return groupName.replace('_', ' ')
}

// Continue to next step
const continueToThirdPlace = () => {
  currentStep.value = 'third-place'
}

const continueToKnockout = () => {
  if (predictionStore.selectedThirdPlaceTeams.length !== 8) {
    alert('Please select exactly 8 third-place teams')
    return
  }
  currentStep.value = 'knockout'
  router.push('/knockouts')
}
</script>

<template>
  <div class="prediction-page">
    <h1>World Cup 2026 - Predictions</h1>
    
    <div v-if="loading" class="loading">
      Loading groups...
    </div>
    
    <!-- Step 1: Group Ranking -->
    <div v-else-if="currentStep === 'group-ranking'" class="step-container">
      <h2>Step 1: Rank Teams in Each Group</h2>
      <p class="step-description">
        Drag and drop teams to determine their finishing order (1st, 2nd, 3rd, 4th)
      </p>
      
      <div class="groups-grid">
        <div 
          v-for="(teams, groupName) in predictionStore.groups" 
          :key="groupName"
          class="group-card"
        >
          <h3>{{ formatGroupName(groupName) }}</h3>
          <draggable
          v-model="predictionStore.groups[groupName]"
          item-key="teamName"
          :group="{
            name: groupName,
            pull: false,
            put: false
          }"
          animation="200"
          ghost-class="ghost"
          chosen-class="chosen"
          handle=".drag-handle"
          :swapThreshold="0.65"
          class="teams-list"
          tag="div"
        >
          <template #item="{ element, index }">
            <div class="team-item">
              <div class="position-badge"
              :class="{
                first: index === 0,
                second: index === 1,
                third: index === 2
              }">{{ index + 1 }}</div>
              <img
                :src="teamCrests[element.teamName]"
                class="team-crest"
              />
              <span class="team-name">{{ element.teamName }}</span>
              <div class="drag-handle">☰</div>
            </div>
          </template>
        </draggable>
        </div>
      </div>
      
      <button class="continue-button" @click="continueToThirdPlace">
        Continue to 3rd Place Selection →
      </button>
    </div>
    
    <!-- Step 2: Third Place Selection -->
    <div v-else-if="currentStep === 'third-place'" class="step-container">
      <h2>Step 2: Select 8 Third-Place Teams</h2>
      <p class="step-description">
        Select 8 of the 12 third-place teams to advance to the Round of 32
      </p>
      
      <div class="selection-counter">
        Selected: {{ predictionStore.selectedThirdPlaceTeams.length }} / 8
      </div>
      
      <div class="third-place-grid">
        <div 
          v-for="teamData in getThirdPlaceTeams()" 
          :key="teamData.team.teamName"
          :class="['third-place-card', { selected: predictionStore.isSelected(teamData) }]"
          @click="predictionStore.toggleThirdPlaceTeam(teamData)"
        >
          <div class="card-header">
            <span class="group-label">{{ formatGroupName(teamData.groupName) }}</span>
            <div class="checkbox">
              <span v-if="predictionStore.isSelected(teamData)">✓</span>
            </div>
          </div>
          <div class="team-info">
            <img 
              :src="teamCrests[teamData.team.teamName] || ''" 
              :alt="teamData.team.teamName"
              class="team-crest"
              @error="$event.target.style.display = 'none'"
            />
            <span class="team-name">{{ teamData.team.teamName }}</span>
          </div>
        </div>
      </div>
      
      <button 
        class="continue-button" 
        :disabled="predictionStore.selectedThirdPlaceTeams.length !== 8"
        @click="continueToKnockout"
      >
        Continue to Knockout Bracket →
      </button>
    </div>
  </div>
</template>

<style scoped>
.prediction-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
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
  text-align: center;
}

.step-container {
  max-width: 1400px;
  margin: 0 auto;
}

.step-container h2 {
  color: white;
  font-size: 24px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 10px;
}

.step-description {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  text-align: center;
  margin-bottom: 30px;
}

.groups-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.group-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.group-card h3 {
  color: #333;
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 15px 0;
  text-align: center;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.teams-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.team-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: move;
  transition: all 0.25s ease;
}

.team-item:hover {
  background: #e9ecef;
}

.position-badge {
  background: #667eea;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.team-crest {
  width: 24px;
  height: 24px;
  object-fit: contain;
  flex-shrink: 0;
}

.team-name {
  color: #333;
  font-size: 14px;
  font-weight: 500;
  flex: 1;
}

.drag-handle {
  color: #999;
  font-size: 18px;
  cursor: grab;
  user-select: none;
  flex-shrink: 0;
}

.continue-button {
  display: block;
  margin: 0 auto;
  padding: 16px 32px;
  background: white;
  color: #667eea;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.continue-button:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.selection-counter {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  max-width: 300px;
  margin-left: auto;
  margin-right: auto;
}

.third-place-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 30px;
}

.third-place-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 3px solid transparent;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.third-place-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.third-place-card.selected {
  border-color: #667eea;
  background: #f0f4ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.group-label {
  color: #666;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.checkbox {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: white;
  transition: all 0.2s ease;
}

.third-place-card.selected .checkbox {
  background: #667eea;
  border-color: #667eea;
}

.team-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.team-info .team-crest {
  width: 32px;
  height: 32px;
}

.team-info .team-name {
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.continue-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.continue-button:disabled:hover {
  transform: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.ghost {
  opacity: 0.4;
  background: #dbe4ff;
}

.chosen {
  transform: scale(1.03);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}

.position-badge.first {
  background: gold;
}

.position-badge.second {
  background: silver;
}

.position-badge.third {
  background: #cd7f32;
}
.sortable-chosen {
  cursor: grabbing;
}

.sortable-drag {
  opacity: 0.8;
}

.sortable-ghost {
  opacity: 0.3;
}

.sortable-fallback {
  transform: rotate(2deg);
}
</style>
