<script setup>
import Schedule from '../components/Schedule.vue'
import { ref, computed, onMounted } from 'vue'
import { getMatches, refreshMatches } from '../services/api'

// reactive state
const selectedMatchday = ref(1);
const selectedDate = ref(null);
const allMatches = ref([]);
const loading = ref(true);
const error = ref(null);

// computed
const matches = computed(() => {
  if (!allMatches.value.length) return [];
  return allMatches.value.filter(match => match.matchday === selectedMatchday.value);
});

// Extract unique dates from matches
const uniqueDates = computed(() => {
  if (!allMatches.value.length) return [];
  
  const dates = new Set();
  allMatches.value.forEach(match => {
    const date = new Date(match.utcDate);
    date.setHours(date.getHours() - 5); // Apply -5 hour offset for timezone fix
    const dateKey = date.toLocaleDateString('en-US', {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
    });
    dates.add(dateKey);
  });
  
  // Convert to array and sort chronologically
  return Array.from(dates).sort((a, b) => {
    const dateA = new Date(a);
    const dateB = new Date(b);
    return dateA - dateB;
  });
});

// Filter matches by selected date
const filteredMatches = computed(() => {
  if (!selectedDate.value || !allMatches.value.length) return [];
  
  return allMatches.value.filter(match => {
    const date = new Date(match.utcDate);
    date.setHours(date.getHours() - 5); // Apply -5 hour offset for timezone fix
    const dateKey = date.toLocaleDateString('en-US', {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
    });
    return dateKey === selectedDate.value;
  }).sort((a, b) => new Date(a.utcDate) - new Date(b.utcDate));
});

const refreshMatchesData = async () => {
  await refreshMatches();
  await getMatchesData();
}

const getMatchesData = async () => {
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
}

// load data from API
onMounted(async () => {
  await getMatchesData();
  
  // Auto-select today's date if it exists in the matches
  const today = new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
  });
  
  if (uniqueDates.value.includes(today)) {
    selectedDate.value = today;
  } else if (uniqueDates.value.length > 0) {
    selectedDate.value = uniqueDates.value[0];
  }
});
</script>

<template>
  <div class="schedule-page">
    <div class="page-header">
      <h1>World Cup 2026 - Match Day {{ selectedMatchday }} Schedule</h1>
      <v-btn color="primary" @click="refreshMatchesData">Refresh</v-btn>
    </div>
    
    <!-- Date Selector -->
    <div class="date-selector" v-if="uniqueDates.length > 0">
      <div 
        v-for="date in uniqueDates" 
        :key="date"
        class="date-item"
        :class="{ active: selectedDate === date }"
        @click="selectedDate = date"
      >
        {{ date }}
      </div>
    </div>
    
    <!-- Schedule with filtered matches -->
    <Schedule :matches="selectedDate ? filteredMatches : matches" />
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 1400px;
  gap: 20px;
}

h1 {
  color: white;
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  flex: 1;
}

.date-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  width: 100%;
  max-width: 1400px;
  padding: 16px;
  background: #2a2a2a;
  border-radius: 12px;
}

.date-item {
  padding: 12px 20px;
  background: #3a3a3a;
  color: #b0b0b0;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
  user-select: none;
}

.date-item:hover {
  background: #4a4a4a;
  color: white;
  transform: translateY(-2px);
}

.date-item.active {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  color: white;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(30, 58, 95, 0.4);
}

@media (max-width: 768px) {
  h1 {
    font-size: 24px;
  }
  
  .date-selector {
    padding: 12px;
    gap: 8px;
  }
  
  .date-item {
    padding: 10px 16px;
    font-size: 12px;
  }
}
</style>
