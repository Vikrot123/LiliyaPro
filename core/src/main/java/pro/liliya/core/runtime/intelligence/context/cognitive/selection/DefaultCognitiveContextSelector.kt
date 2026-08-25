package pro.liliya.core.runtime.intelligence.context.cognitive.selection

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot

class DefaultCognitiveContextSelector(
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : CognitiveContextSelector {

    override fun select(
        snapshot: CognitiveContextSnapshot,
        criteria: CognitiveContextSelectionCriteria
    ): CognitiveContextSelection {
        val maximumAgeMillis = criteria.maximumAgeMillis

        if (maximumAgeMillis != null) {
            val now = nowProvider()
            var ageOverflowed = false
            val ageMillis = if (snapshot.timestamp >= now) {
                0L
            } else {
                try {
                    Math.subtractExact(now, snapshot.timestamp)
                } catch (_: ArithmeticException) {
                    ageOverflowed = true
                    Long.MAX_VALUE
                }
            }

            if (ageOverflowed || ageMillis > maximumAgeMillis) {
                return CognitiveContextSelection(
                    values = emptyMap(),
                    selectedCount = 0,
                    rejectedCount = snapshot.values.size
                )
            }
        }

        if (snapshot.metadata.isEmpty()) {
            return CognitiveContextSelection(
                values = LinkedHashMap(snapshot.values),
                selectedCount = snapshot.values.size,
                rejectedCount = 0
            )
        }

        val selected = linkedMapOf<String, Any?>()
        var rejectedCount = 0

        snapshot.values.forEach { (key, value) ->
            val metadata = snapshot.metadata[key]

            val accepted =
                metadata != null &&
                    metadata.relevance >= criteria.minimumRelevance &&
                    metadata.importance >= criteria.minimumImportance &&
                    metadata.confidence >= criteria.minimumConfidence

            if (accepted) {
                selected[key] = value
            } else {
                rejectedCount++
            }
        }

        return CognitiveContextSelection(
            values = selected,
            selectedCount = selected.size,
            rejectedCount = rejectedCount
        )
    }
}
