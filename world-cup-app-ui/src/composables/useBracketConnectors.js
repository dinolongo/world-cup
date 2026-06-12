import { ref } from 'vue'

export const useBracketConnectors = (leftBracketEl, rightBracketEl, bracketContainerEl) => {
  const leftConnectorPaths = ref([])
  const rightConnectorPaths = ref([])
  const centerConnectorPaths = ref([])
  const leftSvgViewBox = ref('0 0 0 0')
  const rightSvgViewBox = ref('0 0 0 0')
  const centerSvgViewBox = ref('0 0 0 0')
  const cardRefs = ref({})

  const setCardRef = (el, matchNum) => {
    if (el) cardRefs.value[matchNum] = el.$el
  }

  const getRelativeRect = (el, container) => {
    const elRect = el.getBoundingClientRect()
    const containerRect = container.getBoundingClientRect()
    return {
      top: elRect.top - containerRect.top,
      bottom: elRect.bottom - containerRect.top,
      left: elRect.left - containerRect.left,
      right: elRect.right - containerRect.left,
      midY: (elRect.top + elRect.bottom) / 2 - containerRect.top
    }
  }

  const leftBracketPairs = [
    { sources: [74, 77], target: 89 },
    { sources: [73, 75], target: 90 },
    { sources: [83, 84], target: 93 },
    { sources: [81, 82], target: 94 },
    { sources: [89, 90], target: 97 },
    { sources: [93, 94], target: 98 },
  ]

  const rightBracketPairs = [
    { sources: [76, 78], target: 91 },
    { sources: [79, 80], target: 92 },
    { sources: [86, 88], target: 95 },
    { sources: [85, 87], target: 96 },
    { sources: [91, 92], target: 99 },
    { sources: [95, 96], target: 100 },
  ]

  const centerConnectorPairs = [
    {
      type: 'qf-to-sf',
      sources: [97, 98],
      target: 101
    },
    {
      type: 'qf-to-sf',
      sources: [99, 100],
      target: 102
    },
    {
      type: 'sf-to-final',
      sources: [101, 102],
      target: 104
    },
    {
      type: 'sf-to-third',
      sources: [101, 102],
      target: 103
    }
  ]

  const calculatePaths = (pairs, containerEl, direction = 'left') => {
    if (!containerEl) return []
    const paths = []

    for (const pair of pairs) {
      const el1 = cardRefs.value[pair.sources[0]]
      const el2 = cardRefs.value[pair.sources[1]]
      const elTarget = cardRefs.value[pair.target]
      if (!el1 || !el2 || !elTarget) continue

      const r1 = getRelativeRect(el1, containerEl)
      const r2 = getRelativeRect(el2, containerEl)
      const rT = getRelativeRect(elTarget, containerEl)

      const x1 = direction === 'left' ? r1.right : r1.left
      const x2 = direction === 'left' ? r2.right : r2.left
      const xT = direction === 'left' ? rT.left : rT.right

      const y1 = r1.midY
      const y2 = r2.midY
      const yT = rT.midY
      const midY = (y1 + y2) / 2
      const midX = (x1 + xT) / 2

      const d = `
        M ${x1} ${y1} H ${midX} V ${midY}
        M ${x2} ${y2} H ${midX} V ${midY}
        M ${midX} ${midY} H ${xT}
      `.trim()

      paths.push({ id: `${pair.sources[0]}-${pair.sources[1]}`, d })
    }

    return paths
  }

  const calculateCenterPaths = (pairs, containerEl) => {
    if (!containerEl) return []

    const paths = []

    for (const pair of pairs) {
      const el1 = cardRefs.value[pair.sources[0]]
      const el2 = cardRefs.value[pair.sources[1]]
      const elTarget = cardRefs.value[pair.target]

      if (!el1 || !el2 || !elTarget) continue

      const r1 = getRelativeRect(el1, containerEl)
      const r2 = getRelativeRect(el2, containerEl)
      const rT = getRelativeRect(elTarget, containerEl)

      if (pair.type === 'sf-to-final') {
        const joinX = (rT.left + rT.right) / 2
        const joinY = rT.bottom + 30

        const d = `
          M ${r1.right} ${r1.midY}
          H ${joinX}
          V ${joinY}

          M ${r2.left} ${r2.midY}
          H ${joinX}
          V ${joinY}

          M ${joinX} ${joinY}
          V ${rT.bottom}
        `.trim()

        paths.push({
          id: `${pair.sources[0]}-${pair.sources[1]}-${pair.target}`,
          d
        })

        continue
      }

      const x1 = r1.right
      const x2 = r2.left
      const xT = rT.left

      const y1 = r1.midY
      const y2 = r2.midY
      const yT = rT.midY

      const midY = (y1 + y2) / 2
      const midX = (x1 + xT) / 2

      const d = `
        M ${x1} ${y1} H ${midX} V ${midY}
        M ${x2} ${y2} H ${midX} V ${midY}
        M ${midX} ${midY} H ${xT}
      `.trim()

      paths.push({
        id: `${pair.sources[0]}-${pair.sources[1]}-${pair.target}`,
        d
      })
    }

    return paths
  }

  const recalculatePaths = () => {
    if (leftBracketEl.value) {
      const r = leftBracketEl.value.getBoundingClientRect()
      leftSvgViewBox.value = `0 0 ${r.width} ${r.height}`
    }
    if (rightBracketEl.value) {
      const r = rightBracketEl.value.getBoundingClientRect()
      rightSvgViewBox.value = `0 0 ${r.width} ${r.height}`
    }
    if (bracketContainerEl.value) {
      const r = bracketContainerEl.value.getBoundingClientRect()
      centerSvgViewBox.value = `0 0 ${r.width} ${r.height}`
    }
    leftConnectorPaths.value = calculatePaths(leftBracketPairs, leftBracketEl.value, 'left')
    rightConnectorPaths.value = calculatePaths(rightBracketPairs, rightBracketEl.value, 'right')
    centerConnectorPaths.value = calculateCenterPaths(centerConnectorPairs, bracketContainerEl.value)
  }

  return {
    leftConnectorPaths,
    rightConnectorPaths,
    centerConnectorPaths,
    leftSvgViewBox,
    rightSvgViewBox,
    centerSvgViewBox,
    setCardRef,
    recalculatePaths
  }
}
