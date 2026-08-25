package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextMetadataAgeSelectionContractTest {

    private fun selector(now: Long): DefaultCognitiveContextSelector =
        DefaultCognitiveContextSelector(nowProvider = { now })

    private fun snapshot(timestamp: Long): CognitiveContextSnapshot {
        return CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = linkedMapOf(
                "fresh" to "keep"
            ),
            metadata = mapOf(
                "fresh" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 1.0,
                    confidence = 1.0
                )
            ),
            timestamp = timestamp
        )
    }

    @Test
    fun selector_should_accept_fresh_snapshot_within_maximum_age() {
        val result = selector(100_000L).select(
            snapshot = snapshot(90_000L),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 10_000L
            )
        )

        assertEquals(
            mapOf("fresh" to "keep"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun selector_should_reject_snapshot_older_than_maximum_age() {
        val result = selector(100_000L).select(
            snapshot = snapshot(89_999L),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 10_000L
            )
        )

        assertTrue(result.values.isEmpty())
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun null_maximum_age_should_disable_age_filter() {
        val result = selector(100_000L).select(
            snapshot = snapshot(1L),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = null
            )
        )

        assertEquals(
            mapOf("fresh" to "keep"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun snapshot_at_exact_maximum_age_should_remain_accepted() {
        val result = selector(100_000L).select(
            snapshot = snapshot(90_000L),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 10_000L
            )
        )

        assertEquals(
            mapOf("fresh" to "keep"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun future_snapshot_should_not_be_rejected_as_too_old() {
        val result = selector(100_000L).select(
            snapshot = snapshot(110_000L),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 10_000L
            )
        )

        assertEquals(
            mapOf("fresh" to "keep"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun age_filter_should_coexist_with_metadata_thresholds() {
        val result = selector(100_000L).select(
            snapshot = CognitiveContextSnapshot(
                type = CognitiveContextType.WORKING,
                values = linkedMapOf(
                    "qualified" to "keep",
                    "old" to "drop"
                ),
                metadata = mapOf(
                    "qualified" to CognitiveContextValueMetadata(
                        relevance = 0.9,
                        importance = 0.9,
                        confidence = 0.9
                    ),
                    "old" to CognitiveContextValueMetadata(
                        relevance = 0.7,
                        importance = 0.9,
                        confidence = 0.9
                    )
                ),
                timestamp = 90_000L
            ),
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.8,
                minimumImportance = 0.8,
                minimumConfidence = 0.8,
                maximumAgeMillis = 10_000L
            )
        )

        assertEquals(
            mapOf("qualified" to "keep"),
            result.values
        )

        assertEquals(1, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }
}
