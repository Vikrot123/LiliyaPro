package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionAgeOverflowContractTest {

    @Test
    fun extremely_old_snapshot_should_not_be_accepted_when_age_arithmetic_overflows() {
        val now = 100_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf(
                "value" to "ANCIENT"
            ),
            timestamp = Long.MIN_VALUE
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 10_000L
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
    fun extremely_old_snapshot_should_remain_rejected_with_large_but_valid_age_limit() {
        val now = 1_000_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = mapOf(
                "value" to "ANCIENT"
            ),
            timestamp = Long.MIN_VALUE
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = Long.MAX_VALUE
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
