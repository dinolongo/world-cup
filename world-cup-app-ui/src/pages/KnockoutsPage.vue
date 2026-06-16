<script setup>
import { ref, onMounted, computed, onUnmounted, nextTick } from 'vue'
import { useDisplay } from 'vuetify'
import matchStadiumData from '../data/match-stadium.json'
import stadiumData from '../data/stadium-data.json'
import { usePredictionStore } from '../stores/predictionStore'
import KnockoutMatchCard from '../components/KnockoutMatchCard.vue'
import MobileBracketWizard from '../components/MobileBracketWizard.vue'
import { useBracketConnectors } from '../composables/useBracketConnectors'
import { useKnockoutPredictions } from '../composables/useKnockoutPredictions'
import { KNOCKOUT_ROUNDS, BRACKET_LAYOUT } from '../util/constants'
import { checkDisplayName, savePrediction } from '../services/api'

const predictionStore = usePredictionStore()
const { smAndDown } = useDisplay()

// Reactive state
const knockoutMatches = ref([])
const stadiumMap = new Map(stadiumData.stadiums.map(s => [s.name, s]))
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
  finalWinner,
  finalRunnerUp,
  thirdPlaceWinner,
  getTeamCrest,
  getTeamName,
  getTeamId,
  selectWinner,
  finalMatch,
  thirdPlaceMatch,
  sf101Match,
  sf102Match,
  allPredictionsMade,
  buildPayload
} = useKnockoutPredictions(knockoutMatches, predictionStore)

onMounted(async () => {
  knockoutMatches.value = matchStadiumData.matches.filter(match => 
    KNOCKOUT_ROUNDS.has(match.round)
  )
  loading.value = false

  await nextTick()
  recalculatePaths()

  window.addEventListener('resize', recalculatePaths)
})

onUnmounted(() => {
  window.removeEventListener('resize', recalculatePaths)
})

const matchesForNumbers = (nums) =>
  computed(() => nums.map(num => knockoutMatches.value.find(m => m.num === num)).filter(Boolean))

const leftRo32Matches  = matchesForNumbers(BRACKET_LAYOUT.left.ro32)
const leftRo16Matches  = matchesForNumbers(BRACKET_LAYOUT.left.ro16)
const leftQFMatches    = matchesForNumbers(BRACKET_LAYOUT.left.qf)
const rightRo32Matches = matchesForNumbers(BRACKET_LAYOUT.right.ro32)
const rightRo16Matches = matchesForNumbers(BRACKET_LAYOUT.right.ro16)
const rightQFMatches   = matchesForNumbers(BRACKET_LAYOUT.right.qf)

const getStadium = (groundName) => stadiumMap.get(groundName) ?? null

// Dialog state
const saveDialog = ref(false)
const displayName = ref('')
const nameValidationLoading = ref(false)
const nameExists = ref(false)
const nameError = ref('')

const saveBracket = () => {
  saveDialog.value = true
  displayName.value = ''
  nameExists.value = false
  nameError.value = ''
}

const validateDisplayName = async () => {
  if (displayName.value.length === 0 || displayName.value.length > 50) {
    nameError.value = 'Display name must be 1-50 characters'
    return
  }
  
  nameValidationLoading.value = true
  nameExists.value = false
  nameError.value = ''
  
  try {
    const data = await checkDisplayName(displayName.value)
    if (!data.available) {
      nameExists.value = true
      nameError.value = 'Please try another name'
    }
  } catch (error) {
    nameError.value = 'Error validating name'
  } finally {
    nameValidationLoading.value = false
  }
}

const confirmSave = async () => {
  if (nameExists.value || nameError.value || !displayName.value) {
    return
  }
  
  try {
    const payload = buildPayload()
    console.log(payload)
    const data = await savePrediction(
      displayName.value,
      JSON.stringify(payload.groupStagePredictions),
      JSON.stringify(payload.knockoutPredictions)
    )

    console.log('Bracket saved successfully:', data)
    saveDialog.value = false
    alert(`Bracket saved successfully! Your bracket ID is: ${data.bracketId}`)
  } catch (error) {
    nameError.value = 'Error saving bracket'
  }
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
          v-if="allPredictionsMade && !smAndDown"
          color="primary"
          size="large"
          rounded="pill"
          elevation="3"
          @click="saveBracket"
        >
          <v-icon start>mdi-content-save</v-icon>
          Save My Bracket
        </v-btn>
      </v-col>
    </v-row>
    
    <div v-if="loading" class="loading">
      Loading bracket...
    </div>
    
    <!-- Mobile Bracket Wizard -->
    <MobileBracketWizard
      v-else-if="smAndDown"
      :knockout-matches="knockoutMatches"
      @save-bracket="saveBracket"
    />
    
    <!-- Desktop Bracket -->
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
              :team1-id="getTeamId(match.team1, match.num, match.team2)"
              :team2-id="getTeamId(match.team2, match.num, match.team1)"
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
              :team1-id="getTeamId(match.team1, match.num, match.team2)"
              :team2-id="getTeamId(match.team2, match.num, match.team1)"
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
              :team1-id="getTeamId(match.team1, match.num, match.team2)"
              :team2-id="getTeamId(match.team2, match.num, match.team1)"
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
          <div class="results-area">
           <div v-if="finalWinner" class="winner-message">
            <img 
              v-if="getTeamCrest(finalWinner)" 
              :src="getTeamCrest(finalWinner)" 
              class="winner-flag"
              alt=""
            />
            <span class="winner-text">{{ finalWinner }} wins the 2026 World Cup!</span>
          </div>
          <div v-if="finalRunnerUp" class="silver-message">
            <img 
              v-if="getTeamCrest(finalRunnerUp)" 
              :src="getTeamCrest(finalRunnerUp)" 
              class="silver-flag"
              alt=""
            />
            <span class="silver-text">{{ finalRunnerUp }} is the runner-up</span>
          </div>
          <div v-if="thirdPlaceWinner" class="bronze-message" >
            <img 
              v-if="getTeamCrest(thirdPlaceWinner)" 
              :src="getTeamCrest(thirdPlaceWinner)" 
              class="bronze-flag"
              alt=""
            />
            <span class="bronze-text">{{ thirdPlaceWinner }} finishes 3rd</span>
          </div>
        </div>
        <h2 class="final-title">Final</h2>
        <!-- Winner Message -->
         
          <KnockoutMatchCard
            v-if="finalMatch"
            :match="finalMatch"
            :match-num="finalMatch.num"
            :team1-name="getTeamName(finalMatch.team1, finalMatch.num, finalMatch.team2)"
            :team2-name="getTeamName(finalMatch.team2, finalMatch.num, finalMatch.team1)"
            :team1-id="getTeamId(finalMatch.team1, finalMatch.num, finalMatch.team2)"
            :team2-id="getTeamId(finalMatch.team2, finalMatch.num, finalMatch.team1)"
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
              :team1-id="getTeamId(sf101Match.team1, sf101Match.num, sf101Match.team2)"
              :team2-id="getTeamId(sf101Match.team2, sf101Match.num, sf101Match.team1)"
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
              :team1-id="getTeamId(sf102Match.team1, sf102Match.num, sf102Match.team2)"
              :team2-id="getTeamId(sf102Match.team2, sf102Match.num, sf102Match.team1)"
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
            :team1-id="getTeamId(thirdPlaceMatch.team1, thirdPlaceMatch.num, thirdPlaceMatch.team2)"
            :team2-id="getTeamId(thirdPlaceMatch.team2, thirdPlaceMatch.num, thirdPlaceMatch.team1)"
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
              :team1-id="getTeamId(match.team1, match.num, match.team2)"
              :team2-id="getTeamId(match.team2, match.num, match.team1)"
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
              :team1-id="getTeamId(match.team1, match.num, match.team2)"
              :team2-id="getTeamId(match.team2, match.num, match.team1)"
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
              :team1-id="getTeamId(match.team1, match.num, match.team2)"
              :team2-id="getTeamId(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Save Bracket Dialog -->
    <v-dialog v-model="saveDialog" max-width="500" persistent>
      <v-card class="save-dialog-card">
        <v-card-title class="dialog-title">
          <v-icon start class="mr-2">mdi-trophy</v-icon>
          Save Your Bracket
        </v-card-title>
        <v-card-text class="pa-4">
          <p class="dialog-description">
            Enter a display name to save your World Cup 2026 bracket predictions
          </p>
          <v-text-field
            v-model="displayName"
            label="Display Name"
            placeholder="Enter your name"
            :counter="50"
            :error-messages="nameError"
            :loading="nameValidationLoading"
            @blur="validateDisplayName"
            @keyup.enter="validateDisplayName"
            variant="outlined"
            color="primary"
            class="mt-4"
          >
            <template v-slot:append-inner v-if="nameExists">
              <v-icon color="error">mdi-alert</v-icon>
            </template>
            <template v-slot:append-inner v-else-if="displayName && !nameError && !nameValidationLoading">
              <v-icon color="success">mdi-check</v-icon>
            </template>
          </v-text-field>
          <v-alert
            v-if="nameExists"
            type="error"
            density="compact"
            class="mt-2"
          >
            <v-icon start>mdi-alert</v-icon>
            Please try another name
          </v-alert>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer></v-spacer>
          <v-btn
            variant="text"
            @click="saveDialog = false"
          >
            Cancel
          </v-btn>
          <v-btn
            color="primary"
            variant="elevated"
            :disabled="nameExists || !!nameError || !displayName || nameValidationLoading"
            @click="confirmSave"
          >
            Save Bracket
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<style scoped>
.knockouts-page {
  min-height: 100vh;
  background: #1a1a1a;
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
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
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
  z-index: 2;
}

.center-connector-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: visible;
  z-index: 0;
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
.final-row {
  position: relative;
}

.results-area {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  width: 600px;
  margin-bottom: 24px;
}

.results-overlay {
  position: absolute;
  top: -250px;
  left: 50%;
  transform: translateX(-50%);
  width: 500px;
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

.save-dialog-card {
  background: #2d2d2d;
  border: 1px solid #3d3d3d;
}

.dialog-title {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.dialog-description {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin-bottom: 16px;
}
</style>
