<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLeaderboard } from '../composables/useLeaderboard'

const router = useRouter()
const { 
  filteredEntries, 
  loading, 
  scoringStatus, 
  searchQuery, 
  getTeamName, 
  getTeamCrest,
  initialize 
} = useLeaderboard()

const totalCount = computed(() => filteredEntries.value.length)

const headers = [
  // { title: 'Rank', key: 'rank', align: 'start', sortable: false, width: '80px' },
  { title: 'Name', key: 'displayName', align: 'start', sortable: false },
  { title: 'Champion', key: 'predictedChampionId', align: 'start', sortable: false },
  { title: 'Runner-Up', key: 'predictedRunnerUpId', align: 'start', sortable: false },
  { title: '3rd Place', key: 'predictedThirdPlaceId', align: 'start', sortable: false },
  { title: 'Score', key: 'totalScore', align: 'center', sortable: false, width: '150px' },
  { title: 'Actions', key: 'actions', align: 'center', sortable: false, width: '120px' }
]

const scoringStatusText = computed(() => {
  switch (scoringStatus.value) {
    case 'not-started':
      return 'Scoring will begin after the group stage concludes'
    case 'in-progress':
      return 'Scoring live — updates as matches complete'
    case 'final':
      return 'Final standings'
    default:
      return ''
  }
})

const scoringStatusColor = computed(() => {
  switch (scoringStatus.value) {
    case 'not-started':
      return 'info'
    case 'in-progress':
      return 'warning'
    case 'final':
      return 'success'
    default:
      return 'info'
  }
})

const getRankIcon = (rank) => {
  if (rank === 1) return 'mdi-medal'
  if (rank === 2) return 'mdi-medal-outline'
  if (rank === 3) return 'mdi-medal-outline'
  return null
}

const getRankColor = (rank) => {
  if (rank === 1) return 'gold'
  if (rank === 2) return 'silver'
  if (rank === 3) return '#cd7f32'
  return 'grey'
}

const getRowClass = (rank) => {
  if (rank === 1) return 'rank-gold'
  if (rank === 2) return 'rank-silver'
  if (rank === 3) return 'rank-bronze'
  return ''
}

const viewBracket = (bracketId) => {
  router.push(`/bracket/${bracketId}`)
}

const submitBracket = () => {
  router.push('/prediction')
}

onMounted(() => {
  initialize()
})
</script>

<template>
  <div class="leaderboard-page">
    <v-container class="pa-4">
      <!-- Header Section -->
      <div class="header-section mb-6">
        <h1 class="page-title">2026 World Cup Predictions Leaderboard</h1>
        <p class="subtitle">{{ totalCount }} brackets submitted</p>
        
        <div class="header-actions">
          <v-text-field
            v-model="searchQuery"
            placeholder="Search by name..."
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            class="search-field"
            bg-color="white"
            rounded="pill"
          />
          
          <v-btn
            color="primary"
            size="large"
            rounded="pill"
            elevation="3"
            @click="submitBracket"
          >
            Submit Your Bracket
            <v-icon end>mdi-trophy</v-icon>
          </v-btn>
        </div>
      </div>

      <!-- Scoring Status Banner -->
      <v-alert
        v-if="scoringStatusText"
        :color="scoringStatusColor"
        variant="tonal"
        class="mb-4"
        density="comfortable"
      >
        <v-icon start>mdi-information</v-icon>
        {{ scoringStatusText }}
      </v-alert>

      <!-- Loading Skeleton -->
      <div v-if="loading" class="loading-container">
        <v-skeleton-loader
          type="table-row@10"
          class="skeleton-loader"
        />
      </div>

      <!-- Empty State -->
      <v-card
        v-else-if="filteredEntries.length === 0"
        class="empty-state"
        elevation="2"
        rounded="xl"
      >
        <v-card-text class="pa-8 text-center">
          <v-icon size="64" color="grey-lighten-1" class="mb-4">
            mdi-trophy-outline
          </v-icon>
          <h3 class="text-h5 mb-2">No brackets yet</h3>
          <p class="text-body-1 text-grey-600 mb-4">
            Be the first to submit your World Cup predictions!
          </p>
          <v-btn
            color="primary"
            size="large"
            rounded="pill"
            @click="submitBracket"
          >
            Submit Your Bracket
          </v-btn>
        </v-card-text>
      </v-card>

      <!-- Leaderboard Table -->
      <v-card
        v-else
        class="leaderboard-card"
        elevation="2"
        rounded="xl"
      >
        <v-data-table
          :headers="headers"
          :items="filteredEntries"
          :loading="loading"
          class="leaderboard-table"
        >
          <!-- <template v-slot:item.rank="{ item, index }">
            <div class="rank-cell">
              <v-icon
                v-if="getRankIcon(index + 1)"
                :color="getRankColor(index + 1)"
                size="20"
                class="rank-icon"
              >
                {{ getRankIcon(index + 1) }}
              </v-icon>
              <span v-else class="rank-number">{{ index + 1 }}</span>
            </div>
          </template> -->

          <template v-slot:item.predictedChampionId="{ item }">
            <div v-if="item.predictedChampionId" class="team-cell">
              <img
                :src="getTeamCrest(item.predictedChampionId)"
                :alt="getTeamName(item.predictedChampionId)"
                class="team-crest"
                @error="$event.target.style.display = 'none'"
              />
              <span class="team-name">{{ getTeamName(item.predictedChampionId) }}</span>
            </div>
            <span v-else class="unknown-team">?</span>
          </template>

          <template v-slot:item.predictedRunnerUpId="{ item }">
            <div v-if="item.predictedRunnerUpId" class="team-cell">
              <img
                :src="getTeamCrest(item.predictedRunnerUpId)"
                :alt="getTeamName(item.predictedRunnerUpId)"
                class="team-crest"
                @error="$event.target.style.display = 'none'"
              />
              <span class="team-name">{{ getTeamName(item.predictedRunnerUpId) }}</span>
            </div>
            <span v-else class="unknown-team">?</span>
          </template>

          <template v-slot:item.predictedThirdPlaceId="{ item }">
            <div v-if="item.predictedThirdPlaceId" class="team-cell">
              <img
                :src="getTeamCrest(item.predictedThirdPlaceId)"
                :alt="getTeamName(item.predictedThirdPlaceId)"
                class="team-crest"
                @error="$event.target.style.display = 'none'"
              />
              <span class="team-name">{{ getTeamName(item.predictedThirdPlaceId) }}</span>
            </div>
            <span v-else class="unknown-team">?</span>
          </template>

          <template v-slot:item.totalScore="{ item }">
            <div v-if="item.totalScore !== null" class="score-cell">
              <div class="score-value">{{ item.totalScore }}</div>
              <v-progress-linear
                :model-value="(item.totalScore / 108) * 100"
                color="primary"
                height="6"
                rounded
                class="score-progress"
              />
              <div class="score-breakdown">
                <span v-if="item.groupStageScore !== null">Group: {{ item.groupStageScore }}pts</span>
                <span v-if="item.knockoutScore !== null">Knockouts: {{ item.knockoutScore }}pts</span>
              </div>
            </div>
            <span v-else class="no-score">—</span>
          </template>

          <template v-slot:item.actions="{ item }">
            <v-btn
              size="small"
              color="primary"
              variant="text"
              @click="viewBracket(item.bracketId)"
            >
              View Bracket
              <v-icon end size="small">mdi-arrow-right</v-icon>
            </v-btn>
          </template>
        </v-data-table>
      </v-card>
    </v-container>
  </div>
</template>

<style scoped>
.leaderboard-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.header-section {
  text-align: center;
}

.page-title {
  color: white;
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 18px;
  margin-bottom: 24px;
}

.header-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  max-width: 800px;
  margin: 0 auto;
}

.search-field {
  max-width: 400px;
  flex: 1;
}

.loading-container {
  background: white;
  border-radius: 16px;
  padding: 20px;
}

.skeleton-loader {
  background: transparent;
}

.empty-state {
  background: white;
  text-align: center;
}

.leaderboard-card {
  background: white;
  overflow: hidden;
}

.leaderboard-table {
  background: transparent;
}

.rank-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.rank-icon {
  font-size: 20px;
}

.rank-number {
  font-weight: 600;
  color: #666;
  font-size: 16px;
}

.team-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.team-crest {
  width: 24px;
  height: 24px;
  object-fit: contain;
  flex-shrink: 0;
}

.team-name {
  font-size: 14px;
  font-weight: 500;
}

.unknown-team {
  color: #999;
  font-size: 18px;
  font-weight: 600;
}

.score-cell {
  text-align: center;
}

.score-value {
  font-weight: 700;
  font-size: 18px;
  color: #667eea;
  margin-bottom: 4px;
}

.score-progress {
  margin: 4px 0;
}

.score-breakdown {
  font-size: 11px;
  color: #666;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.no-score {
  color: #999;
  font-size: 18px;
  font-weight: 600;
}

/* Top 3 row styling */
.rank-gold {
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.15) 0%, rgba(255, 215, 0, 0.05) 100%);
}

.rank-silver {
  background: linear-gradient(90deg, rgba(192, 192, 192, 0.15) 0%, rgba(192, 192, 192, 0.05) 100%);
}

.rank-bronze {
  background: linear-gradient(90deg, rgba(205, 127, 50, 0.15) 0%, rgba(205, 127, 50, 0.05) 100%);
}

/* Mobile responsive */
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }
  
  .subtitle {
    font-size: 16px;
  }
  
  .header-actions {
    flex-direction: column;
  }
  
  .search-field {
    max-width: 100%;
    width: 100%;
  }
  
  .leaderboard-table :deep(.v-data-table__wrapper) {
    overflow-x: auto;
  }
}
</style>
