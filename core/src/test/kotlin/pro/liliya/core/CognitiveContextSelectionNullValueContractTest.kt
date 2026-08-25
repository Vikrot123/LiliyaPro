package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionNullValueContractTest {

    @Test
    fun null_value_with_qualifying_metadata_should_be_selected() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = linkedMapOf(
                "nullable" to null
            ),
            metadata = mapOf(
                "nullable" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
                    confidence = 0.9
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertTrue(result.values.containsKey("nullable"))
        assertEquals(null, result.values["nullable"])
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun null_value_with_rejected_metadata_should_be_rejected_normally() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = linkedMapOf(
                "nullable" to null
            ),
            metadata = mapOf(
                "nullable" to CognitiveContextValueMetadata(
                    relevance = 0.2,
                    importance = 0.9,
                    confidence = 0.9
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun null_and_non_null_values_should_be_counted_independently() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = linkedMapOf(
                "null_value" to null,
                "text_value" to "READY",
                "rejected_value" to null
            ),
            metadata = mapOf(
                "null_value" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
                    confidence = 0.9
                ),
                "text_value" to CognitiveContextValueMetadata(
                    relevance = 0.8,
                    importance = 0.8,
                    confidence = 0.8
                ),
                "rejected_value" to CognitiveContextValueMetadata(
                    relevance = 0.1,
                    importance = 0.9,
                    confidence = 0.9
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertEquals(
            listOf("null_value", "text_value"),
            result.values.keys.toList()
        )
        assertTrue(result.values.containsKey("null_value"))
        assertEquals(null, result.values["null_value"])
        assertEquals("READY", result.values["text_value"])
        assertEquals(2, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    private fun criteria(): CognitiveContextSelectionCriteria {
        return CognitiveContextSelectionCriteria(
            minimumRelevance = 0.8,
            minimumImportance = 0.8,
            minimumConfidence = 0.8
        )
    }
}
