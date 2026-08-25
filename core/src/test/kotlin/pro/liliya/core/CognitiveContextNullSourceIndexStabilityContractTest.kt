package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextNullSourceIndexStabilityContractTest {

    @Test
    fun null_snapshot_should_not_block_later_sources() {
        val result = pipeline().process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("FIRST"),
                nullSource(),
                source("THIRD")
            )
        )

        assertEquals(
            listOf("source_0", "source_2"),
            result.values.keys.toList()
        )

        assertEquals(
            "FIRST",
            snapshotValue(result.values["source_0"])
        )

        assertEquals(
            "THIRD",
            snapshotValue(result.values["source_2"])
        )

        assertEquals(2, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun null_snapshot_should_not_renumber_following_sources() {
        val result = pipeline().process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("A"),
                nullSource(),
                source("C"),
                source("D")
            )
        )

        assertEquals(
            listOf("source_0", "source_2", "source_3"),
            result.values.keys.toList()
        )

        assertTrue("source_1" !in result.values)

        assertEquals("A", snapshotValue(result.values["source_0"]))
        assertEquals("C", snapshotValue(result.values["source_2"]))
        assertEquals("D", snapshotValue(result.values["source_3"]))
    }

    @Test
    fun multiple_null_snapshots_should_preserve_all_successful_positions() {
        val result = pipeline().process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                nullSource(),
                source("B"),
                nullSource(),
                source("D"),
                nullSource(),
                source("F")
            )
        )

        assertEquals(
            listOf("source_1", "source_3", "source_5"),
            result.values.keys.toList()
        )

        assertEquals("B", snapshotValue(result.values["source_1"]))
        assertEquals("D", snapshotValue(result.values["source_3"]))
        assertEquals("F", snapshotValue(result.values["source_5"]))

        assertEquals(3, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun all_null_snapshots_should_produce_empty_selection() {
        val result = pipeline().process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                nullSource(),
                nullSource(),
                nullSource()
            )
        )

        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )

        assertEquals(0, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun repeated_null_source_selection_should_remain_stable() {
        val sources = listOf(
            source("A"),
            nullSource(),
            source("C"),
            nullSource(),
            source("E")
        )

        val first = pipeline().process(
            type = CognitiveContextType.TASK,
            sources = sources
        )

        val second = pipeline().process(
            type = CognitiveContextType.TASK,
            sources = sources
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
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
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

    private fun nullSource(): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot? {
                return null
            }
        }
    }

    private fun snapshotValue(value: Any?): Any? {
        return (value as? CognitiveContextSnapshot)
            ?.values
            ?.get("payload")
    }
}
