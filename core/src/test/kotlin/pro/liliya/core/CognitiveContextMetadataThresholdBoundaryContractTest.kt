package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextMetadataThresholdBoundaryContractTest {

    @Test
    fun exact_threshold_should_accept_value() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = snapshot(
            relevance = 0.8,
            importance = 0.8,
            confidence = 0.8
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertEquals(snapshot.values, result.values)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun relevance_just_below_threshold_should_reject_value() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = snapshot(
            relevance = 0.799999,
            importance = 1.0,
            confidence = 1.0
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertEquals(emptyMap<String, Any?>(), result.values)
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun importance_just_below_threshold_should_reject_value() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = snapshot(
            relevance = 1.0,
            importance = 0.799999,
            confidence = 1.0
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertEquals(emptyMap<String, Any?>(), result.values)
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun confidence_just_below_threshold_should_reject_value() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = snapshot(
            relevance = 1.0,
            importance = 1.0,
            confidence = 0.799999
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertEquals(emptyMap<String, Any?>(), result.values)
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun one_failed_dimension_should_not_be_compensated_by_other_dimensions() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = linkedMapOf(
                "relevance_fail" to "R",
                "importance_fail" to "I",
                "confidence_fail" to "C",
                "accepted" to "OK"
            ),
            metadata = mapOf(
                "relevance_fail" to CognitiveContextValueMetadata(
                    relevance = 0.799999,
                    importance = 1.0,
                    confidence = 1.0
                ),
                "importance_fail" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 0.799999,
                    confidence = 1.0
                ),
                "confidence_fail" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 1.0,
                    confidence = 0.799999
                ),
                "accepted" to CognitiveContextValueMetadata(
                    relevance = 0.8,
                    importance = 0.8,
                    confidence = 0.8
                )
            )
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertEquals(
            mapOf("accepted" to "OK"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(3, result.rejectedCount)
    }

    @Test
    fun mixed_threshold_results_should_preserve_source_order() {
        val selector = DefaultCognitiveContextSelector()

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = linkedMapOf(
                "first" to "FIRST",
                "second" to "SECOND",
                "third" to "THIRD"
            ),
            metadata = mapOf(
                "first" to CognitiveContextValueMetadata(
                    relevance = 0.8,
                    importance = 0.8,
                    confidence = 0.8
                ),
                "second" to CognitiveContextValueMetadata(
                    relevance = 0.7,
                    importance = 1.0,
                    confidence = 1.0
                ),
                "third" to CognitiveContextValueMetadata(
                    relevance = 0.9,
                    importance = 0.9,
                    confidence = 0.9
                )
            )
        )

        val result = selector.select(
            snapshot = snapshot,
            criteria = criteria()
        )

        assertEquals(
            listOf("first", "third"),
            result.values.keys.toList()
        )
        assertEquals(
            mapOf(
                "first" to "FIRST",
                "third" to "THIRD"
            ),
            result.values
        )
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

    private fun snapshot(
        relevance: Double,
        importance: Double,
        confidence: Double
    ): CognitiveContextSnapshot {
        return CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf(
                "value" to "payload"
            ),
            metadata = mapOf(
                "value" to CognitiveContextValueMetadata(
                    relevance = relevance,
                    importance = importance,
                    confidence = confidence
                )
            )
        )
    }
}
