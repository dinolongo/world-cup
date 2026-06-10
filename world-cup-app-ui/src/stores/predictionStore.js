import { defineStore } from 'pinia'
import thirdPlaceSeeding from '../data/third-place-seeding.json'

export const usePredictionStore = defineStore('prediction', {
  state: () => ({
    groups: {},
    selectedThirdPlaceTeams: [],
    roundOf32: []
  }),
  
  getters: {
    // Get the team name from a team code (e.g., "1A" -> 1st place team from Group A)
    getTeamFromCode: (state) => (code) => {
      if (!code) return null
      
      // Handle group position codes (1A, 2B, 3C, etc.)
      const match = code.match(/^(\d)([A-L])$/)
      if (match) {
        const position = parseInt(match[1]) - 1 // 0-indexed
        const group = `GROUP_${match[2]}`
        const groupTeams = state.groups[group]
        if (groupTeams && groupTeams[position]) {
          return groupTeams[position].teamName
        }
      }
      
      // Handle third place team codes (3A, 3B, etc.)
      const thirdPlaceMatch = code.match(/^3([A-L])$/)
      if (thirdPlaceMatch) {
        const group = `GROUP_${thirdPlaceMatch[1]}`
        const groupTeams = state.groups[group]
        if (groupTeams && groupTeams[2]) {
          return groupTeams[2].teamName
        }
      }
      
      return null
    },
    
    // Get the seeding lookup for third-place teams
    getThirdPlaceSeeding: (state) => () => {
      if (state.selectedThirdPlaceTeams.length !== 8) return null
      
      // Extract group letters from selected teams
      const groupLetters = state.selectedThirdPlaceTeams
        .map(t => t.groupName.replace('GROUP_', ''))
        .sort()
        .join('')
      
      // Look up in seeding table
      return thirdPlaceSeeding[groupLetters] || null
    },
    
    // Get the actual third-place team for a seeding position (e.g., "3E")
    getThirdPlaceTeamForSeeding: (state) => (seedingCode) => {
      const group = `GROUP_${seedingCode[1]}`
      const groupTeams = state.groups[group]
      if (groupTeams && groupTeams[2]) {
        return groupTeams[2].teamName
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
        t => t.team.teamName === teamData.team.teamName
      )
      if (index > -1) {
        this.selectedThirdPlaceTeams.splice(index, 1)
      } else if (this.selectedThirdPlaceTeams.length < 8) {
        this.selectedThirdPlaceTeams.push(teamData)
      }
    },
    
    isSelected(teamData) {
      return this.selectedThirdPlaceTeams.some(
        t => t.team.teamName === teamData.team.teamName
      )
    },
    
    reset() {
      this.groups = {}
      this.selectedThirdPlaceTeams = []
      this.roundOf32 = []
    }
  }
})
