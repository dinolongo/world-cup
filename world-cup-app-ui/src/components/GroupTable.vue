<script setup>
const props = defineProps({
  groupName: {
    type: String,
    required: true
  },
  teams: {
    type: Array,
    required: true
  }
})

const headers = [
  { title: 'Team', key: 'team', align: 'start', width: '38%' },
  { title: 'MP', key: 'played', align: 'center', width: '7%' },
  { title: 'W', key: 'wins', align: 'center', width: '7%' },
  { title: 'D', key: 'draws', align: 'center', width: '7%' },
  { title: 'L', key: 'losses', align: 'center', width: '7%' },
  { title: 'GF', key: 'goalsFor', align: 'center', width: '7%' },
  { title: 'GA', key: 'goalsAgainst', align: 'center', width: '7%' },
  { title: 'GD', key: 'goalDifference', align: 'center', width: '7%' },
  { title: 'Pts', key: 'points', align: 'center', width: '7%' },
]
</script>

<template>
  <v-card class="group-table-card" elevation="2" rounded="xl">
    <v-card-title class="group-header">{{ groupName }}</v-card-title>
    <v-data-table
      :headers="headers"
      :items="teams"
      density="compact"
      hide-default-footer
      :items-per-page="-1"
      class="standings-table"
    >
      <template v-slot:item.team="{ item }">
        <div class="team-cell">
          <img :src="item.team.crest" :alt="item.team.name" class="team-crest">
          <span class="team-name">{{ item.team.name }}</span>
        </div>
      </template>
      <template v-slot:item.points="{ item }">
        <span class="points-cell">{{ item.points }}</span>
      </template>
    </v-data-table>
  </v-card>
</template>

<style scoped>
.group-table-card {
  margin-bottom: 16px;
  border: none;
}

.group-header {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  padding: 12px 16px;
}

.standings-table :deep(.v-table__wrapper) {
  overflow-x: auto;
}

.standings-table :deep(table) {
  font-size: 13px;
}

.standings-table :deep(th) {
  font-size: 13px !important;
  font-weight: 600 !important;
  color: #424242 !important;
  padding: 8px 4px !important;
  white-space: nowrap;
  background-color: #f5f5f5 !important;
  text-align: center !important;
}

.standings-table :deep(td) {
  padding: 8px 4px !important;
  font-size: 13px !important;
}

.standings-table :deep(.v-data-table__tr:hover) {
  background-color: #f5f5f5 !important;
}

.team-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  text-align: left;
}

.team-crest {
  width: 20px;
  height: 20px;
  object-fit: contain;
  flex-shrink: 0;
}

.team-name {
  font-weight: 600;
  color: #212121;
  font-size: 12px;
  text-align: left;
  line-height: 1.2;
}

.points-cell {
  font-weight: 700;
  color: #1e3a5f;
  font-size: 14px;
}

@media (max-width: 600px) {
  .group-header {
    font-size: 14px;
    padding: 10px 12px;
  }

  .standings-table :deep(table) {
    font-size: 11px;
  }

  .standings-table :deep(th) {
    font-size: 10px !important;
    padding: 6px 3px !important;
  }

  .standings-table :deep(td) {
    padding: 6px 3px !important;
    font-size: 11px !important;
  }

  .team-crest {
    width: 16px;
    height: 16px;
  }

  .team-name {
    font-size: 11px;
  }

  .points-cell {
    font-size: 13px;
  }
}
</style>
