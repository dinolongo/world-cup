<script setup>
import Schedule from '../components/Schedule.vue'
import { ref, computed, onMounted } from 'vue'
import { getMatches } from '../services/api'

// reactive state
const selectedMatchday = ref(1);
const allMatches = ref([]);
const loading = ref(true);
const error = ref(null);

// computed
const matches = computed(() => {
  if (!allMatches.value.length) return [];
  return allMatches.value.filter(match => match.matchday === selectedMatchday.value);
});

// load data from API
onMounted(async () => {
  try {
    loading.value = true;
    const data = await getMatches();
    // Transform backend DTO to match frontend structure
    allMatches.value = data.map(match => ({
      id: match.id,
      utcDate: match.utcDate,
      status: match.status,
      matchday: selectedMatchday.value, // Will need to be added to backend or derived
      stage: match.stage || 'GROUP_STAGE',
      group: match.group,
      lastUpdated: match.lastUpdated,
      homeTeam: {
        id: match.homeTeam.id,
        name: match.homeTeam.name,
        shortName: match.homeTeam.shortName,
        tla: match.homeTeam.tla,
        crest: match.homeTeam.crestUrl
      },
      awayTeam: {
        id: match.awayTeam.id,
        name: match.awayTeam.name,
        shortName: match.awayTeam.shortName,
        tla: match.awayTeam.tla,
        crest: match.awayTeam.crestUrl
      },
      score: {
        fullTime: {
          home: match.homeScore,
          away: match.awayScore
        }
      }
    }));
  } catch (err) {
    error.value = err.message;
    console.error('Failed to load matches:', err);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="schedule-page">
    <h1>World Cup 2026 - Match Day {{ selectedMatchday }} Schedule</h1>
    <Schedule :matches="matches" />
  </div>
</template>

<style scoped>
.schedule-page {
  min-height: 100vh;
  background: #1a1a1a;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
}

h1 {
  color: white;
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

@media (max-width: 768px) {
  h1 {
    font-size: 24px;
  }
}
</style>
