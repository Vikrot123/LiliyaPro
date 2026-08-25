package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionInputIsolationContractTest {

    @Test
    fun selection_without_metadata_should_not_alias_snapshot_values() {
        val sourceValues = linkedMapOf<String, Any?>(
            "first" to "A",
            "second" to "B"
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = sourceValues
        )

        val result = DefaultCognitiveContextSelector().select(snapshot)

        assertNotSame(
            snapshot.values,
            result.values
        )

        assertEquals(
            snapshot.values,
            result.values
        )
    }

    @Test
    fun empty_selection_should_not_alias_empty_snapshot_values() {
        val sourceValues = linkedMapOf<String, Any?>()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = sourceValues
        )

        val result = DefaultCognitiveContextSelector().select(snapshot)

        assertNotSame(
            snapshot.values,
            result.values
        )

        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )
    }

    @Test
    fun selection_should_preserve_input_value_order() {
        val sourceValues = linkedMapOf<String, Any?>(
            "first" to "FIRST",
            "second" to "SECOND",
            "third" to "THIRD"
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.SESSION,
            values = sourceValues
        )

        val result = DefaultCognitiveContextSelector().select(snapshot)

        assertEquals(
            listOf("first", "second", "third"),
            result.values.keys.toList()
        )

        assertEquals(
            listOf("FIRST", "SECOND", "THIRD"),
            result.values.values.toList()
        )
    }
}
