package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextMultiSourceSelectionStabilityContractTest {

    @Test
    fun multiple_sources_should_preserve_source_order_after_selection() {
        val sourceA = source(
            "A",
            CognitiveContextType.TASK
        )
        val sourceB = source(
            "B",
            CognitiveContextType.TASK
        )
        val sourceC = source(
            "C",
            CognitiveContextType.TASK
        )

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(sourceA, sourceB, sourceC)
        )

        assertEquals(
            listOf("source_0", "source_1", "source_2"),
            result.values.keys.toList()
        )

        assertEquals(
            "A",
            snapshotValue(result.values["source_0"])
        )

        assertEquals(
            "B",
            snapshotValue(result.values["source_1"])
        )

        assertEquals(
            "C",
            snapshotValue(result.values["source_2"])
        )
    }

    @Test
    fun rejected_source_should_not_renumber_remaining_source_keys() {
        val sourceA = source(
            "A",
            CognitiveContextType.TASK
        )
        val sourceB = source(
            "B",
            CognitiveContextType.TASK
        )
        val sourceC = source(
            "C",
            CognitiveContextType.TASK
        )

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(sourceA, sourceB, sourceC),
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.9,
                minimumImportance = 0.9,
                minimumConfidence = 0.9
            )
        )

        assertEquals(
            listOf("source_0", "source_1", "source_2"),
            result.values.keys.toList()
        )

        assertEquals(
            3,
            result.selectedCount
        )

        assertEquals(
            0,
            result.rejectedCount
        )
    }

    @Test
    fun nested_snapshots_should_remain_independent_between_sources() {
        val sourceA = source(
            "A",
            CognitiveContextType.TASK
        )
        val sourceB = source(
            "B",
            CognitiveContextType.TASK
        )

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(sourceA, sourceB)
        )

        val first =
            result.values["source_0"] as? CognitiveContextSnapshot

        val second =
            result.values["source_1"] as? CognitiveContextSnapshot

        assertNotNull(first)
        assertNotNull(second)

        assertNotSame(
            first.values,
            second.values
        )

        assertEquals(
            "A",
            first.values["payload"]
        )

        assertEquals(
            "B",
            second.values["payload"]
        )
    }

    @Test
    fun repeated_multi_source_selection_should_return_stable_results() {
        val sources = listOf(
            source("A", CognitiveContextType.TASK),
            source("B", CognitiveContextType.TASK),
            source("C", CognitiveContextType.TASK)
        )

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = sources
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = sources
        )

        assertEquals(
            first.values.keys.toList(),
            second.values.keys.toList()
        )

        assertEquals(
            first.selectedCount,
            second.selectedCount
        )

        assertEquals(
            first.rejectedCount,
            second.rejectedCount
        )

        assertEquals(
            first.values.mapValues { snapshotValue(it.value) },
            second.values.mapValues { snapshotValue(it.value) }
        )
    }

    private fun source(
        value: String,
        type: CognitiveContextType
    ): CognitiveContextSource {
        return object : CognitiveContextSource {

            override fun snapshot(
                requestedType: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = requestedType,
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
