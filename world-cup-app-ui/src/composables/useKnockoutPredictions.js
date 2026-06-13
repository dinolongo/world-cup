import { ref, computed } from 'vue'
import { teamCrests, TOTAL_KNOCKOUT_MATCHES, BRACKET_LAYOUT } from '../util/constants'
import { buildAdvancementMap } from './useAdvancementMap'

export const useKnockoutPredictions = (knockoutMatches, predictionStore) => {
  const knockoutPredictions = ref({})
  const knockoutLosers = ref({})

  const advancementMap = buildAdvancementMap(BRACKET_LAYOUT)


  const thirdPlaceSeeding = computed(() => predictionStore.getThirdPlaceSeeding())

  // ── Team name resolution ──────────────────────────────────────────────────

  const formatTeamNamePlaceholder = (teamCode) => {
    const groupMatch = teamCode.match(/^(\d)([A-L])$/)
    if (groupMatch) {
      const [, position, group] = groupMatch
      const positionText = position === '1' ? 'Winner' : position === '2' ? 'Runner-up' : '3rd Place'
      return `${positionText} of Group ${group}`
    }
    return teamCode
  }

  const getTeamName = (teamCode, _matchNum, opponentCode) => {
    if (!teamCode) return 'TBD'

    if (teamCode.startsWith('W')) {
      const sourceMatchNum = teamCode.substring(1)
      const teamId = knockoutPredictions.value[sourceMatchNum]
      if (teamId) {
        const team = predictionStore.getTeamById(teamId)
        if (team) return team.teamName
      }
      return `Winner of Match ${sourceMatchNum}`
    }

    if (teamCode.startsWith('L')) {
      const sourceMatchNum = teamCode.substring(1)
      const teamId = knockoutLosers.value[sourceMatchNum]
      if (teamId) {
        const team = predictionStore.getTeamById(teamId)
        if (team) return team.teamName
      }
      return `Loser of Match ${sourceMatchNum}`
    }

    if (teamCode.includes('/')) {
      if (thirdPlaceSeeding.value && opponentCode) {
        const thirdPlaceCode = thirdPlaceSeeding.value[opponentCode]
        const teamName = thirdPlaceCode && predictionStore.getTeamFromCode(thirdPlaceCode)
        if (teamName) return teamName
      }
      return 'Best 3rd Place Team'
    }

    return predictionStore.getTeamFromCode(teamCode) ?? formatTeamNamePlaceholder(teamCode)
  }

  const getTeamId = (teamCode, _matchNum, opponentCode) => {
    if (!teamCode) return null

    if (teamCode.startsWith('W')) {
      const sourceMatchNum = teamCode.substring(1)
      return knockoutPredictions.value[sourceMatchNum] ?? null
    }

    if (teamCode.startsWith('L')) {
      const sourceMatchNum = teamCode.substring(1)
      return knockoutLosers.value[sourceMatchNum] ?? null
    }

    if (teamCode.includes('/')) {
      if (thirdPlaceSeeding.value && opponentCode) {
        const thirdPlaceCode = thirdPlaceSeeding.value[opponentCode]
        if (thirdPlaceCode) {
          return predictionStore.getTeamIdFromCode(thirdPlaceCode)
        }
      }
      return null
    }

    // Look up teamId from team code (e.g., "1A" -> teamId)
    return predictionStore.getTeamIdFromCode(teamCode)
  }

  const getTeamCrest = (teamName) => teamCrests[teamName] ?? null

  // ── Match lookups ─────────────────────────────────────────────────────────

  const finalMatch      = computed(() => knockoutMatches.value.find(m => m.round === 'Final'))
  const thirdPlaceMatch = computed(() => knockoutMatches.value.find(m => m.round === 'Match for third place'))
  const sf101Match      = computed(() => knockoutMatches.value.find(m => m.num === 101))
  const sf102Match      = computed(() => knockoutMatches.value.find(m => m.num === 102))

  // ── Derived display state ─────────────────────────────────────────────────

  const finalWinner = computed(() => {
    const teamId = finalMatch.value ? knockoutPredictions.value[finalMatch.value.num] ?? null : null
    if (teamId) {
      const team = predictionStore.getTeamById(teamId)
      return team ? team.teamName : null
    }
    return null
  })

  const finalRunnerUp = computed(() => {
    if (!finalMatch.value) return null
    const winnerId = knockoutPredictions.value[finalMatch.value.num]
    if (!winnerId) return null
    const team1Id = getTeamId(finalMatch.value.team1, finalMatch.value.num, finalMatch.value.team2)
    const team2Id = getTeamId(finalMatch.value.team2, finalMatch.value.num, finalMatch.value.team1)
    const loserId = winnerId === team1Id ? team2Id : team1Id
    return loserId ? predictionStore.getTeamById(loserId)?.teamName ?? null : null
  })

  const thirdPlaceWinner = computed(() => {
    const teamId = thirdPlaceMatch.value ? knockoutPredictions.value[thirdPlaceMatch.value.num] ?? null : null
    if (teamId) {
      const team = predictionStore.getTeamById(teamId)
      return team ? team.teamName : null
    }
    return null
  })

  

  // ── Prediction actions ────────────────────────────────────────────────────

  const selectWinner = (match, teamId) => {
    console.log(teamId)
    knockoutPredictions.value[match.num] = teamId

    const entry = advancementMap[match.num]
    if (!entry) return

    if (entry.loserTo) {
      const team1Id = getTeamId(match.team1, match.num, match.team2)
      const team2Id = getTeamId(match.team2, match.num, match.team1)
      knockoutLosers.value[match.num] = teamId === team1Id ? team2Id : team1Id

      const loserMatch = knockoutMatches.value.find(m => m.num === entry.loserTo)
      if (loserMatch) {
        if (entry.position === 'team1') loserMatch.team1 = `L${match.num}`
        else loserMatch.team2 = `L${match.num}`
      }
    }

    const advancingMatch = knockoutMatches.value.find(m => m.num === entry.winnerTo)
    if (!advancingMatch) return

    if (entry.position === 'team1') advancingMatch.team1 = `W${match.num}`
    else advancingMatch.team2 = `W${match.num}`
  }

  const resetPredictions = () => {
    knockoutPredictions.value = {}
    knockoutLosers.value = {}
  }

  // ── Save / load ───────────────────────────────────────────────────────────

  const allPredictionsMade = computed(() => {
    console.log(Object.keys(knockoutPredictions.value).length)
    return Object.keys(knockoutPredictions.value).length === TOTAL_KNOCKOUT_MATCHES
    // 32 === TOTAL_KNOCKOUT_MATCHES
  })

  const buildPayload = () => ({
    knockoutPredictions: { ...knockoutPredictions.value },
    groupStagePredictions: predictionStore.getGroupStagePredictions()
  })

  const loadPredictions = (data) => {
    if (data.knockoutPredictions) knockoutPredictions.value = data.knockoutPredictions
    if (data.groupStagePredictions) predictionStore.loadGroupStagePredictions(data.groupStagePredictions)
  }

  return {
    finalMatch, thirdPlaceMatch, sf101Match, sf102Match,
    finalWinner, finalRunnerUp, thirdPlaceWinner,
    getTeamName, getTeamId, getTeamCrest,
    selectWinner, resetPredictions,
    allPredictionsMade, buildPayload, loadPredictions
  }
}