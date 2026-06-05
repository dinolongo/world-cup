<script setup>
import { computed } from 'vue'
import MatchCard from './MatchCard.vue'

const props = defineProps({
  matches: {
    type: Array,
    required: true
  }
})

const groupedMatches = computed(() => {
  const groups = {}
  
  props.matches.forEach(match => {
    const date = new Date(match.utcDate)
    const dateKey = date.toLocaleDateString('en-US', {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    })
    
    if (!groups[dateKey]) {
      groups[dateKey] = []
    }
    groups[dateKey].push(match)
  })
  
  // Sort each group by kick off time
  Object.keys(groups).forEach(dateKey => {
    groups[dateKey].sort((a, b) => {
      return new Date(a.utcDate) - new Date(b.utcDate)
    })
  })
  
  // Sort dates chronologically
  const sortedGroups = {}
  Object.keys(groups).sort((a, b) => {
    const dateA = new Date(groups[a][0].utcDate)
    const dateB = new Date(groups[b][0].utcDate)
    return dateA - dateB
  }).forEach(key => {
    sortedGroups[key] = groups[key]
  })
  
  return sortedGroups
})
</script>

<template>
  <div class="schedule">
    <div v-for="(matches, date) in groupedMatches" :key="date" class="date-group">
      <h2 class="date-header">{{ date }}</h2>
      <div class="matches-grid">
        <MatchCard 
          v-for="match in matches" 
          :key="match.id" 
          :match="match" 
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.schedule {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.date-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.date-header {
  color: white;
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.matches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

@media (max-width: 768px) {
  .matches-grid {
    grid-template-columns: 1fr;
  }
  
  .date-header {
    font-size: 20px;
  }
}
</style>
