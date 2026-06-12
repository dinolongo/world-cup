import { defineStore } from 'pinia'
import thirdPlaceSeeding from '../data/third-place-seeding.json'

export const usePredictionStore = defineStore('prediction', {
  state: () => ({
    groups: {},
    selectedThirdPlaceTeams: []
  }),

  getters: {
    getTeamFromCode: (state) => (code) => {
      if (!code) return null

      const groupPositionMatch = code.match(/^(\d)([A-L])$/)
      if (groupPositionMatch) {
        const position = parseInt(groupPositionMatch[1]) - 1
        const groupTeams = state.groups[`GROUP_${groupPositionMatch[2]}`]
        return groupTeams?.[position]?.teamName ?? null
      }

      const thirdPlaceMatch = code.match(/^3([A-L])$/)
      if (thirdPlaceMatch) {
        const groupTeams = state.groups[`GROUP_${thirdPlaceMatch[1]}`]
        return groupTeams?.[2]?.teamName ?? null
      }

      return null
    },

    getThirdPlaceSeeding: (state) => () => {
      if (state.selectedThirdPlaceTeams.length !== 8) return null
      const groupLetters = state.selectedThirdPlaceTeams
        .map(t => t.groupName.replace('GROUP_', ''))
        .sort()
        .join('')
      return thirdPlaceSeeding[groupLetters] ?? null
    },

    getThirdPlaceTeamForSeeding: (state) => (seedingCode) => {
      const groupTeams = state.groups[`GROUP_${seedingCode[1]}`]
      return groupTeams?.[2]?.teamName ?? null
    },

    isThirdPlaceTeamSelected: (state) => (teamData) => {
      return state.selectedThirdPlaceTeams.some(
        t => t.team.teamName === teamData.team.teamName
      )
    },

    // ── Save/load support ─────────────────────────────────────────────────

    getGroupStagePredictions: (state) => () => ({
      groups: state.groups,
      selectedThirdPlaceTeams: state.selectedThirdPlaceTeams
    })
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
        t => t.team.teamName === teamData.team.teamName
      )
      if (index > -1) {
        this.selectedThirdPlaceTeams.splice(index, 1)
      } else if (this.selectedThirdPlaceTeams.length < 8) {
        this.selectedThirdPlaceTeams.push(teamData)
      }
    },

    loadGroupStagePredictions(data) {
      this.groups = data.groups ?? {}
      this.selectedThirdPlaceTeams = data.selectedThirdPlaceTeams ?? []
    },

    reset() {
      this.groups = {}
      this.selectedThirdPlaceTeams = []
    }
  }
})