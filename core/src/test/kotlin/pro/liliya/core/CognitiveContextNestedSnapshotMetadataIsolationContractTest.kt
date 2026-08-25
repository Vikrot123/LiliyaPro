package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.CognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextNestedSnapshotMetadataIsolationContractTest {

    @Test
    fun consecutive_processes_should_keep_nested_metadata_independent() {
        val pipeline = pipeline()

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("FIRST", 0.11))
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("SECOND", 0.22))
        )

        val firstSnapshot = snapshot(first.values["source_0"])
        val secondSnapshot = snapshot(second.values["source_0"])

        assertNotSame(firstSnapshot, secondSnapshot)
        assertNotSame(firstSnapshot.metadata, secondSnapshot.metadata)

        assertEquals(
            0.11,
            firstSnapshot.metadata["payload"]?.relevance
        )
        assertEquals(
            0.22,
            secondSnapshot.metadata["payload"]?.relevance
        )
    }

    @Test
    fun metadata_should_remain_bound_to_its_own_source() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("A", 0.31),
                source("B", 0.32),
                source("C", 0.33)
            )
        )

        val first = snapshot(result.values["source_0"])
        val second = snapshot(result.values["source_1"])
        val third = snapshot(result.values["source_2"])

        assertEquals("A", first.values["payload"])
        assertEquals("B", second.values["payload"])
        assertEquals("C", third.values["payload"])

        assertEquals(0.31, first.metadata["payload"]?.relevance)
        assertEquals(0.32, second.metadata["payload"]?.relevance)
        assertEquals(0.33, third.metadata["payload"]?.relevance)
    }

    @Test
    fun later_process_should_not_replace_previous_nested_metadata() {
        val pipeline = pipeline()

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("OLD_A", 0.41),
                source("OLD_B", 0.42)
            )
        )

        val firstMetadata = first.values.mapValues {
            snapshot(it.value).metadata["payload"]?.relevance
        }

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("NEW_ONLY", 0.51)
            )
        )

        assertEquals(
            firstMetadata,
            first.values.mapValues {
                snapshot(it.value).metadata["payload"]?.relevance
            }
        )

        assertEquals(
            0.41,
            snapshot(first.values["source_0"])
                .metadata["payload"]?.relevance
        )

        assertEquals(
            0.42,
            snapshot(first.values["source_1"])
                .metadata["payload"]?.relevance
        )
    }

    @Test
    fun selection_should_not_mutate_nested_snapshot_metadata() {
        val pipeline = pipeline()

        val source = source("STABLE", 0.61)

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.8,
                minimumImportance = 0.8,
                minimumConfidence = 0.8
            )
        )

        val selected = snapshot(result.values["source_0"])

        assertEquals(
            0.61,
            selected.metadata["payload"]?.relevance
        )

        assertEquals(
            CognitiveContextValueMetadata(
                relevance = 0.61,
                importance = 0.8,
                confidence = 0.7
            ),
            selected.metadata["payload"]
        )
    }

    @Test
    fun empty_nested_metadata_should_not_inherit_metadata_from_other_sources() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("WITH_META", 0.71),
                sourceWithoutMetadata("WITHOUT_META")
            )
        )

        val withMetadata = snapshot(result.values["source_0"])
        val withoutMetadata = snapshot(result.values["source_1"])

        assertEquals(
            0.71,
            withMetadata.metadata["payload"]?.relevance
        )

        assertEquals(
            emptyMap<String, CognitiveContextValueMetadata>(),
            withoutMetadata.metadata
        )
    }

    private fun pipeline(): DefaultCognitiveContextPipeline {
        return DefaultCognitiveContextPipeline(
            builder = MetadataPreservingBuilder(),
            selector = DefaultCognitiveContextSelector()
        )
    }

    private fun source(
        value: String,
        relevance: Double
    ): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("payload" to value),
                    metadata = mapOf(
                        "payload" to CognitiveContextValueMetadata(
                            relevance = relevance,
                            importance = 0.8,
                            confidence = 0.7
                        )
                    )
                )
            }
        }
    }

    private fun sourceWithoutMetadata(
        value: String
    ): CognitiveContextSource {
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

    private class MetadataPreservingBuilder : CognitiveContextBuilder {

        override fun build(
            type: CognitiveContextType,
            sources: List<CognitiveContextSource>
        ): CognitiveContextSnapshot {
            val values = linkedMapOf<String, Any?>()

            sources.forEachIndexed { index, source ->
                val snapshot = source.snapshot(type)
                    ?: error("source returned null snapshot")

                values["source_$index"] = snapshot
            }

            return CognitiveContextSnapshot(
                type = type,
                values = values
            )
        }
    }

    private fun snapshot(value: Any?): CognitiveContextSnapshot {
        return value as CognitiveContextSnapshot
    }
}
