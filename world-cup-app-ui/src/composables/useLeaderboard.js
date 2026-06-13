import { ref, computed } from 'vue'
import { getLeaderboard, getLeaderboardCount, getGroups } from '../services/api'
import { teamCrests } from '../util/constants'

export const useLeaderboard = () => {
  const entries = ref([])
  const loading = ref(true)
  const searchQuery = ref('')
  const totalCount = ref(0)
  const teamsMap = ref(new Map())

  const scoringStatus = computed(() => {
    if (entries.value.length === 0) return 'none'
    
    const scoredEntries = entries.value.filter(entry => entry.totalScore !== null)
    
    if (scoredEntries.length === 0) return 'not-started'
    if (scoredEntries.length === entries.value.length) return 'final'
    return 'in-progress'
  })

  const fetchLeaderboard = async (page = 0, size = 20) => {
    try {
      loading.value = true
      const [data, count] = await Promise.all([
        getLeaderboard(page, size),
        getLeaderboardCount()
      ])
      
      entries.value = data.content || []
      totalCount.value = count
    } catch (error) {
      console.error('Failed to fetch leaderboard:', error)
      entries.value = []
      totalCount.value = 0
    } finally {
      loading.value = false
    }
  }

  const fetchTeams = async () => {
    try {
      const teams = await getGroups()
      const map = new Map()
      teams.forEach(team => {
        map.set(team.teamId, team)
      })
      teamsMap.value = map
    } catch (error) {
      console.error('Failed to fetch teams:', error)
    }
  }

  const getTeamById = (teamId) => {
    return teamsMap.value.get(teamId)
  }

  const getTeamName = (teamId) => {
    const team = getTeamById(teamId)
    return team ? team.teamName : null
  }

  const getTeamCrest = (teamId) => {
    const team = getTeamById(teamId)
    return team ? teamCrests[team.teamName] : null
  }

  const filteredEntries = computed(() => {
    if (!searchQuery.value) return entries.value
    
    const query = searchQuery.value.toLowerCase()
    return entries.value.filter(entry =>
      entry.displayName.toLowerCase().includes(query)
    )
  })

  const initialize = async () => {
    await Promise.all([
      fetchTeams(),
      fetchLeaderboard()
    ])
  }

  return {
    entries,
    filteredEntries,
    loading,
    scoringStatus,
    searchQuery,
    totalCount,
    getTeamName,
    getTeamCrest,
    fetchLeaderboard,
    initialize
  }
}
