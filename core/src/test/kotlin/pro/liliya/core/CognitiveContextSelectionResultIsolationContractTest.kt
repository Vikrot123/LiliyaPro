package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionResultIsolationContractTest {

    @Test
    fun consecutive_process_results_should_have_independent_value_maps() {
        val pipeline = pipeline()

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("FIRST")
            )
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("SECOND")
            )
        )

        assertNotSame(first.values, second.values)

        assertEquals(
            "FIRST",
            snapshotValue(first.values["source_0"])
        )

        assertEquals(
            "SECOND",
            snapshotValue(second.values["source_0"])
        )
    }

    @Test
    fun later_process_should_not_replace_previous_selection_values() {
        val pipeline = pipeline()

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("OLD_A"),
                source("OLD_B")
            )
        )

        val firstValues = first.values.mapValues {
            snapshotValue(it.value)
        }

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("NEW_ONLY")
            )
        )

        assertEquals(
            firstValues,
            first.values.mapValues {
                snapshotValue(it.value)
            }
        )

        assertEquals(
            listOf("source_0", "source_1"),
            first.values.keys.toList()
        )
    }

    @Test
    fun selection_results_should_remain_independent_when_process_shapes_differ() {
        val pipeline = pipeline()

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("A"),
                source("B"),
                source("C")
            )
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("X")
            )
        )

        assertNotSame(first.values, second.values)

        assertEquals(
            listOf("source_0", "source_1", "source_2"),
            first.values.keys.toList()
        )

        assertEquals(
            listOf("source_0"),
            second.values.keys.toList()
        )

        assertEquals(
            mapOf(
                "source_0" to "A",
                "source_1" to "B",
                "source_2" to "C"
            ),
            first.values.mapValues {
                snapshotValue(it.value)
            }
        )

        assertEquals(
            mapOf(
                "source_0" to "X"
            ),
            second.values.mapValues {
                snapshotValue(it.value)
            }
        )
    }

    @Test
    fun repeated_process_results_should_not_share_selection_state() {
        val pipeline = pipeline()

        val results = (1..3).map { index ->
            pipeline.process(
                type = CognitiveContextType.TASK,
                sources = listOf(
                    source("VALUE_$index")
                )
            )
        }

        assertNotSame(results[0].values, results[1].values)
        assertNotSame(results[1].values, results[2].values)
        assertNotSame(results[0].values, results[2].values)

        assertEquals(
            "VALUE_1",
            snapshotValue(results[0].values["source_0"])
        )

        assertEquals(
            "VALUE_2",
            snapshotValue(results[1].values["source_0"])
        )

        assertEquals(
            "VALUE_3",
            snapshotValue(results[2].values["source_0"])
        )
    }

    @Test
    fun empty_selection_result_should_not_share_values_with_later_selection() {
        val pipeline = pipeline()

        val empty = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = emptyList()
        )

        val populated = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("POPULATED")
            )
        )

        assertNotSame(empty.values, populated.values)

        assertEquals(
            emptyMap<String, Any?>(),
            empty.values
        )

        assertEquals(
            "POPULATED",
            snapshotValue(populated.values["source_0"])
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

    private fun snapshotValue(value: Any?): Any? {
        return (value as? CognitiveContextSnapshot)
            ?.values
            ?.get("payload")
    }
}
