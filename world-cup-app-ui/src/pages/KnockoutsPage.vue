<script setup>
import { ref, onMounted, computed, onUnmounted, nextTick } from 'vue'
import matchStadiumData from '../data/match-stadium.json'
import stadiumData from '../data/stadium-data.json'
import { usePredictionStore } from '../stores/predictionStore'
import KnockoutMatchCard from '../components/KnockoutMatchCard.vue'
import { useBracketConnectors } from '../composables/useBracketConnectors'
import { useKnockoutPredictions } from '../composables/useKnockoutPredictions'
import { KNOCKOUT_ROUNDS, TOTAL_KNOCKOUT_MATCHES } from '../util/constants'

const predictionStore = usePredictionStore()

// Reactive state
const knockoutMatches = ref([])
const stadiumMap = ref({})
const loading = ref(true)
const leftBracketEl = ref(null)
const rightBracketEl = ref(null)
const bracketContainerEl = ref(null)

// Use composables
const {
  leftConnectorPaths,
  rightConnectorPaths,
  centerConnectorPaths,
  leftSvgViewBox,
  rightSvgViewBox,
  centerSvgViewBox,
  setCardRef,
  recalculatePaths
} = useBracketConnectors(leftBracketEl, rightBracketEl, bracketContainerEl)

const {
  knockoutPredictions,
  knockoutLosers,
  finalWinner,
  finalRunnerUp,
  thirdPlaceWinner,
  getWinnerCrest,
  getTeamName,
  selectWinner
} = useKnockoutPredictions(knockoutMatches, predictionStore)

// Load data
onMounted(async () => {
  knockoutMatches.value = matchStadiumData.matches.filter(match => 
    KNOCKOUT_ROUNDS.has(match.round)
  )
  stadiumMap.value = new Map(stadiumData.stadiums.map(s => [s.name, s]))
  loading.value = false

  await nextTick()
  recalculatePaths()

  window.addEventListener('resize', recalculatePaths)
})

onUnmounted(() => {
  window.removeEventListener('resize', recalculatePaths)
})

const leftRoundOf32MatchNumbers = [
  74, 77, 73, 75, 83, 84, 81, 82
]
const rightRoundOf32MatchNumbers = [
  76, 78, 79, 80, 86, 88,85, 87 
]
const leftRoundOf16MatchNumbers = [
  89, 90, 93, 94
]
const rightRoundOf16MatchNumbers = [
  91, 92, 95, 96
]
const leftQuarterFinalMatchNumbers = [97, 98]
const rightQuarterFinalMatchNumbers = [99, 100]

// Sorted matches by bracket slot for proper connector alignment
const matchesForNumbers = (nums) =>
  computed(() => nums.map(num => knockoutMatches.value.find(m => m.num === num)).filter(Boolean))

const leftRo32Matches = matchesForNumbers(leftRoundOf32MatchNumbers)
const leftRo16Matches = matchesForNumbers(leftRoundOf16MatchNumbers)
const leftQFMatches = matchesForNumbers(leftQuarterFinalMatchNumbers)
const rightRo32Matches = matchesForNumbers(rightRoundOf32MatchNumbers)
const rightRo16Matches = matchesForNumbers(rightRoundOf16MatchNumbers)
const rightQFMatches = matchesForNumbers(rightQuarterFinalMatchNumbers)


const finalMatch = computed(() => {
  return knockoutMatches.value.find(match => match.round === 'Final')
})

const thirdPlaceMatch = computed(() => {
  return knockoutMatches.value.find(match => match.round === 'Match for third place')
})

const sf101Match = computed(() => {
  return knockoutMatches.value.find(match => match.num === 101)
})

const sf102Match = computed(() => {
  return knockoutMatches.value.find(match => match.num === 102)
})

// Get stadium details by name
const getStadium = (groundName) => stadiumMap.value.get(groundName) ?? null

const allPredictionsMade = computed(() =>
  Object.keys(knockoutPredictions.value).length === TOTAL_KNOCKOUT_MATCHES
)

const saveBracket = () => {
  console.log('Saving bracket...')
}

</script>

<template>
  <div class="knockouts-page">
    <v-row>
      <v-col cols="10">
        <h1>World Cup 2026 - Knockout Stage</h1>
      </v-col>
      <v-col cols="2">
        <v-btn
          :disabled="!allPredictionsMade"
          @click="saveBracket"
        >
          Save My Bracket
        </v-btn>
      </v-col>
    </v-row>
    
    <div v-if="loading" class="loading">
      Loading bracket...
    </div>
    
    <div v-else class="bracket-container" ref="bracketContainerEl">
      <!-- Center SVG Layer for all center connectors -->
      <svg class="center-connector-svg" ref="centerSvg" :viewBox="centerSvgViewBox">
        <path 
          v-for="path in centerConnectorPaths" 
          :key="path.id"
          :d="path.d"
          fill="none"
          stroke="rgba(255,255,255,0.5)"
          stroke-width="2"
        />
      </svg>

      <!-- Left Bracket -->
      <div class="bracket-side left-bracket" ref="leftBracketEl">
        <svg class="connector-svg" ref="leftSvg" :viewBox="leftSvgViewBox">
          <path 
            v-for="path in leftConnectorPaths" 
            :key="path.id"
            :d="path.d"
            fill="none"
            stroke="rgba(255,255,255,0.5)"
            stroke-width="2"
          />
        </svg>
        <!-- Round of 32 -->
        <div class="round-section">
          <h3>Round of 32</h3>
          <div class="matches">
            <KnockoutMatchCard
              v-for="match in leftRo32Matches"
              :key="match.num"
              :match="match"
              :match-num="match.num"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
                :ref="el => setCardRef(el, match.num)"

            />
          </div>
        </div>

        <!-- Round of 16 -->
        <div class="round-section">
          <h3>Round of 16</h3>
          <div class="matches">
            <KnockoutMatchCard
              v-for="match in leftRo16Matches"
              :key="match.num"
              :match="match"
              :match-num="match.num"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
          </div>
        </div>

        <!-- Quarter Finals -->
        <div class="round-section">
          <h3>Quarter Finals</h3>
          <div class="matches">
            <KnockoutMatchCard
              v-for="match in leftQFMatches"
              :key="match.num"
              :match="match"
              :match-num="match.num"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
          </div>
        </div>
      </div>

      <!-- Center Diamond -->
      <div class="center-diamond">
        <!-- Final -->
        <div class="diamond-row final-row">
           <div v-if="finalWinner" class="winner-message">
            <img 
              v-if="getWinnerCrest(finalWinner)" 
              :src="getWinnerCrest(finalWinner)" 
              class="winner-flag"
              alt=""
            />
            <span class="winner-text">{{ finalWinner }} wins the 2026 World Cup!</span>
          </div>
          <div v-if="finalRunnerUp" class="silver-message">
            <img 
              v-if="getWinnerCrest(finalRunnerUp)" 
              :src="getWinnerCrest(finalRunnerUp)" 
              class="silver-flag"
              alt=""
            />
            <span class="silver-text">{{ finalRunnerUp }} is the runner-up</span>
          </div>
          <div v-if="thirdPlaceWinner" class="bronze-message" >
            <img 
              v-if="getWinnerCrest(thirdPlaceWinner)" 
              :src="getWinnerCrest(thirdPlaceWinner)" 
              class="bronze-flag"
              alt=""
            />
            <span class="bronze-text">{{ thirdPlaceWinner }} finishes 3rd</span>
          </div>
          <h2 class="final-title">Final</h2>
          <!-- Winner Message -->
         
          <KnockoutMatchCard
            v-if="finalMatch"
            :match="finalMatch"
            :match-num="finalMatch.num"
            :team1-name="getTeamName(finalMatch.team1, finalMatch.num, finalMatch.team2)"
            :team2-name="getTeamName(finalMatch.team2, finalMatch.num, finalMatch.team1)"
            :stadium="getStadium(finalMatch.ground)"
            @select-winner="selectWinner(finalMatch, $event)"
            class="final-card"
            :ref="el => setCardRef(el, finalMatch.num)"
          />
        </div>

        <!-- Semi Finals -->
        <div class="diamond-row semi-finals-row">
          <div class="semi-final-left">
            <h3>Semi Final</h3>
            <KnockoutMatchCard
              v-if="sf101Match"
              :match="sf101Match"
              :match-num="sf101Match.num"
              :team1-name="getTeamName(sf101Match.team1, sf101Match.num, sf101Match.team2)"
              :team2-name="getTeamName(sf101Match.team2, sf101Match.num, sf101Match.team1)"
              :stadium="getStadium(sf101Match.ground)"
              @select-winner="selectWinner(sf101Match, $event)"
              :ref="el => setCardRef(el, sf101Match.num)"
            />
          </div>
          <div class="semi-final-right">
            <h3>Semi Final</h3>
            <KnockoutMatchCard
              v-if="sf102Match"
              :match="sf102Match"
              :match-num="sf102Match.num"
              :team1-name="getTeamName(sf102Match.team1, sf102Match.num, sf102Match.team2)"
              :team2-name="getTeamName(sf102Match.team2, sf102Match.num, sf102Match.team1)"
              :stadium="getStadium(sf102Match.ground)"
              @select-winner="selectWinner(sf102Match, $event)"
              :ref="el => setCardRef(el, sf102Match.num)"
            />
          </div>
        </div>

        <!-- Third Place -->
        <div class="diamond-row third-place-row">
          <h2>Third Place Match</h2>
          <KnockoutMatchCard
            v-if="thirdPlaceMatch"
            :match="thirdPlaceMatch"
            :match-num="thirdPlaceMatch.num"
            :team1-name="getTeamName(thirdPlaceMatch.team1, thirdPlaceMatch.num, thirdPlaceMatch.team2)"
            :team2-name="getTeamName(thirdPlaceMatch.team2, thirdPlaceMatch.num, thirdPlaceMatch.team1)"
            :stadium="getStadium(thirdPlaceMatch.ground)"
            @select-winner="selectWinner(thirdPlaceMatch, $event)"
            :ref="el => setCardRef(el, thirdPlaceMatch.num)"
          />
         
        </div>
      </div>

      <!-- Right Bracket -->
      <div class="bracket-side right-bracket" ref="rightBracketEl">
        <svg class="connector-svg" ref="rightSvg" :viewBox="rightSvgViewBox">
           <path 
            v-for="path in rightConnectorPaths" 
            :key="path.id"
            :d="path.d"
            fill="none"
            stroke="rgba(255,255,255,0.5)"
            stroke-width="2"
          />
        </svg>
        <!-- Round of 32 -->
        <div class="round-section">
          <h3>Round of 32</h3>
          <div class="matches">
            <KnockoutMatchCard
              v-for="match in rightRo32Matches"
              :key="match.num"
              :match="match"
              :match-num="match.num"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
          </div>
        </div>

        <!-- Round of 16 -->
        <div class="round-section">
          <h3>Round of 16</h3>
          <div class="matches">
            <KnockoutMatchCard
              v-for="match in rightRo16Matches"
              :key="match.num"
              :match="match"
              :match-num="match.num"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
          </div>
        </div>

        <!-- Quarter Finals -->
        <div class="round-section">
          <h3>Quarter Finals</h3>
          <div class="matches">
            <KnockoutMatchCard
              v-for="match in rightQFMatches"
              :key="match.num"
              :match="match"
              :match-num="match.num"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
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
  position: relative;
  display: flex;
  gap: 40px;
  width: 100%;
  max-width: 1800px;
  overflow-x: auto;
  padding: 20px;
  align-items: stretch;
}

.bracket-side {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: stretch; 
  gap: 16px;
  flex: 1;
  min-width: 0;
}

.bracket-side h2 {
  color: white;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 24px;
}

.round-section {
  flex: 0 0 132px;
  display: flex;
  flex-direction: column;
}

.round-section h3 {
  color: white;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 12px;
  flex-shrink: 0;
}


.matches {
  gap: 8px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;  /* distributes cards evenly in available height */
  flex: 1;                         /* fills the full column height */
}

.center-diamond {
  flex: 0 0 200px;
  display: flex;
  flex-direction: column;
  gap: 32px;
  align-items: center;
  justify-content: center;
}

.center-connector-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: visible;
  z-index: 5;
}

.diamond-row {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.semi-finals-row {
  display: flex;
  flex-direction: row;
  gap: 80px;
  justify-content: center;
  align-items: center;
}

.semi-final-left,
.semi-final-right {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.diamond-row h2,
.diamond-row h3 {
  color: white;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 16px;
}

.final-title {
  font-size: 24px;
  color: #ffd700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.final-card :deep(.knockout-match-card) {
  border: 3px solid #ffd700;
  box-shadow: 0 4px 16px rgba(255, 215, 0, 0.3);
}

.final-card :deep(.team-button:hover) {
  background: #fff8e1;
  border-color: #ffc107;
  color: #f57c00;
}

.winner-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #ffd700 0%, #ffecb3 100%);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(255, 215, 0, 0.4);
  animation: celebrate 0.5s ease-out;
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

.winner-flag {
  width: 48px;
  height: 48px;
  object-fit: contain;
  flex-shrink: 0;
}

.winner-text {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.silver-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(192, 192, 192, 0.4);
  animation: celebrate 0.5s ease-out;
  margin-bottom: 20px;
}

.silver-flag {
  width: 48px;
  height: 48px;
  object-fit: contain;
  flex-shrink: 0;
}

.silver-text {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.bronze-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #cd7f32 0%, #e6a86c 100%);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(205, 127, 50, 0.4);
  animation: celebrate 0.5s ease-out;
  margin-bottom: 20px;
}

.bronze-flag {
  width: 48px;
  height: 48px;
  object-fit: contain;
  flex-shrink: 0;
}

.bronze-text {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.connector-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: visible;
  z-index: 1;
}

.right-bracket {
  flex-direction: row-reverse;
}

@media (max-width: 1200px) {
  .bracket-container {
    flex-direction: column;
    align-items: center;
  }
  
  .bracket-side {
    flex-direction: column;
    align-items: center;
  }
}
</style>
