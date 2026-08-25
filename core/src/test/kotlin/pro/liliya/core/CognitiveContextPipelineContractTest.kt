package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelector
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextPipelineContractTest {

    @Test
    fun pipeline_should_build_and_select_context() {
        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("value" to "test")
                )
            }
        }

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source)
        )

        assertNotNull(result)
        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
        assertNotNull(result.values["source_0"])
    }

    @Test
    fun pipeline_should_preserve_requested_context_type() {
        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        var observedType: CognitiveContextType? = null

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                observedType = type
                return CognitiveContextSnapshot(type)
            }
        }

        pipeline.process(
            type = CognitiveContextType.PROCESSOR,
            sources = listOf(source)
        )

        assertEquals(
            CognitiveContextType.PROCESSOR,
            observedType
        )
    }

    @Test
    fun pipeline_should_handle_empty_sources() {
        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val result = pipeline.process(
            type = CognitiveContextType.WORKING,
            sources = emptyList()
        )

        assertTrue(result.values.isEmpty())
        assertEquals(0, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun pipeline_should_pass_selection_criteria_to_selector() {
        var receivedCriteria: CognitiveContextSelectionCriteria? = null

        val selector = object : CognitiveContextSelector {

            override fun select(
                snapshot: CognitiveContextSnapshot,
                criteria: CognitiveContextSelectionCriteria
            ): CognitiveContextSelection {
                receivedCriteria = criteria

                return CognitiveContextSelection(
                    values = snapshot.values,
                    selectedCount = snapshot.values.size,
                    rejectedCount = 0
                )
            }
        }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = selector
        )

        val criteria = CognitiveContextSelectionCriteria(
            minimumRelevance = 0.8,
            minimumImportance = 0.7,
            minimumConfidence = 0.9,
            maximumAgeMillis = 30_000L
        )

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = emptyList(),
            criteria = criteria
        )

        assertSame(
            criteria,
            receivedCriteria
        )
    }

    @Test
    fun separate_pipelines_should_not_share_state() {
        val first = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val second = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = DefaultCognitiveContextSelector()
        )

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("value" to "first")
                )
            }
        }

        val firstResult = first.process(
            CognitiveContextType.TASK,
            listOf(source)
        )

        val secondResult = second.process(
            CognitiveContextType.TASK,
            emptyList()
        )

        assertEquals(1, firstResult.selectedCount)
        assertEquals(0, secondResult.selectedCount)
    }
}
