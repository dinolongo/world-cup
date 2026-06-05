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

// teams prop now contains the full standings data with team and statistics
</script>

<template>
  <div class="group-table-container">
    <h2 class="group-title">{{ groupName }}</h2>
    <div class="table-wrapper">
      <table class="group-table">
        <thead>
          <tr>
            <th class="team-col">Team</th>
            <th>MP</th>
            <th>W</th>
            <th>D</th>
            <th>L</th>
            <th>GF</th>
            <th>GA</th>
            <th>GD</th>
            <th>Pts</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="result in teams" :key="result.team.id">
            <td class="team-cell">
              <img :src="result.team.crest" :alt="result.team.name" class="team-crest">
              <span class="team-name">{{ result.team.name }}</span>
            </td>
            <td>{{ result.played }}</td>
            <td>{{ result.wins }}</td>
            <td>{{ result.draws }}</td>
            <td>{{ result.losses }}</td>
            <td>{{ result.goalsFor }}</td>
            <td>{{ result.goalsAgainst }}</td>
            <td>{{ result.goalDifference }}</td>
            <td class="points-cell">{{ result.points }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.group-table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 24px;
}

.group-title {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  padding: 16px 20px;
}

.table-wrapper {
  overflow-x: auto;
}

.group-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.group-table thead {
  background: #f5f5f5;
}

.group-table th {
  padding: 12px 16px;
  text-align: center;
  font-weight: 600;
  color: #1a1a1a;
  border-bottom: 2px solid #e0e0e0;
}

.group-table th.team-col {
  text-align: left;
  min-width: 200px;
}

.group-table td {
  padding: 12px 16px;
  text-align: center;
  border-bottom: 1px solid #e0e0e0;
  color: #333;
}

.group-table tbody tr:hover {
  background: #f9f9f9;
}

.team-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px !important;
}

.team-crest {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.team-name {
  font-weight: 600;
  color: #1a1a1a;
}

.points-cell {
  font-weight: 700;
  color: #1e3a5f;
  font-size: 16px;
}

@media (max-width: 768px) {
  .group-table {
    font-size: 12px;
  }
  
  .group-table th,
  .group-table td {
    padding: 8px 12px;
  }
  
  .team-crest {
    width: 24px;
    height: 24px;
  }
  
  .group-title {
    font-size: 16px;
    padding: 12px 16px;
  }
}
</style>
