<script setup>
import { computed, onMounted, ref } from 'vue'
import GroupTable from '../components/GroupTable.vue'
import { getGroups } from '../services/api'

// reactive state
const allGroupStandings = ref([]);
const loading = ref(true);
const error = ref(null);

// Transform group standings data for display
const groups = computed(() => {
  const groupMap = {};
  
  allGroupStandings.value.forEach(standing => {
    const groupName = standing.groupName;
    if (!groupMap[groupName]) {
      groupMap[groupName] = [];
    }
    // Transform standing data to match frontend structure
    groupMap[groupName].push({
      team: {
        ...standing.team,
        crest: standing.team.crestUrl
      },
      played: standing.playedGames,
      wins: standing.wins,
      draws: standing.draws,
      losses: standing.losses,
      goalsFor: standing.goalsFor,
      goalsAgainst: standing.goalsAgainst,
      goalDifference: standing.goalDifference,
      points: standing.points
    });
  });
  
  // Sort groups alphabetically and sort teams by points within each group
  const sortedGroups = {};
  Object.keys(groupMap).sort().forEach(groupName => {
    sortedGroups[groupName] = groupMap[groupName].sort((a, b) => b.points - a.points);
  });
  
  return sortedGroups;
});

// load data from API
onMounted(async () => {
  try {
    loading.value = true;
    const data = await getGroups();
    allGroupStandings.value = data;
  } catch (err) {
    error.value = err.message;
    console.error('Failed to load group standings:', err);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="groups-page">
    <h1>World Cup 2026 - Group Stage Standings</h1>
    <div class="groups-grid">
      <GroupTable 
        v-for="(teams, groupName) in groups" 
        :key="groupName"
        :group-name="groupName"
        :teams="teams"
      />
    </div>
  </div>
</template>

<style scoped>
.groups-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.groups-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
  width: 100%;
  max-width: 1400px;
}

@media (max-width: 768px) {
  h1 {
    font-size: 24px;
  }
  
  .groups-grid {
    grid-template-columns: 1fr;
  }
}
</style>
