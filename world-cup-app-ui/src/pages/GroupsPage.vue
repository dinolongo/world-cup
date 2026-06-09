<script setup>
import { computed, onMounted, ref } from 'vue'
import GroupTable from '../components/GroupTable.vue'
import { getGroups } from '../services/api'

// Team crest mapping
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
  'Canada': 'https://crests.football-data.org/768.svg',
  'Qatar': 'https://crests.football-data.org/qatar.svg',
  'Switzerland': 'https://crests.football-data.org/767.svg',
  'Morocco': 'https://crests.football-data.org/morocco.svg',
  'Australia': 'https://crests.football-data.org/australia.svg',
  'Netherlands': 'https://crests.football-data.org/774.svg',
  'Sweden': 'https://crests.football-data.org/775.svg',
  'Belgium': 'https://crests.football-data.org/776.svg',
  'Egypt': 'https://crests.football-data.org/egypt.svg',
  'Iran': 'https://crests.football-data.org/iran.svg',
  'New Zealand': 'https://crests.football-data.org/new-zealand.svg',
  'Cape Verde': 'https://crests.football-data.org/cape-verde.svg',
  'Saudi Arabia': 'https://crests.football-data.org/saudi-arabia.svg',
  'Senegal': 'https://crests.football-data.org/senegal.svg',
  'Iraq': 'https://crests.football-data.org/iraq.svg',
  'Norway': 'https://crests.football-data.org/norway.svg',
  'Algeria': 'https://crests.football-data.org/algeria.svg',
  'Austria': 'https://crests.football-data.org/austria.svg',
  'Jordan': 'https://crests.football-data.org/jordan.svg',
  'Uzbekistan': 'https://crests.football-data.org/uzbekistan.svg',
  'Colombia': 'https://crests.football-data.org/colombia.svg',
  'DR Congo': 'https://crests.football-data.org/dr-congo.svg',
  'Croatia': 'https://crests.football-data.org/croatia.svg',
  'Panama': 'https://crests.football-data.org/panama.svg',
  'Czechia': 'https://crests.football-data.org/czechia.svg',
  'Bosnia and Herzegovina': 'https://crests.football-data.org/bosnia.svg',
  'Haiti': 'https://crests.football-data.org/haiti.svg',
  'Scotland': 'https://crests.football-data.org/scotland.svg',
  'Turkey': 'https://crests.football-data.org/turkey.svg',
  'Curaçao': 'https://crests.football-data.org/curacao.svg',
  'Ivory Coast': 'https://crests.football-data.org/ivory-coast.svg',
  'Ecuador': 'https://crests.football-data.org/ecuador.svg',
  'Tunisia': 'https://crests.football-data.org/tunisia.svg'
};

const getTeamCrest = (teamName) => {
  return teamCrests[teamName] || '';
};

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
        id: standing.teamId,
        name: standing.teamName,
        crest: getTeamCrest(standing.teamName)
      },
      played: standing.played,
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
