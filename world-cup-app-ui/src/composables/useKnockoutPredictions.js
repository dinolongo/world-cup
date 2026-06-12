import { ref, computed } from 'vue'
import { teamCrests } from '../util/constants'

export const useKnockoutPredictions = (knockoutMatches, predictionStore) => {
  const knockoutPredictions = ref({})
  const knockoutLosers = ref({})

  const thirdPlaceSeeding = computed(() => predictionStore.getThirdPlaceSeeding())

  const finalMatch = computed(() => {
    return knockoutMatches.value.find(match => match.round === 'Final')
  })

  const thirdPlaceMatch = computed(() => {
    return knockoutMatches.value.find(match => match.round === 'Match for third place')
  })

  const finalWinner = computed(() => {
    if (finalMatch.value && knockoutPredictions.value[finalMatch.value.num]) {
      return knockoutPredictions.value[finalMatch.value.num]
    }
    return null
  })

  const finalRunnerUp = computed(() => {
    if (finalMatch.value && knockoutPredictions.value[finalMatch.value.num]) {
      const team1Name = getTeamName(finalMatch.value.team1, finalMatch.value.num, finalMatch.value.team2)
      const team2Name = getTeamName(finalMatch.value.team2, finalMatch.value.num, finalMatch.value.team1)
      const winner = knockoutPredictions.value[finalMatch.value.num]
      return winner === team1Name ? team2Name : team1Name
    }
    return null
  })

  const thirdPlaceWinner = computed(() => {
    if (thirdPlaceMatch.value && knockoutPredictions.value[thirdPlaceMatch.value.num]) {
      return knockoutPredictions.value[thirdPlaceMatch.value.num]
    }
    return null
  })

  const getWinnerCrest = (teamName) => {
    return teamCrests[teamName] || null
  }

  const formatTeamNamePlaceholder = (teamCode) => {
    const groupMatch = teamCode.match(/^(\d)([A-L])$/)
    if (groupMatch) {
      const position = groupMatch[1]
      const group = groupMatch[2]
      const positionText = position === '1' ? 'Winner' : position === '2' ? 'Runner-up' : '3rd Place'
      return `${positionText} of Group ${group}`
    }
    return teamCode
  }

  const getTeamName = (teamCode, matchNum, opponentCode) => {
    if (!teamCode) return 'TBD'
    
    if (teamCode.startsWith('W')) {
      const sourceMatchNum = teamCode.substring(1)
      if (knockoutPredictions.value[sourceMatchNum]) {
        return knockoutPredictions.value[sourceMatchNum]
      }
      return `Winner of Match ${sourceMatchNum}`
    }
    
    if (teamCode.startsWith('L')) {
      const matchNum = teamCode.substring(1)
      if (knockoutLosers.value[matchNum]) {
        return knockoutLosers.value[matchNum]
      }
      return `Loser of Match ${matchNum}`
    }
    
    if (teamCode.includes('/')) {
      if (thirdPlaceSeeding.value && opponentCode) {
        const thirdPlaceCode = thirdPlaceSeeding.value[opponentCode]
        if (thirdPlaceCode) {
          const teamName = predictionStore.getTeamFromCode(thirdPlaceCode)
          if (teamName) return teamName
        }
      }
      return 'Best 3rd Place Team'
    }
    
    const predictedTeam = predictionStore.getTeamFromCode(teamCode)
    if (predictedTeam) return predictedTeam
    
    return formatTeamNamePlaceholder(teamCode)
  }

  const selectWinner = (match, team) => {
    knockoutPredictions.value[match.num] = team
    
    if (match.num === 101 || match.num === 102) {
      const team1Name = getTeamName(match.team1, match.num, match.team2)
      const team2Name = getTeamName(match.team2, match.num, match.team1)
      const loser = team === team1Name ? team2Name : team1Name
      knockoutLosers.value[match.num] = loser
    }
    
    import('../data/bracket-advancement.json').then(data => {
      const advancement = data.default || data
      
      const advancesTo = advancement.forward[match.num]
      if (advancesTo) {
        const advancingMatch = knockoutMatches.value.find(m => m.num === advancesTo.matchNum)
        if (advancingMatch) {
          if (advancesTo.position === 'team1') {
            advancingMatch.team1 = `W${match.num}`
          } else if (advancesTo.position === 'team2') {
            advancingMatch.team2 = `W${match.num}`
          }
        }
      }
    })
  }

  const getPredictionsData = () => {
    return {
      predictions: knockoutPredictions.value,
      losers: knockoutLosers.value
    }
  }

  const loadPredictionsData = (data) => {
    if (data.predictions) {
      knockoutPredictions.value = data.predictions
    }
    if (data.losers) {
      knockoutLosers.value = data.losers
    }
  }

  const resetPredictions = () => {
    knockoutPredictions.value = {}
    knockoutLosers.value = {}
  }

  return {
    knockoutPredictions,
    knockoutLosers,
    finalWinner,
    finalRunnerUp,
    thirdPlaceWinner,
    getWinnerCrest,
    getTeamName,
    selectWinner,
    getPredictionsData,
    loadPredictionsData,
    resetPredictions
  }
}
