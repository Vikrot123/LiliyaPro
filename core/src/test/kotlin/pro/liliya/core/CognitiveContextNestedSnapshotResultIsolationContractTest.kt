package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextNestedSnapshotResultIsolationContractTest {

    @Test
    fun snapshots_from_consecutive_processes_should_be_independent() {
        val pipeline = pipeline()

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("FIRST"))
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("SECOND"))
        )

        val firstSnapshot = snapshot(first.values["source_0"])
        val secondSnapshot = snapshot(second.values["source_0"])

        assertNotSame(firstSnapshot, secondSnapshot)
        assertNotSame(firstSnapshot.values, secondSnapshot.values)

        assertEquals(
            "FIRST",
            firstSnapshot.values["payload"]
        )

        assertEquals(
            "SECOND",
            secondSnapshot.values["payload"]
        )
    }

    @Test
    fun later_process_should_not_replace_previous_nested_snapshot() {
        val pipeline = pipeline()

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("OLD_A"),
                source("OLD_B")
            )
        )

        val firstSnapshots = first.values.mapValues {
            snapshot(it.value)
        }

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("NEW_ONLY")
            )
        )

        assertEquals(
            "OLD_A",
            firstSnapshots["source_0"]?.values?.get("payload")
        )

        assertEquals(
            "OLD_B",
            firstSnapshots["source_1"]?.values?.get("payload")
        )

        assertEquals(
            "OLD_A",
            snapshot(first.values["source_0"]).values["payload"]
        )

        assertEquals(
            "OLD_B",
            snapshot(first.values["source_1"]).values["payload"]
        )
    }

    @Test
    fun multiple_sources_should_receive_distinct_nested_snapshots() {
        val pipeline = pipeline()

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(
                source("A"),
                source("B"),
                source("C")
            )
        )

        val first = snapshot(result.values["source_0"])
        val second = snapshot(result.values["source_1"])
        val third = snapshot(result.values["source_2"])

        assertNotSame(first, second)
        assertNotSame(second, third)
        assertNotSame(first, third)

        assertNotSame(first.values, second.values)
        assertNotSame(second.values, third.values)
        assertNotSame(first.values, third.values)

        assertEquals("A", first.values["payload"])
        assertEquals("B", second.values["payload"])
        assertEquals("C", third.values["payload"])
    }

    @Test
    fun repeated_processes_should_not_reuse_nested_snapshot_instances() {
        val pipeline = pipeline()

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("ONE"))
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("TWO"))
        )

        val third = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("THREE"))
        )

        val firstSnapshot = snapshot(first.values["source_0"])
        val secondSnapshot = snapshot(second.values["source_0"])
        val thirdSnapshot = snapshot(third.values["source_0"])

        assertNotSame(firstSnapshot, secondSnapshot)
        assertNotSame(secondSnapshot, thirdSnapshot)
        assertNotSame(firstSnapshot, thirdSnapshot)

        assertEquals("ONE", firstSnapshot.values["payload"])
        assertEquals("TWO", secondSnapshot.values["payload"])
        assertEquals("THREE", thirdSnapshot.values["payload"])
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

    private fun snapshot(value: Any?): CognitiveContextSnapshot {
        return value as CognitiveContextSnapshot
    }
}
