package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionSpecialDoubleBoundaryContractTest {

    @Test
    fun positive_infinity_threshold_should_accept_finite_metadata_only_when_metadata_reaches_it() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = linkedMapOf(
                "finite" to "FINITE",
                "infinite" to "INFINITE"
            ),
            metadata = mapOf(
                "finite" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 1.0,
                    confidence = 1.0
                ),
                "infinite" to CognitiveContextValueMetadata(
                    relevance = Double.POSITIVE_INFINITY,
                    importance = Double.POSITIVE_INFINITY,
                    confidence = Double.POSITIVE_INFINITY
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = Double.POSITIVE_INFINITY,
                minimumImportance = Double.POSITIVE_INFINITY,
                minimumConfidence = Double.POSITIVE_INFINITY
            )
        )

        assertEquals(
            mapOf("infinite" to "INFINITE"),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun negative_infinity_threshold_should_accept_finite_metadata() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "PAYLOAD"),
            metadata = mapOf(
                "value" to CognitiveContextValueMetadata(
                    relevance = 0.0,
                    importance = 0.0,
                    confidence = 0.0
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = Double.NEGATIVE_INFINITY,
                minimumImportance = Double.NEGATIVE_INFINITY,
                minimumConfidence = Double.NEGATIVE_INFINITY
            )
        )

        assertEquals(snapshot.values, result.values)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun nan_threshold_should_not_accidentally_accept_value() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf("value" to "PAYLOAD"),
            metadata = mapOf(
                "value" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 1.0,
                    confidence = 1.0
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = Double.NaN
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
    fun nan_metadata_should_not_satisfy_threshold() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = linkedMapOf(
                "nan_relevance" to "R",
                "nan_importance" to "I",
                "nan_confidence" to "C"
            ),
            metadata = mapOf(
                "nan_relevance" to CognitiveContextValueMetadata(
                    relevance = Double.NaN,
                    importance = 1.0,
                    confidence = 1.0
                ),
                "nan_importance" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = Double.NaN,
                    confidence = 1.0
                ),
                "nan_confidence" to CognitiveContextValueMetadata(
                    relevance = 1.0,
                    importance = 1.0,
                    confidence = Double.NaN
                )
            )
        )

        val result = DefaultCognitiveContextSelector().select(
            snapshot = snapshot,
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.0,
                minimumImportance = 0.0,
                minimumConfidence = 0.0
            )
        )

        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )
        assertEquals(0, result.selectedCount)
        assertEquals(3, result.rejectedCount)
    }
}
