<script setup>
import { ref, onMounted, computed, onUnmounted, nextTick } from 'vue'
import matchStadiumData from '../data/match-stadium.json'
import stadiumData from '../data/stadium-data.json'
import { usePredictionStore } from '../stores/predictionStore'
import KnockoutMatchCard from '../components/KnockoutMatchCard.vue'

const predictionStore = usePredictionStore()

// Reactive state
const knockoutMatches = ref([])
const stadiums = ref([])
const loading = ref(true)
const cardRefs = ref({})
const leftBracketEl = ref(null)
const leftConnectorPaths = ref([])
const rightBracketEl = ref(null)
const rightConnectorPaths = ref([])
const leftSvgViewBox = ref('0 0 0 0')
const rightSvgViewBox = ref('0 0 0 0')

// Get third-place seeding lookup
const thirdPlaceSeeding = computed(() => predictionStore.getThirdPlaceSeeding())

// Load data
onMounted(async () => {
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

  await nextTick()
  recalculatePaths()

  window.addEventListener('resize', recalculatePaths)
})

onUnmounted(() => {
  window.removeEventListener('resize', recalculatePaths)
})

const setCardRef = (el, matchNum) => {
  if (el) cardRefs.value[matchNum] = el.$el  // $el gives the root DOM node
}

// Split matches into left and right brackets
const leftBracketMatches = computed(() => {
  return knockoutMatches.value.filter(match => 
    match.num >= 73 && match.num <= 80 || // Ro32 left
    match.num >= 89 && match.num <= 92 || // Ro16 left
    match.num >= 97 && match.num <= 98 || // QF left
    match.num === 101 // SF left
  )
})

const rightBracketMatches = computed(() => {
  return knockoutMatches.value.filter(match => 
    match.num >= 81 && match.num <= 88 || // Ro32 right
    match.num >= 93 && match.num <= 96 || // Ro16 right
    match.num >= 99 && match.num <= 100 || // QF right
    match.num === 102 // SF right
  )
})

const finalMatch = computed(() => {
  return knockoutMatches.value.find(match => match.round === 'Final')
})

const thirdPlaceMatch = computed(() => {
  return knockoutMatches.value.find(match => match.round === 'Match for third place')
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

// Get team name from code, using predictions if available
const getTeamName = (teamCode, matchNum, opponentCode) => {
  if (!teamCode) return 'TBD'
  
  // Handle winner references (W74, W77, etc.)
  if (teamCode.startsWith('W')) {
    const sourceMatchNum = teamCode.substring(1)
    // Check if we have a prediction for this match
    if (knockoutPredictions.value[sourceMatchNum]) {
      return knockoutPredictions.value[sourceMatchNum]
    }
    return `Winner of Match ${sourceMatchNum}`
  }
  
  // Handle loser references (L101, L102, etc.)
  if (teamCode.startsWith('L')) {
    const matchNum = teamCode.substring(1)
    return `Loser of Match ${matchNum}`
  }
  
  // Handle 3rd place team codes with multiple groups (3A/B/C/D/F, etc.)
  if (teamCode.includes('/')) {
    // Use seeding lookup to determine which third-place team plays here
    if (thirdPlaceSeeding.value && opponentCode) {
      // The opponent code (e.g., "1E") tells us which position this match is for
      // Look up which third-place team code is assigned to this position
      const thirdPlaceCode = thirdPlaceSeeding.value[opponentCode]
      if (thirdPlaceCode) {
        const teamName = predictionStore.getTeamFromCode(thirdPlaceCode)
        if (teamName) return teamName
      }
    }
    return 'Best 3rd Place Team'
  }
  
  // Try to get predicted team from store
  const predictedTeam = predictionStore.getTeamFromCode(teamCode)
  if (predictedTeam) return predictedTeam
  
  // Fallback to formatted placeholder
  return formatTeamNamePlaceholder(teamCode)
}

// Format team name placeholder when no prediction available
const formatTeamNamePlaceholder = (teamCode) => {
  // Handle group position codes (1A, 2B, 3C, etc.)
  const groupMatch = teamCode.match(/^(\d)([A-L])$/)
  if (groupMatch) {
    const position = groupMatch[1]
    const group = groupMatch[2]
    const positionText = position === '1' ? 'Winner' : position === '2' ? 'Runner-up' : '3rd Place'
    return `${positionText} of Group ${group}`
  }
  
  return teamCode
}


// Store for knockout predictions
const knockoutPredictions = ref({})

const getRelativeRect = (el, container) => {
  const elRect = el.getBoundingClientRect()
  const containerRect = container.getBoundingClientRect()
  return {
    top: elRect.top - containerRect.top,
    bottom: elRect.bottom - containerRect.top,
    left: elRect.left - containerRect.left,
    right: elRect.right - containerRect.left,
    midY: (elRect.top + elRect.bottom) / 2 - containerRect.top
  }
}


const leftBracketPairs = [
  { sources: [73, 74], target: 89 },
  { sources: [75, 76], target: 90 },
  { sources: [77, 78], target: 91 },
  { sources: [79, 80], target: 92 },
  { sources: [89, 90], target: 97 },
  { sources: [91, 92], target: 98 },
  { sources: [97, 98], target: 101 },
]

const rightBracketPairs = [
  { sources: [81, 82], target: 93 },
  { sources: [83, 84], target: 94 },
  { sources: [85, 86], target: 95 },
  { sources: [87, 88], target: 96 },
  { sources: [93, 94], target: 99 },
  { sources: [95, 96], target: 100 },
  { sources: [99, 100], target: 102 },
]

const calculatePaths = (pairs, containerEl, direction = 'left') => {
  if (!containerEl) return []
  const paths = []

  for (const pair of pairs) {
    const el1 = cardRefs.value[pair.sources[0]]
    const el2 = cardRefs.value[pair.sources[1]]
    const elTarget = cardRefs.value[pair.target]
    if (!el1 || !el2 || !elTarget) continue

    const r1 = getRelativeRect(el1, containerEl)
    const r2 = getRelativeRect(el2, containerEl)
    const rT = getRelativeRect(elTarget, containerEl)

    // For left bracket: lines exit right edge of source, enter left edge of target
    // For right bracket: lines exit left edge of source, enter right edge of target
    const x1 = direction === 'left' ? r1.right : r1.left
    const x2 = direction === 'left' ? r2.right : r2.left
    const xT = direction === 'left' ? rT.left : rT.right

    const y1 = r1.midY
    const y2 = r2.midY
    const yT = rT.midY
    const midY = (y1 + y2) / 2
    const midX = (x1 + xT) / 2

    // From card1: go horizontal to midX, drop to midY
    // From card2: go horizontal to midX, rise to midY  
    // Then from midY go horizontal into target card
    const d = `
      M ${x1} ${y1} H ${midX} V ${midY}
      M ${x2} ${y2} H ${midX} V ${midY}
      M ${midX} ${midY} H ${xT}
    `.trim()

    paths.push({ id: `${pair.sources[0]}-${pair.sources[1]}`, d })
  }

  return paths
}

const recalculatePaths = () => {
  if (leftBracketEl.value) {
    const r = leftBracketEl.value.getBoundingClientRect()
    leftSvgViewBox.value = `0 0 ${r.width} ${r.height}`
  }
  if (rightBracketEl.value) {
    const r = rightBracketEl.value.getBoundingClientRect()
    rightSvgViewBox.value = `0 0 ${r.width} ${r.height}`
  }
  leftConnectorPaths.value = calculatePaths(leftBracketPairs, leftBracketEl.value, 'left')
  rightConnectorPaths.value = calculatePaths(rightBracketPairs, rightBracketEl.value, 'right')
}

// Handle team selection with auto-advance
const selectWinner = (match, team) => {
  // Store the winner for this match
  knockoutPredictions.value[match.num] = team
  
  // Load bracket advancement data
  import('../data/bracket-advancement.json').then(data => {
    const advancement = data.default || data
    
    // Find which matches this winner advances to
    const advancesTo = advancement.forward[match.num]
    if (advancesTo) {
      // Update the team in the advancing match
      const advancingMatch = knockoutMatches.value.find(m => m.num === advancesTo.matchNum)
      if (advancingMatch) {
        if (advancesTo.position === 'team1') {
          advancingMatch.team1 = `W${match.num}`
        } else if (advancesTo.position === 'team2') {
          advancingMatch.team2 = `W${match.num}`
        }
      }
    }
  })
}
</script>

<template>
  <div class="knockouts-page">
    <h1>World Cup 2026 - Knockout Stage</h1>
    
    <div v-if="loading" class="loading">
      Loading bracket...
    </div>
    
    <div v-else class="bracket-container">
      <!-- Left Bracket -->
      <div class="bracket-side left-bracket" ref="leftBracketEl">
        <svg class="connector-svg" ref="leftSvg" viewBox="leftSvgViewBox">
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
              v-for="match in leftBracketMatches.filter(m => m.round === 'Round of 32')"
              :key="match.num"
              :match="match"
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
              v-for="match in leftBracketMatches.filter(m => m.round === 'Round of 16')"
              :key="match.num"
              :match="match"
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
              v-for="match in leftBracketMatches.filter(m => m.round === 'Quarter-final')"
              :key="match.num"
              :match="match"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
          </div>
        </div>

        <!-- Semi Final -->
        <div class="round-section">
          <h3>Semi Final</h3>
          <div class="matches">
            <KnockoutMatchCard
              v-for="match in leftBracketMatches.filter(m => m.round === 'Semi-final')"
              :key="match.num"
              :match="match"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
          </div>
        </div>
      </div>

      <!-- Center Section (Final & Third Place) -->
      <div class="center-section">
        <div v-if="thirdPlaceMatch" class="third-place-section">
          <h2>Third Place Match</h2>
          <KnockoutMatchCard
            :match="thirdPlaceMatch"
            :team1-name="getTeamName(thirdPlaceMatch.team1, thirdPlaceMatch.num, thirdPlaceMatch.team2)"
            :team2-name="getTeamName(thirdPlaceMatch.team2, thirdPlaceMatch.num, thirdPlaceMatch.team1)"
            :stadium="getStadium(thirdPlaceMatch.ground)"
            @select-winner="selectWinner(thirdPlaceMatch, $event)"
            :ref="el => setCardRef(el, thirdPlaceMatch.num)"
          />
        </div>

        <div v-if="finalMatch" class="final-section">
          <h2 class="final-title">Final</h2>
          <KnockoutMatchCard
            :match="finalMatch"
            :team1-name="getTeamName(finalMatch.team1, finalMatch.num, finalMatch.team2)"
            :team2-name="getTeamName(finalMatch.team2, finalMatch.num, finalMatch.team1)"
            :stadium="getStadium(finalMatch.ground)"
            @select-winner="selectWinner(finalMatch, $event)"
            class="final-card"
            :ref="el => setCardRef(el, finalMatch.num)"
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
              v-for="match in rightBracketMatches.filter(m => m.round === 'Round of 32')"
              :key="match.num"
              :match="match"
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
              v-for="match in rightBracketMatches.filter(m => m.round === 'Round of 16')"
              :key="match.num"
              :match="match"
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
              v-for="match in rightBracketMatches.filter(m => m.round === 'Quarter-final')"
              :key="match.num"
              :match="match"
              :team1-name="getTeamName(match.team1, match.num, match.team2)"
              :team2-name="getTeamName(match.team2, match.num, match.team1)"
              :stadium="getStadium(match.ground)"
              @select-winner="selectWinner(match, $event)"
              :ref="el => setCardRef(el, match.num)"
            />
          </div>
        </div>

        <!-- Semi Final -->
        <div class="round-section">
          <h3>Semi Final</h3>
          <div class="matches">
            <KnockoutMatchCard
              v-for="match in rightBracketMatches.filter(m => m.round === 'Semi-final')"
              :key="match.num"
              :match="match"
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

.center-section {
  flex: 0 0 230px;
  display: flex;
  flex-direction: column;
  gap: 32px;
  align-items: center;
  justify-content: center;
  min-height: 100%;
}

.third-place-section h2,
.final-section h2 {
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
  
  .center-section {
    flex: none;
    width: 100%;
    max-width: 400px;
  }
}

.connector-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: visible;
}
</style>
