<script setup>
import { computed } from 'vue'

const props = defineProps({
  match: {
    type: Object,
    required: true
  }
})

const formattedDate = computed(() => {
  const date = new Date(props.match.utcDate)
  date.setHours(date.getHours() - 5) // Add 5 hour offset for timezone fix
  return date.toLocaleDateString('en-US', {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
})

const formattedTime = computed(() => {
  const date = new Date(props.match.utcDate)
  date.setHours(date.getHours() - 5) // Add 5 hour offset for timezone fix
  return date.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    timeZone: 'America/Chicago',
    timeZoneName: 'short'
  })
})

const matchStatus = computed(() => {
  if (props.match.status === 'TIMED') return 'Upcoming'
  if (props.match.status === 'LIVE') return 'Live'
  if (props.match.status === 'FINISHED') return 'Finished'
  return props.match.status
})
</script>

<template>
  <div class="match-card">
    <div class="match-header">
      <span class="group">{{ match.group }}</span>
      <span class="status">{{ matchStatus }}</span>
    </div>
    
    <div class="match-content">
      <div class="team home">
        <img :src="match.homeTeam.crest" :alt="match.homeTeam.name" class="team-crest">
        <div class="team-info">
          <span class="team-name">{{ match.homeTeam.name }}</span>
          <span class="team-tla">{{ match.homeTeam.tla }}</span>
        </div>
      </div>
      
      <div class="score-section">
        <div class="score">
          <span class="score-home">{{ match.score.fullTime.home ?? '-' }}</span>
          <span class="score-divider">:</span>
          <span class="score-away">{{ match.score.fullTime.away ?? '-' }}</span>
        </div>
        <div class="match-time">{{ formattedTime }}</div>
      </div>
      
      <div class="team away">
        <img :src="match.awayTeam.crest" :alt="match.awayTeam.name" class="team-crest">
        <div class="team-info">
          <span class="team-name">{{ match.awayTeam.name }}</span>
          <span class="team-tla">{{ match.awayTeam.tla }}</span>
        </div>
      </div>
    </div>
    
    <div class="match-footer">
      <span class="match-date">{{ formattedDate }}</span>
      <span class="venue">{{ match.stage }}</span>
    </div>
  </div>
</template>

<style scoped>
.match-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 100%;
}

.match-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  color: white;
}

.group {
  font-weight: 600;
  font-size: 14px;
}

.status {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
}

.match-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 16px;
  gap: 12px;
}

.team {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.team.away {
  flex-direction: row-reverse;
  text-align: right;
}

.team-crest {
  width: 48px;
  height: 48px;
  object-fit: contain;
}

.team-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.team-name {
  font-weight: 600;
  font-size: 14px;
  color: #1a1a1a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px;
}

.team-tla {
  font-size: 12px;
  color: #666;
}

.score-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  min-width: 100px;
  flex-shrink: 0;
}

.score {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 32px;
  font-weight: 700;
  color: #1e3a5f;
}

.score-divider {
  color: #999;
}

.match-time {
  font-size: 12px;
  color: #666;
}

.match-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f5f5;
  font-size: 12px;
  color: #666;
}
</style>
