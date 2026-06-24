<script setup>
import Schedule from '../components/Schedule.vue'
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getMatches, refreshMatches } from '../services/api'

// reactive state
const selectedMatchday = ref(1);
const selectedDate = ref(null);
const allMatches = ref([]);
const loading = ref(true);
const error = ref(null);
const countdown = ref({ days: 0, hours: 0, minutes: 0, seconds: 0 });
let countdownInterval = null;

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

// Find next upcoming match
const nextMatch = computed(() => {
  if (!allMatches.value.length) return null;
  
  const now = new Date();
  const upcomingMatches = allMatches.value.filter(match => {
    const matchDate = new Date(match.utcDate);
    matchDate.setHours(matchDate.getHours() - 5); // Apply -5 hour offset for timezone fix
    return matchDate > now && match.status !== 'FINISHED';
  });
  
  if (upcomingMatches.length === 0) return null;
  
  return upcomingMatches.sort((a, b) => new Date(a.utcDate) - new Date(b.utcDate))[0];
});

// Update countdown
const updateCountdown = () => {
  if (!nextMatch.value) {
    countdown.value = { days: 0, hours: 0, minutes: 0, seconds: 0 };
    return;
  }
  
  const now = new Date();
  const matchDate = new Date(nextMatch.value.utcDate);
  matchDate.setHours(matchDate.getHours() - 5); // Apply -5 hour offset for timezone fix
  
  const diff = matchDate - now;
  
  if (diff <= 0) {
    countdown.value = { days: 0, hours: 0, minutes: 0, seconds: 0 };
    return;
  }
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((diff % (1000 * 60)) / 1000);
  
  countdown.value = { days, hours, minutes, seconds };
};

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
  
  // Start countdown timer
  updateCountdown();
  countdownInterval = setInterval(updateCountdown, 1000);
});

onUnmounted(() => {
  if (countdownInterval) {
    clearInterval(countdownInterval);
  }
});
</script>

<template>
  <div class="schedule-page">
    <div class="page-header">
      <h1>World Cup 2026 Schedule</h1>
      <v-btn color="primary" @click="refreshMatchesData">Refresh</v-btn>
    </div>
    
    <div class="countdown-container" v-if="nextMatch">
      <h2 class="countdown-title">Next Match: {{ nextMatch.homeTeam.name }} vs {{ nextMatch.awayTeam.name }}</h2>
      <div class="countdown-timer">
        <div class="countdown-item">
          <span class="countdown-value">{{ countdown.days }}</span>
          <span class="countdown-label">Days</span>
        </div>
        <span class="countdown-separator">:</span>
        <div class="countdown-item">
          <span class="countdown-value">{{ countdown.hours }}</span>
          <span class="countdown-label">Hours</span>
        </div>
        <span class="countdown-separator">:</span>
        <div class="countdown-item">
          <span class="countdown-value">{{ countdown.minutes }}</span>
          <span class="countdown-label">Minutes</span>
        </div>
        <span class="countdown-separator">:</span>
        <div class="countdown-item">
          <span class="countdown-value">{{ countdown.seconds }}</span>
          <span class="countdown-label">Seconds</span>
        </div>
      </div>
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

.countdown-container {
  width: 100%;
  max-width: 1400px;
  padding: 24px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(30, 58, 95, 0.4);
}

.countdown-title {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 20px 0;
  text-align: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.countdown-timer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.countdown-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 80px;
}

.countdown-value {
  font-size: 36px;
  font-weight: 700;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  line-height: 1;
}

.countdown-label {
  font-size: 12px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.8);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.countdown-separator {
  font-size: 36px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 8px;
}

@media (max-width: 768px) {
  h1 {
    font-size: 24px;
  }
  
  .countdown-container {
    padding: 16px;
  }
  
  .countdown-title {
    font-size: 16px;
  }
  
  .countdown-timer {
    gap: 8px;
  }
  
  .countdown-item {
    min-width: 60px;
  }
  
  .countdown-value {
    font-size: 24px;
  }
  
  .countdown-label {
    font-size: 10px;
  }
  
  .countdown-separator {
    font-size: 24px;
    margin: 0 4px;
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
