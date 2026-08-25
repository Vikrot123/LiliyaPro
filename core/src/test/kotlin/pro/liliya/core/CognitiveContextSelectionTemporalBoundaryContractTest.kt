package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionTemporalBoundaryContractTest {

    @Test
    fun future_snapshot_should_be_accepted() {
        val now = 100_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "FUTURE"),
            timestamp = now + 10_000L
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
    fun exact_maximum_age_should_be_accepted() {
        val now = 100_000L
        val maximumAge = 10_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = mapOf("value" to "BOUNDARY"),
            timestamp = now - maximumAge
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = maximumAge
            )
        )

        assertEquals(snapshot.values, result.values)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun one_millisecond_beyond_maximum_age_should_be_rejected() {
        val now = 100_000L
        val maximumAge = 10_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = mapOf("value" to "STALE"),
            timestamp = now - maximumAge - 1L
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = maximumAge
            )
        )

        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun negative_maximum_age_should_reject_current_snapshot() {
        val now = 100_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "CURRENT"),
            timestamp = now
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = -1L
            )
        )

        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }
}
