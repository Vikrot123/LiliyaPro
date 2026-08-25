package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionBoundaryContractTest {

    @Test
    fun negative_relevance_threshold_should_not_reject_default_metadata_values() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "payload")
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = -0.5
            )
        )

        assertEquals(snapshot.values, result.values)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun negative_importance_threshold_should_not_reject_default_metadata_values() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "payload")
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumImportance = -0.5
            )
        )

        assertEquals(snapshot.values, result.values)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun negative_confidence_threshold_should_not_reject_default_metadata_values() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "payload")
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumConfidence = -0.5
            )
        )

        assertEquals(snapshot.values, result.values)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun zero_maximum_age_should_accept_snapshot_created_at_selection_time() {
        val now = 100_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "payload"),
            timestamp = now
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 0L
            )
        )

        assertEquals(snapshot.values, result.values)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun future_snapshot_should_be_accepted() {
        val selector = DefaultCognitiveContextSelector(
            nowProvider = { 100_000L }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "payload"),
            timestamp = 100_001L
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 0L
            )
        )

        assertEquals(snapshot.values, result.values)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun missing_metadata_for_one_value_should_reject_only_that_value() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = linkedMapOf(
                "qualified" to "keep",
                "unqualified" to "drop"
            ),
            metadata = mapOf(
                "qualified" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
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

        assertEquals(
            mapOf("qualified" to "keep"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun negative_maximum_age_should_reject_even_current_snapshot() {
        val now = 100_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "payload"),
            timestamp = now
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = -1L
            )
        )

        assertEquals(emptyMap(), result.values)
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }
}
