package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionMetadataOrphanKeyContractTest {

    @Test
    fun metadata_without_matching_value_should_not_create_selection_entry() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = linkedMapOf(
                "real" to "VALUE"
            ),
            metadata = mapOf(
                "real" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
                    confidence = 0.9
                ),
                "orphan" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 1.0,
                    confidence = 1.0
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.8,
                minimumImportance = 0.8,
                minimumConfidence = 0.8
            )
        )

        assertEquals(
            mapOf("real" to "VALUE"),
            result.values
        )
        assertFalse(result.values.containsKey("orphan"))
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun multiple_orphan_metadata_entries_should_not_affect_counts() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = linkedMapOf(
                "accepted" to "OK",
                "rejected" to "DROP"
            ),
            metadata = mapOf(
                "accepted" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
                    confidence = 0.9
                ),
                "rejected" to CognitiveContextValueMetadata(
                    relevance = 0.1,
                    importance = 0.9,
                    confidence = 0.9
                ),
                "orphan_1" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 1.0,
                    confidence = 1.0
                ),
                "orphan_2" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 1.0,
                    confidence = 1.0
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.8,
                minimumImportance = 0.8,
                minimumConfidence = 0.8
            )
        )

        assertEquals(
            mapOf("accepted" to "OK"),
            result.values
        )
        assertFalse(result.values.containsKey("orphan_1"))
        assertFalse(result.values.containsKey("orphan_2"))
        assertEquals(1, result.selectedCount)
        assertEquals(1, result.rejectedCount)
        assertEquals(
            snapshot.values.size,
            result.selectedCount + result.rejectedCount
        )
    }
}
