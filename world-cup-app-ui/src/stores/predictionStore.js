import { defineStore } from 'pinia'
import thirdPlaceSeeding from '../data/third-place-seeding.json'

export const usePredictionStore = defineStore('prediction', {
  state: () => ({
    groups: {},
    selectedThirdPlaceTeams: []
  }),

  getters: {
    _resolveTeamCode: (state) => (code) => {
      if (!code) return null

      const groupPositionMatch = code.match(/^(\d)([A-L])$/)
      if (groupPositionMatch) {
        const position = parseInt(groupPositionMatch[1]) - 1
        const groupTeams = state.groups[`GROUP_${groupPositionMatch[2]}`]
        return groupTeams?.[position] ?? null
      }

      const thirdPlaceMatch = code.match(/^3([A-L])$/)
      if (thirdPlaceMatch) {
        const groupTeams = state.groups[`GROUP_${thirdPlaceMatch[1]}`]
        return groupTeams?.[2] ?? null
      }

      return null
    },

    getTeamFromCode: (state) => (code) => state._resolveTeamCode(code)?.teamName ?? null,

    getTeamIdFromCode: (state) => (code) => state._resolveTeamCode(code)?.teamId ?? null,

    getThirdPlaceSeeding: (state) => () => {
      if (state.selectedThirdPlaceTeams.length !== 8) return null
      const groupLetters = state.selectedThirdPlaceTeams
        .map(t => t.groupName.replace('GROUP_', ''))
        .sort()
        .join('')
      return thirdPlaceSeeding[groupLetters] ?? null
    },

    isThirdPlaceTeamSelected: (state) => (teamData) => {
      return state.selectedThirdPlaceTeams.some(
        t => t.team.teamId === teamData.team.teamId
      )
    },

    // ── Save/load support ─────────────────────────────────────────────────

    getGroupStagePredictions: (state) => () => ({
      groups: Object.fromEntries(
      Object.entries(state.groups).map(([groupName, teams]) => [
        groupName,
        teams.map(t => t.teamId)  // just the ordered ids, no stats
      ])
      ),
      selectedThirdPlaceTeams: state.selectedThirdPlaceTeams.map(t => t.groupName.replace('GROUP_', ''))
    }),

    // ── Team lookup helpers ────────────────────────────────────────────────

    getTeamById: (state) => (teamId) => {
      for (const group of Object.values(state.groups)) {
        const team = group.find(t => t.teamId === teamId)
        if (team) return team
      }
      return null
    }
  },

  actions: {
    setGroups(groups) {
      this.groups = groups
    },

    setSelectedThirdPlaceTeams(teams) {
      this.selectedThirdPlaceTeams = teams
    },

    toggleThirdPlaceTeam(teamData) {
      const index = this.selectedThirdPlaceTeams.findIndex(
        t => t.team.teamId === teamData.team.teamId
      )
      if (index > -1) {
        this.selectedThirdPlaceTeams.splice(index, 1)
      } else if (this.selectedThirdPlaceTeams.length < 8) {
        this.selectedThirdPlaceTeams.push(teamData)
      }
    },

    loadGroupStagePredictions(data) {
      // groups must be restored by caller via setGroups() after re-fetching /api/groups
      this.groups = data.groups ?? {}
      this.selectedThirdPlaceTeams = data.selectedThirdPlaceTeams ?? []
    },

    reset() {
      this.groups = {}
      this.selectedThirdPlaceTeams = []
    }
  }
})