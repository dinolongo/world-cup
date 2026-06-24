<script setup>
import { ref, computed, onMounted } from 'vue'
import KnockoutMatchCard from './KnockoutMatchCard.vue'
import { useKnockoutPredictions } from '../composables/useKnockoutPredictions'
import { BRACKET_LAYOUT, TOTAL_KNOCKOUT_MATCHES, teamCrests } from '../util/constants'
import { usePredictionStore } from '../stores/predictionStore'

const props = defineProps({
  knockoutMatches: {
    type: Array,
    required: true
  }
})

const predictionStore = usePredictionStore()

const {
  finalWinner,
  finalRunnerUp,
  thirdPlaceWinner,
  getTeamCrest,
  getTeamName,
  getTeamId,
  selectWinner,
  allPredictionsMade,
  buildPayload
} = useKnockoutPredictions(props.knockoutMatches, predictionStore)

const currentMatchIndex = ref(0)
const showCompletionScreen = ref(false)
const stadiumMap = new Map()
const predictedMatchesCount = ref(0)

onMounted(() => {
  import('../data/stadium-data.json').then(module => {
    const stadiumData = module.default
    stadiumData.stadiums.forEach(s => stadiumMap.set(s.name, s))
  })
})

const getStadium = (groundName) => stadiumMap.get(groundName) ?? null

const matchOrder = computed(() => {
  return [
    ...BRACKET_LAYOUT.left.ro32,
    ...BRACKET_LAYOUT.right.ro32,
    ...BRACKET_LAYOUT.left.ro16,
    ...BRACKET_LAYOUT.right.ro16,
    ...BRACKET_LAYOUT.left.qf,
    ...BRACKET_LAYOUT.right.qf,
    ...BRACKET_LAYOUT.left.sf,
    ...BRACKET_LAYOUT.right.sf,
    ...BRACKET_LAYOUT.thirdPlace,
    ...BRACKET_LAYOUT.final
  ]
})

const currentMatch = computed(() => {
  if (currentMatchIndex.value >= matchOrder.value.length) return null
  const matchNum = matchOrder.value[currentMatchIndex.value]
  return props.knockoutMatches.find(m => m.num === matchNum)
})

const currentRoundName = computed(() => {
  if (!currentMatch.value) return ''
  return currentMatch.value.round
})

const currentMatchNumber = computed(() => currentMatchIndex.value + 1)

const totalMatches = computed(() => matchOrder.value.length)

const progressPercentage = computed(() => {
  return (currentMatchNumber.value / totalMatches.value) * 100
})


const actualProgressPercentage = computed(() => {
  return (predictedMatchesCount.value / TOTAL_KNOCKOUT_MATCHES) * 100
})

const handleWinnerSelection = (teamId) => {
  console.log('winner selected', teamId)
  if (!currentMatch.value) return
  
  selectWinner(currentMatch.value, teamId)
  predictedMatchesCount.value++
  
  if (predictedMatchesCount.value >= TOTAL_KNOCKOUT_MATCHES) {
    setTimeout(() => {
      showCompletionScreen.value = true
    }, 500)
  } else {
    setTimeout(() => {
      currentMatchIndex.value++
    }, 400)
  }
}

const getTeamCrestFromName = (teamName) => teamCrests[teamName] ?? null

const resetWizard = () => {
  currentMatchIndex.value = 0
  showCompletionScreen.value = false
}
</script>

<template>
  <div class="mobile-bracket-wizard">
    <div v-if="!showCompletionScreen" class="wizard-content">
      <div class="wizard-header">
        <h2 class="round-title">{{ currentRoundName }}</h2>
        <div class="match-counter">
          Match {{ currentMatchNumber }} of {{ totalMatches }}
        </div>
        <v-progress-linear
          :model-value="actualProgressPercentage"
          color="primary"
          height="8"
          rounded
          class="progress-bar"
        >
          <template v-slot:default="{ value }">
            <strong>{{ Math.round(value) }}%</strong>
          </template>
        </v-progress-linear>
      </div>

      <div v-if="currentMatch" class="match-card-container">
        <KnockoutMatchCard
          :match="currentMatch"
          :match-num="currentMatch.num"
          :team1-name="getTeamName(currentMatch.team1, currentMatch.num, currentMatch.team2)"
          :team2-name="getTeamName(currentMatch.team2, currentMatch.num, currentMatch.team1)"
          :team1-id="getTeamId(currentMatch.team1, currentMatch.num, currentMatch.team2)"
          :team2-id="getTeamId(currentMatch.team2, currentMatch.num, currentMatch.team1)"
          :stadium="getStadium(currentMatch.ground)"
          @select-winner="handleWinnerSelection"
          class="mobile-match-card"
        />
      </div>
    </div>

    <div v-else class="completion-screen">
      <div class="completion-header">
        <h1 class="completion-title">🏆 Your World Cup Champion</h1>
      </div>

      <div class="champion-card">
        <img 
          v-if="getTeamCrestFromName(finalWinner)" 
          :src="getTeamCrestFromName(finalWinner)" 
          class="champion-crest"
          alt=""
        />
        <h2 class="champion-name">{{ finalWinner }}</h2>
      </div>

      <div class="runner-up-card">
        <h3 class="runner-up-title">Runner-Up</h3>
        <div class="runner-up-content">
          <img 
            v-if="getTeamCrestFromName(finalRunnerUp)" 
            :src="getTeamCrestFromName(finalRunnerUp)" 
            class="runner-up-crest"
            alt=""
          />
          <span class="runner-up-name">{{ finalRunnerUp }}</span>
        </div>
      </div>

      <div class="third-place-card">
        <h3 class="third-place-title">Third Place</h3>
        <div class="third-place-content">
          <img 
            v-if="getTeamCrestFromName(thirdPlaceWinner)" 
            :src="getTeamCrestFromName(thirdPlaceWinner)" 
            class="third-place-crest"
            alt=""
          />
          <span class="third-place-name">{{ thirdPlaceWinner }}</span>
        </div>
      </div>

      <div class="completion-actions">
        <v-btn
          color="primary"
          size="large"
          rounded="pill"
          elevation="3"
          @click="$emit('save-bracket')"
          class="save-button"
        >
          <v-icon start>mdi-content-save</v-icon>
          Save My Bracket
        </v-btn>
        
        <v-btn
          variant="outlined"
          size="large"
          rounded="pill"
          @click="resetWizard"
          class="reset-button"
        >
          <v-icon start>mdi-refresh</v-icon>
          Start Over
        </v-btn>
      </div>
    </div>
  </div>
</template>

<style scoped>
.mobile-bracket-wizard {
  min-height: 100vh;
  background: #1a1a1a;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.wizard-content {
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.wizard-header {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.round-title {
  color: white;
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.match-counter {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  font-weight: 500;
}

.progress-bar {
  margin-top: 8px;
}

.match-card-container {
  display: flex;
  justify-content: center;
  width: 100%;
}

.mobile-match-card {
  width: 100%;
  max-width: 350px;
}

.mobile-match-card :deep(.knockout-match-card) {
  min-width: unset;
  max-width: unset;
  width: 100%;
  padding: 16px;
}

.mobile-match-card :deep(.match-info) {
  font-size: 14px;
  gap: 4px;
  margin-bottom: 12px;
}

.mobile-match-card :deep(.match-number) {
  font-size: 14px;
}

.mobile-match-card :deep(.date-time) {
  font-size: 13px;
}

.mobile-match-card :deep(.stadium) {
  font-size: 12px;
}

.mobile-match-card :deep(.city) {
  font-size: 12px;
}

.mobile-match-card :deep(.teams) {
  gap: 12px;
}

.mobile-match-card :deep(.team-button) {
  padding: 12px 16px;
  font-size: 16px;
  font-weight: 600;
}

.mobile-match-card :deep(.team-flag) {
  width: 32px;
  height: 32px;
}

.completion-screen {
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  text-align: center;
}

.completion-header {
  margin-bottom: 16px;
}

.completion-title {
  color: #ffd700;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.champion-card {
  background: linear-gradient(135deg, #ffd700 0%, #ffecb3 100%);
  border-radius: 16px;
  padding: 32px 24px;
  box-shadow: 0 8px 24px rgba(255, 215, 0, 0.4);
  animation: celebrate 0.6s ease-out;
}

.champion-crest {
  width: 80px;
  height: 80px;
  object-fit: contain;
  margin-bottom: 16px;
}

.champion-name {
  color: #333;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.runner-up-card {
  background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(192, 192, 192, 0.4);
  animation: celebrate 0.6s ease-out 0.1s backwards;
}

.runner-up-title {
  color: #333;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
}

.runner-up-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.runner-up-crest {
  width: 48px;
  height: 48px;
  object-fit: contain;
}

.runner-up-name {
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.third-place-card {
  background: linear-gradient(135deg, #cd7f32 0%, #e6a86c 100%);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(205, 127, 50, 0.4);
  animation: celebrate 0.6s ease-out 0.2s backwards;
}

.third-place-title {
  color: #333;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
}

.third-place-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.third-place-crest {
  width: 48px;
  height: 48px;
  object-fit: contain;
}

.third-place-name {
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.completion-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.save-button {
  width: 100%;
}

.reset-button {
  width: 100%;
  border-color: rgba(255, 255, 255, 0.3);
  color: rgba(255, 255, 255, 0.8);
}

@keyframes celebrate {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
