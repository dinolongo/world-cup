// util/buildAdvancementMap.js
export const buildAdvancementMap = (layout) => {
  const map = {} // { sourceMatchNum: { winnerTo, position } }

  const processPairs = (sources, targets) => {
    for (let i = 0; i < sources.length; i += 2) {
      const targetNum = targets[Math.floor(i / 2)]
      map[sources[i]]     = { winnerTo: targetNum, position: 'team1' }
      map[sources[i + 1]] = { winnerTo: targetNum, position: 'team2' }
    }
  }

  // Left bracket
  processPairs(layout.left.ro32, layout.left.ro16)
  processPairs(layout.left.ro16, layout.left.qf)
  processPairs(layout.left.qf,   layout.left.sf)

  // Right bracket
  processPairs(layout.right.ro32, layout.right.ro16)
  processPairs(layout.right.ro16, layout.right.qf)
  processPairs(layout.right.qf,   layout.right.sf)

  // Semi-finals → final and third place
  const [sf1, sf2] = [...layout.left.sf, ...layout.right.sf]
  const finalNum      = layout.final[0]
  const thirdPlaceNum = layout.thirdPlace[0]

  map[sf1] = { winnerTo: finalNum,      loserTo: thirdPlaceNum, position: 'team1' }
  map[sf2] = { winnerTo: finalNum,      loserTo: thirdPlaceNum, position: 'team2' }

  return map
}