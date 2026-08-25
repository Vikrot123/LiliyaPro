package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionContractTest {

    @Test
    fun selector_should_preserve_snapshot_values() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf(
                "memory" to "important",
                "runtime" to "ready"
            )
        )

        val result = selector.select(snapshot)

        assertEquals(
            snapshot.values,
            result.values
        )

        assertEquals(
            2,
            result.selectedCount
        )

        assertEquals(
            0,
            result.rejectedCount
        )
    }

    @Test
    fun selector_should_handle_empty_snapshot() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING
        )

        val result = selector.select(snapshot)

        assertTrue(result.values.isEmpty())
        assertEquals(0, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun selector_should_accept_selection_criteria_without_being_coupled_to_sources() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.PROCESSOR,
            values = mapOf("value" to "test")
        )

        val criteria = CognitiveContextSelectionCriteria(
            minimumRelevance = 0.7,
            minimumImportance = 0.5,
            minimumConfidence = 0.8,
            maximumAgeMillis = 60_000L
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = criteria
        )

        assertEquals(
            snapshot.values,
            result.values
        )
    }

    @Test
    fun selector_should_not_mutate_snapshot() {
        val selector = DefaultCognitiveContextSelector()

        val values = linkedMapOf<String, Any?>(
            "first" to "A",
            "second" to "B"
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.SESSION,
            values = values
        )

        selector.select(snapshot)

        assertEquals(
            values,
            snapshot.values
        )
    }
}
