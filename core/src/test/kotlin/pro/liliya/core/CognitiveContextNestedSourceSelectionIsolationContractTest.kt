package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextNestedSourceSelectionIsolationContractTest {

    @Test
    fun nested_source_metadata_should_not_control_aggregate_selection() {
        val nestedMetadata = CognitiveContextValueMetadata(
            relevance = 0.1,
            importance = 0.1,
            confidence = 0.1
        )

        val nestedSnapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK,
            values = mapOf(
                "memory" to "payload"
            ),
            metadata = mapOf(
                "memory" to nestedMetadata
            )
        )

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return nestedSnapshot
            }
        }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.9,
                minimumImportance = 0.9,
                minimumConfidence = 0.9
            )
        )

        assertEquals(
            1,
            result.selectedCount
        )

        assertEquals(
            0,
            result.rejectedCount
        )

        assertTrue(
            result.values.containsKey("source_0")
        )

        val selectedNestedSnapshot =
            result.values["source_0"] as CognitiveContextSnapshot

        assertEquals(
            nestedSnapshot,
            selectedNestedSnapshot
        )

        assertEquals(
            nestedMetadata,
            selectedNestedSnapshot.metadata["memory"]
        )
    }

    @Test
    fun nested_source_metadata_should_not_be_promoted_to_aggregate_metadata() {
        val nestedMetadata = CognitiveContextValueMetadata(
            relevance = 0.1,
            importance = 0.1,
            confidence = 0.1
        )

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf(
                        "memory" to "payload"
                    ),
                    metadata = mapOf(
                        "memory" to nestedMetadata
                    )
                )
            }
        }

        var observedAggregate: CognitiveContextSnapshot? = null

        val observingSelector =
            object : pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelector {

                override fun select(
                    snapshot: CognitiveContextSnapshot,
                    criteria: CognitiveContextSelectionCriteria
                ): pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection {
                    observedAggregate = snapshot

                    return pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection(
                        values = snapshot.values,
                        selectedCount = snapshot.values.size,
                        rejectedCount = 0
                    )
                }
            }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = observingSelector
        )

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source)
        )

        val aggregate =
            observedAggregate
                ?: error("selector did not receive aggregate snapshot")

        assertTrue(
            aggregate.metadata.isEmpty()
        )

        val nested =
            aggregate.values["source_0"] as CognitiveContextSnapshot

        assertEquals(
            nestedMetadata,
            nested.metadata["memory"]
        )
    }
}
