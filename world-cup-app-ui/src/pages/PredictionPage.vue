<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import draggable from 'vuedraggable'
import { usePredictionStore } from '../stores/predictionStore'
import { teamCrests } from '../util/constants'

const router = useRouter()
const predictionStore = usePredictionStore()

const currentStep = ref('group-ranking')
const loading = ref(true)

onMounted(async () => {
  try {
    const response = await fetch('https://world-cup-yzg0.onrender.com/api/groups')
    const data = await response.json()
    const groupedData = {}
    data.forEach(team => {
      if (!groupedData[team.groupName]) groupedData[team.groupName] = []
      groupedData[team.groupName].push(team)
    })
    predictionStore.setGroups(groupedData)
  } catch (error) {
    console.error('Failed to load groups:', error)
  } finally {
    loading.value = false
  }
})

const formatGroupName = (groupName) => groupName?.replace('_', ' ') ?? groupName

const thirdPlaceTeams = computed(() =>
  Object.entries(predictionStore.groups)
    .filter(([, teams]) => teams.length >= 3)
    .map(([groupName, teams]) => ({ team: teams[2], groupName }))
)

const continueToThirdPlace = () => {
  currentStep.value = 'third-place'
}

const continueToKnockout = () => {
  if (predictionStore.selectedThirdPlaceTeams.length !== 8) {
    alert('Please select exactly 8 third-place teams')
    return
  }
  router.push('/knockouts')
}
</script>

<template>
  <div class="prediction-page">
    <v-container>
      <h1 class="page-title">World Cup 2026 - Predictions</h1>
      
      <div v-if="loading" class="loading">
        <v-progress-circular indeterminate color="white" size="50"></v-progress-circular>
        <p>Loading groups...</p>
      </div>
      
      <!-- Step 1: Group Ranking -->
      <div v-else-if="currentStep === 'group-ranking'" class="step-container">
        <h2 class="step-title">Step 1: Rank Teams in Each Group</h2>
        <p class="step-description">
          Drag and drop teams to determine their finishing order (1st, 2nd, 3rd, 4th)
        </p>
        
        <v-row>
          <v-col 
            v-for="(teams, groupName) in predictionStore.groups" 
            :key="groupName"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <v-card class="group-card" elevation="3" rounded="xl">
              <v-card-title class="group-card-title">{{ formatGroupName(groupName) }}</v-card-title>
              <v-card-text class="pa-0">
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
                    <div class="team-item-wrapper">
                      <div class="team-item-content">
                        <div 
                          :class="['position-badge', {
                            'first': index === 0,
                            'second': index === 1,
                            'third': index === 2
                          }]"
                        >
                          {{ index + 1 }}
                        </div>
                        <img 
                          :src="teamCrests[element.teamName]" 
                          class="team-crest"
                          width="28"
                          height="28"
                        />
                        <span class="team-name">{{ element.teamName }}</span>
                      </div>
                      <div class="drag-handle">☰</div>
                    </div>
                  </template>
                </draggable>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
        
        <div class="button-container">
          <v-btn 
            size="large" 
            color="primary" 
            rounded="pill"
            elevation="3"
            @click="continueToThirdPlace"
          >
            Continue to 3rd Place Selection
            <v-icon end>mdi-arrow-right</v-icon>
          </v-btn>
        </div>
      </div>
      
      <!-- Step 2: Third Place Selection -->
      <div v-else-if="currentStep === 'third-place'" class="step-container">
        <h2 class="step-title">Step 2: Select 8 Third-Place Teams</h2>
        <p class="step-description">
          Select 8 of the 12 third-place teams to advance to the Round of 32
        </p>
        
        <div class="selection-counter">
          <v-chip size="large" color="primary" class="counter-chip">
            Selected: {{ predictionStore.selectedThirdPlaceTeams.length }} / 8
          </v-chip>
        </div>
        
        <v-row>
          <v-col 
            v-for="teamData in thirdPlaceTeams" 
            :key="teamData.team.teamName"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <v-card 
              :class="['third-place-card', { selected: predictionStore.isThirdPlaceTeamSelected(teamData) }]"
              elevation="2"
              rounded="xl"
              @click="predictionStore.toggleThirdPlaceTeam(teamData)"
              hover
            >
              <v-card-text class="pa-3">
                <div class="card-header">
                  <v-chip size="small" color="grey-lighten-1" class="group-label">
                    {{ formatGroupName(teamData.groupName) }}
                  </v-chip>
                  <v-checkbox 
                    :model-value="predictionStore.isThirdPlaceTeamSelected(teamData)"
                    color="primary"
                    hide-details
                    readonly
                  ></v-checkbox>
                </div>
                <div class="team-info">
                  <img 
                    :src="teamCrests[teamData.team.teamName] || ''" 
                    :alt="teamData.team.teamName"
                    class="team-crest mr-2 "
                    width="36"
                    height="36"
                    @error="$event.target.style.display = 'none'"
                  />
                  <span class="team-name">{{ teamData.team.teamName }}</span>
                </div>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
        
        <div class="button-container">
          <v-btn 
            size="large" 
            color="primary" 
            rounded="pill"
            elevation="3"
            :disabled="predictionStore.selectedThirdPlaceTeams.length !== 8"
            @click="continueToKnockout"
          >
            Continue to Knockout Bracket
            <v-icon end>mdi-arrow-right</v-icon>
          </v-btn>
        </div>
      </div>
    </v-container>
  </div>
</template>

<style scoped>
.prediction-page {
  min-height: 100vh;
  background: #1a1a1a;
  padding: 20px;
}

.page-title {
  color: white;
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin: 0 0 32px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  color: white;
  font-size: 18px;
  text-align: center;
  padding: 60px 20px;
}

.step-container {
  max-width: 1400px;
  margin: 0 auto;
}

.step-title {
  color: white;
  font-size: 24px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 8px;
}

.step-description {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  text-align: center;
  margin-bottom: 32px;
}

.group-card {
  background: #2d2d2d;
  border: 1px solid #3d3d3d;
}

.group-card-title {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  padding: 12px 16px;
  text-align: center;
}

.teams-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px;
}

.team-item-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #3d3d3d;
  border-radius: 8px;
  margin: 0;
  cursor: move;
  transition: all 0.25s ease;
}

.team-item-wrapper:hover {
  background: #4d4d4d;
}

.team-item-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.position-badge {
  background: #1e3a5f;
  color: white;
  font-size: 12px;
  font-weight: 600;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.team-crest {
  object-fit: contain;
  flex-shrink: 0;
}

.position-badge.first {
  background: #ffd700;
  color: #1a1a1a;
}

.position-badge.second {
  background: #c0c0c0;
  color: #1a1a1a;
}

.position-badge.third {
  background: #cd7f32;
  color: white;
}

.team-name {
  color: #e0e0e0;
  font-size: 14px;
  font-weight: 500;
}

.drag-handle {
  color: #888;
  font-size: 20px;
  cursor: grab;
  user-select: none;
}

.button-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.selection-counter {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.counter-chip {
  font-size: 16px;
  font-weight: 600;
}

.third-place-card {
  background: #2d2d2d;
  border: 2px solid #3d3d3d;
  cursor: pointer;
  transition: all 0.2s ease;
}

.third-place-card:hover {
  transform: translateY(-2px);
  border-color: #4d4d4d;
}

.third-place-card.selected {
  border-color: #1e3a5f;
  background: #1e3a5f;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.group-label {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}

.team-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.team-info .team-name {
  color: #e0e0e0;
  font-size: 14px;
  font-weight: 600;
}

.ghost {
  opacity: 0.4;
  background: #1e3a5f;
}

.chosen {
  transform: scale(1.02);
  box-shadow: 0 8px 20px rgba(0,0,0,0.3);
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
