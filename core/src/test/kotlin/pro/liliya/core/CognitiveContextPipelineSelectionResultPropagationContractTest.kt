package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelector

class CognitiveContextPipelineSelectionResultPropagationContractTest {

    @Test
    fun pipeline_should_return_selector_values_unchanged() {
        val selectedValues = linkedMapOf<String, Any?>(
            "selected" to "payload"
        )

        val selector = object : CognitiveContextSelector {
            override fun select(
                snapshot: CognitiveContextSnapshot,
                criteria: CognitiveContextSelectionCriteria
            ): CognitiveContextSelection {
                return CognitiveContextSelection(
                    values = selectedValues,
                    selectedCount = 1,
                    rejectedCount = 2
                )
            }
        }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = selector
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = emptyList()
        )

        assertEquals(selectedValues, result.values)
    }

    @Test
    fun pipeline_should_return_selector_counts_unchanged() {
        val selector = object : CognitiveContextSelector {
            override fun select(
                snapshot: CognitiveContextSnapshot,
                criteria: CognitiveContextSelectionCriteria
            ): CognitiveContextSelection {
                return CognitiveContextSelection(
                    values = mapOf("selected" to "payload"),
                    selectedCount = 7,
                    rejectedCount = 11
                )
            }
        }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = selector
        )

        val result = pipeline.process(
            type = CognitiveContextType.WORKING,
            sources = emptyList()
        )

        assertEquals(7, result.selectedCount)
        assertEquals(11, result.rejectedCount)
    }

    @Test
    fun pipeline_should_return_empty_selector_result_without_rewriting_counts() {
        val selector = object : CognitiveContextSelector {
            override fun select(
                snapshot: CognitiveContextSnapshot,
                criteria: CognitiveContextSelectionCriteria
            ): CognitiveContextSelection {
                return CognitiveContextSelection(
                    values = emptyMap(),
                    selectedCount = 0,
                    rejectedCount = 3
                )
            }
        }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = selector
        )

        val result = pipeline.process(
            type = CognitiveContextType.SESSION,
            sources = emptyList()
        )

        assertEquals(emptyMap(), result.values)
        assertEquals(0, result.selectedCount)
        assertEquals(3, result.rejectedCount)
    }
}
