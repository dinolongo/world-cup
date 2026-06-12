import { ref, computed } from 'vue'
import { teamCrests, TOTAL_KNOCKOUT_MATCHES, BRACKET_LAYOUT } from '../util/constants'
import bracketAdvancement from '../data/bracket-advancement.json'
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
      return knockoutPredictions.value[sourceMatchNum] ?? `Winner of Match ${sourceMatchNum}`
    }

    if (teamCode.startsWith('L')) {
      const sourceMatchNum = teamCode.substring(1)
      return knockoutLosers.value[sourceMatchNum] ?? `Loser of Match ${sourceMatchNum}`
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

  const getTeamCrest = (teamName) => teamCrests[teamName] ?? null

  // ── Match lookups ─────────────────────────────────────────────────────────

  const finalMatch      = computed(() => knockoutMatches.value.find(m => m.round === 'Final'))
  const thirdPlaceMatch = computed(() => knockoutMatches.value.find(m => m.round === 'Match for third place'))
  const sf101Match      = computed(() => knockoutMatches.value.find(m => m.num === 101))
  const sf102Match      = computed(() => knockoutMatches.value.find(m => m.num === 102))

  // ── Derived display state ─────────────────────────────────────────────────

  const finalWinner = computed(() =>
    finalMatch.value ? knockoutPredictions.value[finalMatch.value.num] ?? null : null
  )

  const finalRunnerUp = computed(() => {
    if (!finalMatch.value || !finalWinner.value) return null
    const team1 = getTeamName(finalMatch.value.team1, finalMatch.value.num, finalMatch.value.team2)
    const team2 = getTeamName(finalMatch.value.team2, finalMatch.value.num, finalMatch.value.team1)
    return finalWinner.value === team1 ? team2 : team1
  })

  const thirdPlaceWinner = computed(() =>
    thirdPlaceMatch.value ? knockoutPredictions.value[thirdPlaceMatch.value.num] ?? null : null
  )

  // ── Prediction actions ────────────────────────────────────────────────────

  const selectWinner = (match, team) => {
    knockoutPredictions.value[match.num] = team

    const entry = advancementMap[match.num]
    if (!entry) return

    if (entry.loserTo) {
      const team1 = getTeamName(match.team1, match.num, match.team2)
      const team2 = getTeamName(match.team2, match.num, match.team1)
      knockoutLosers.value[match.num] = team === team1 ? team2 : team1

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

  const allPredictionsMade = computed(() =>
    console.log(Object.keys(knockoutPredictions.value).length),
    Object.keys(knockoutPredictions.value).length >= TOTAL_KNOCKOUT_MATCHES
  )

  const buildPayload = () => ({
    predictions: { ...knockoutPredictions.value },
    groupStage: predictionStore.getGroupStagePredictions()
  })

  const loadPredictions = (data) => {
    if (data.predictions) knockoutPredictions.value = data.predictions
    if (data.groupStage) predictionStore.loadGroupStagePredictions(data.groupStage)
  }

  return {
    finalMatch, thirdPlaceMatch, sf101Match, sf102Match,
    finalWinner, finalRunnerUp, thirdPlaceWinner,
    getTeamName, getTeamCrest,
    selectWinner, resetPredictions,
    allPredictionsMade, buildPayload, loadPredictions
  }
}