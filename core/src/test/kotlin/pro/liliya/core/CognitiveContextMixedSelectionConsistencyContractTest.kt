package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.CognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextMixedSelectionConsistencyContractTest {

    @Test
    fun mixed_selection_should_keep_counts_consistent_with_values() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("ACCEPTED"),
                source("REJECTED"),
                source("ACCEPTED_2")
            ),
            criteria = criteria()
        )

        assertEquals(2, result.selectedCount)
        assertEquals(1, result.rejectedCount)
        assertEquals(2, result.values.size)

        assertEquals(
            setOf("source_0", "source_2"),
            result.values.keys
        )

        assertEquals(
            "ACCEPTED",
            snapshotValue(result.values["source_0"])
        )

        assertEquals(
            "ACCEPTED_2",
            snapshotValue(result.values["source_2"])
        )

        assertEquals(
            result.selectedCount + result.rejectedCount,
            3
        )
    }

    @Test
    fun all_rejected_selection_should_have_no_values() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("REJECTED_A"),
                source("REJECTED_B")
            ),
            criteria = criteria()
        )

        assertEquals(0, result.selectedCount)
        assertEquals(2, result.rejectedCount)
        assertEquals(emptyMap<String, Any?>(), result.values)
    }

    @Test
    fun all_accepted_selection_should_have_no_rejections() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("ACCEPTED_A"),
                source("ACCEPTED_B")
            ),
            criteria = criteria()
        )

        assertEquals(2, result.selectedCount)
        assertEquals(0, result.rejectedCount)
        assertEquals(2, result.values.size)
    }

    @Test
    fun repeated_mixed_selection_should_remain_consistent() {
        val pipeline = pipeline()
        val criteria = criteria()

        val sources = listOf(
            source("A"),
            source("B"),
            source("C")
        )

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = sources,
            criteria = criteria
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = sources,
            criteria = criteria
        )

        assertEquals(first.values.keys, second.values.keys)
        assertEquals(first.selectedCount, second.selectedCount)
        assertEquals(first.rejectedCount, second.rejectedCount)

        assertEquals(
            first.values.mapValues { snapshotValue(it.value) },
            second.values.mapValues { snapshotValue(it.value) }
        )
    }

    private fun pipeline(): DefaultCognitiveContextPipeline {
        return DefaultCognitiveContextPipeline(
            builder = AggregateMetadataBuilder(),
            selector = DefaultCognitiveContextSelector()
        )
    }

    private fun criteria(): CognitiveContextSelectionCriteria {
        return CognitiveContextSelectionCriteria(
            minimumRelevance = 0.8,
            minimumImportance = 0.8,
            minimumConfidence = 0.8
        )
    }

    private fun source(value: String): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("payload" to value)
                )
            }
        }
    }

    private class AggregateMetadataBuilder : CognitiveContextBuilder {

        override fun build(
            type: CognitiveContextType,
            sources: List<CognitiveContextSource>
        ): CognitiveContextSnapshot {
            val values = linkedMapOf<String, Any?>()
            val metadata =
                linkedMapOf<String, CognitiveContextValueMetadata>()

            sources.forEachIndexed { index, source ->
                val snapshot = source.snapshot(type)
                    ?: error("source returned null snapshot")

                val key = "source_$index"

                values[key] = snapshot

                val value = snapshot.values["payload"] as String

                val accepted = when (value) {
                    "ACCEPTED",
                    "ACCEPTED_2",
                    "ACCEPTED_A",
                    "ACCEPTED_B",
                    "A",
                    "C" -> true

                    else -> false
                }

                metadata[key] = if (accepted) {
                    CognitiveContextValueMetadata(
                        relevance = 1.0,
                        importance = 1.0,
                        confidence = 1.0
                    )
                } else {
                    CognitiveContextValueMetadata(
                        relevance = 0.1,
                        importance = 0.1,
                        confidence = 0.1
                    )
                }
            }

            return CognitiveContextSnapshot(
                type = type,
                values = values,
                metadata = metadata
            )
        }
    }

    private fun snapshotValue(
        value: Any?
    ): Any? {
        return (value as? CognitiveContextSnapshot)
            ?.values
            ?.get("payload")
    }
}
