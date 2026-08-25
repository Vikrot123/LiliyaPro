package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextProcessLifecycleIsolationContractTest {

    @Test
    fun second_process_should_not_inherit_sources_from_first_process() {
        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("A"),
                source("B")
            )
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("C")
            )
        )

        assertEquals(
            listOf("source_0", "source_1"),
            first.values.keys.toList()
        )

        assertEquals(
            listOf("source_0"),
            second.values.keys.toList()
        )

        assertEquals(
            "A",
            snapshotValue(first.values["source_0"])
        )

        assertEquals(
            "B",
            snapshotValue(first.values["source_1"])
        )

        assertEquals(
            "C",
            snapshotValue(second.values["source_0"])
        )
    }

    @Test
    fun changing_sources_between_processes_should_replace_previous_source_set() {
        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("OLD_A"),
                source("OLD_B"),
                source("OLD_C")
            )
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("NEW_X"),
                source("NEW_Y")
            )
        )

        assertEquals(
            listOf("source_0", "source_1"),
            result.values.keys.toList()
        )

        assertEquals(
            "NEW_X",
            snapshotValue(result.values["source_0"])
        )

        assertEquals(
            "NEW_Y",
            snapshotValue(result.values["source_1"])
        )

        assertEquals(
            false,
            result.values.values.any {
                snapshotValue(it) in setOf(
                    "OLD_A",
                    "OLD_B",
                    "OLD_C"
                )
            }
        )
    }

    @Test
    fun previous_result_should_remain_stable_after_next_process() {
        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("FIRST_A"),
                source("FIRST_B")
            )
        )

        val firstKeys = first.values.keys.toList()
        val firstValues = first.values.mapValues {
            snapshotValue(it.value)
        }
        val firstSelectedCount = first.selectedCount
        val firstRejectedCount = first.rejectedCount

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("SECOND_ONLY")
            )
        )

        assertEquals(
            firstKeys,
            first.values.keys.toList()
        )

        assertEquals(
            firstValues,
            first.values.mapValues {
                snapshotValue(it.value)
            }
        )

        assertEquals(
            firstSelectedCount,
            first.selectedCount
        )

        assertEquals(
            firstRejectedCount,
            first.rejectedCount
        )
    }

    @Test
    fun process_with_empty_sources_should_not_retain_previous_values() {
        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("PERSISTED")
            )
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = emptyList()
        )

        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )

        assertEquals(
            0,
            result.selectedCount
        )

        assertEquals(
            0,
            result.rejectedCount
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
                    values = mapOf(
                        "payload" to value
                    )
                )
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
