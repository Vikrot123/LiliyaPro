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

class CognitiveContextPipelineSelectionSemanticsContractTest {

    @Test
    fun pipeline_should_apply_age_to_aggregate_snapshot() {
        val now = 100_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = selector
        )

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("value" to "payload"),
                    timestamp = now
                )
            }
        }

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 0L
            )
        )

        assertEquals(
            mapOf("source_0" to source.snapshot(CognitiveContextType.TASK)),
            result.values
        )
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun pipeline_should_reject_stale_aggregate_snapshot() {
        val now = 100_000L

        val selector = DefaultCognitiveContextSelector(
            nowProvider = { now }
        )

        val staleBuilder = object : pro.liliya.core.runtime.intelligence.context.cognitive.builder.CognitiveContextBuilder {
            override fun build(
                type: CognitiveContextType,
                sources: List<CognitiveContextSource>
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("source_0" to "payload"),
                    timestamp = 99_999L
                )
            }
        }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = staleBuilder,
            selector = selector
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = emptyList(),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 0L
            )
        )

        assertTrue(result.values.isEmpty())
        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
    }

    @Test
    fun pipeline_should_not_promote_source_metadata_to_aggregate_metadata() {
        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val metadata = CognitiveContextValueMetadata(
            relevance = 0.9,
            importance = 0.9,
            confidence = 0.9
        )

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("memory" to "important"),
                    metadata = mapOf("memory" to metadata)
                )
            }
        }

        var observedSnapshot: CognitiveContextSnapshot? = null

        val observingSelector =
            object : pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelector {

                override fun select(
                    snapshot: CognitiveContextSnapshot,
                    criteria: CognitiveContextSelectionCriteria
                ): pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection {
                    observedSnapshot = snapshot

                    return pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection(
                        values = snapshot.values,
                        selectedCount = snapshot.values.size,
                        rejectedCount = 0
                    )
                }
            }

        val observingPipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = observingSelector
        )

        observingPipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source)
        )

        val aggregate = observedSnapshot
            ?: error("selector did not receive aggregate snapshot")

        assertTrue(aggregate.metadata.isEmpty())

        val nested =
            aggregate.values["source_0"] as CognitiveContextSnapshot

        assertEquals(
            metadata,
            nested.metadata["memory"]
        )
    }
}
