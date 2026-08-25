package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionCountInvariantContractTest {

    @Test
    fun selected_and_rejected_counts_should_cover_all_values() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = linkedMapOf(
                "accepted_1" to "one",
                "accepted_2" to "two",
                "rejected_1" to "three",
                "rejected_2" to "four"
            ),
            metadata = mapOf(
                "accepted_1" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
                    confidence = 0.9
                ),
                "accepted_2" to CognitiveContextValueMetadata(
                    relevance = 0.8,
                    importance = 0.8,
                    confidence = 0.8
                ),
                "rejected_1" to CognitiveContextValueMetadata(
                    relevance = 0.2,
                    importance = 0.9,
                    confidence = 0.9
                ),
                "rejected_2" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.2,
                    confidence = 0.9
                )
            )
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.8,
                minimumImportance = 0.8,
                minimumConfidence = 0.8
            )
        )

        assertEquals(2, result.selectedCount)
        assertEquals(2, result.rejectedCount)
        assertEquals(
            snapshot.values.size,
            result.selectedCount + result.rejectedCount
        )
    }

    @Test
    fun age_rejection_should_count_every_value_as_rejected() {
        val now = 100_000L
        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = linkedMapOf(
                "one" to 1,
                "two" to 2,
                "three" to 3
            ),
            timestamp = 89_999L
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 10_000L
            )
        )

        assertEquals(0, result.selectedCount)
        assertEquals(3, result.rejectedCount)
        assertEquals(
            snapshot.values.size,
            result.selectedCount + result.rejectedCount
        )
    }

    @Test
    fun empty_snapshot_should_have_zero_selected_and_rejected_counts() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.SESSION
        )

        val result = selector.select(snapshot)

        assertEquals(0, result.selectedCount)
        assertEquals(0, result.rejectedCount)
        assertEquals(
            snapshot.values.size,
            result.selectedCount + result.rejectedCount
        )
    }
}
