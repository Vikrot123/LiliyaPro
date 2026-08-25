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

class CognitiveContextSourceFailureIsolationContractTest {

    @Test
    fun failed_source_should_not_block_later_sources() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("FIRST"),
                failingSource(),
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
    fun failed_source_should_not_be_replaced_by_another_source_key() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("A"),
                failingSource(),
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
    fun multiple_failed_sources_should_not_destroy_successful_sources() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                failingSource(),
                source("B"),
                failingSource(),
                source("D"),
                failingSource()
            )
        )

        assertEquals(
            listOf("source_1", "source_3"),
            result.values.keys.toList()
        )

        assertEquals("B", snapshotValue(result.values["source_1"]))
        assertEquals("D", snapshotValue(result.values["source_3"]))

        assertEquals(2, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun all_failed_sources_should_produce_empty_selection() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                failingSource(),
                failingSource(),
                failingSource()
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
    fun source_failure_should_not_change_successful_source_snapshot() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("STABLE_A"),
                failingSource(),
                source("STABLE_C")
            )
        )

        val first = result.values["source_0"] as? CognitiveContextSnapshot
        val third = result.values["source_2"] as? CognitiveContextSnapshot

        assertEquals("STABLE_A", first?.values?.get("payload"))
        assertEquals("STABLE_C", third?.values?.get("payload"))
    }

    private fun pipeline(): DefaultCognitiveContextPipeline {
        return DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )
    }

    private fun source(
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

    private fun failingSource(): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                throw IllegalStateException("intentional cognitive source failure")
            }
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
