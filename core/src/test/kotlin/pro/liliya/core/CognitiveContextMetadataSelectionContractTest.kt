package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextMetadataSelectionContractTest {

    @Test
    fun selector_should_select_value_when_relevance_meets_threshold() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf(
                "memory" to "relevant"
            ),
            metadata = mapOf(
                "memory" to CognitiveContextValueMetadata(
                    relevance = 0.8
                )
            )
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.8
            )
        )

        assertEquals(
            mapOf("memory" to "relevant"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun selector_should_reject_value_when_relevance_is_below_threshold() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf(
                "memory" to "weak"
            ),
            metadata = mapOf(
                "memory" to CognitiveContextValueMetadata(
                    relevance = 0.4
                )
            )
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.5
            )
        )

        assertTrue(result.values.isEmpty())
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun selector_should_apply_importance_threshold() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = mapOf(
                "important" to "keep",
                "unimportant" to "drop"
            ),
            metadata = mapOf(
                "important" to CognitiveContextValueMetadata(
                    importance = 0.9
                ),
                "unimportant" to CognitiveContextValueMetadata(
                    importance = 0.3
                )
            )
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumImportance = 0.7
            )
        )

        assertEquals(
            mapOf("important" to "keep"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun selector_should_apply_confidence_threshold() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.PROCESSOR,
            values = mapOf(
                "trusted" to "keep",
                "uncertain" to "drop"
            ),
            metadata = mapOf(
                "trusted" to CognitiveContextValueMetadata(
                    confidence = 0.95
                ),
                "uncertain" to CognitiveContextValueMetadata(
                    confidence = 0.4
                )
            )
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumConfidence = 0.8
            )
        )

        assertEquals(
            mapOf("trusted" to "keep"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun selector_should_require_all_configured_thresholds() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.SESSION,
            values = mapOf(
                "fullyQualified" to "keep",
                "lowRelevance" to "drop",
                "lowImportance" to "drop",
                "lowConfidence" to "drop"
            ),
            metadata = mapOf(
                "fullyQualified" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
                    confidence = 0.9
                ),
                "lowRelevance" to CognitiveContextValueMetadata(
                    relevance = 0.4,
                    importance = 0.9,
                    confidence = 0.9
                ),
                "lowImportance" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.4,
                    confidence = 0.9
                ),
                "lowConfidence" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
                    confidence = 0.4
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
            mapOf("fullyQualified" to "keep"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(3, result.rejectedCount)
    }

    @Test
    fun selector_should_preserve_value_order() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = linkedMapOf(
                "first" to "A",
                "second" to "B",
                "third" to "C"
            ),
            metadata = mapOf(
                "first" to CognitiveContextValueMetadata(
                    relevance = 0.9
                ),
                "second" to CognitiveContextValueMetadata(
                    relevance = 0.3
                ),
                "third" to CognitiveContextValueMetadata(
                    relevance = 0.8
                )
            )
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.7
            )
        )

        assertEquals(
            listOf("first", "third"),
            result.values.keys.toList()
        )
    }

    @Test
    fun selector_should_not_mutate_snapshot_metadata() {
        val selector = DefaultCognitiveContextSelector()

        val metadata = mapOf(
            "value" to CognitiveContextValueMetadata(
                relevance = 0.9,
                importance = 0.8,
                confidence = 0.7
            )
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.GLOBAL,
            values = mapOf("value" to "payload"),
            metadata = metadata
        )

        selector.select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.5
            )
        )

        assertEquals(metadata, snapshot.metadata)
    }
}
