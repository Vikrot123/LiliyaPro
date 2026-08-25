package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelector

class CognitiveContextPipelineCriteriaPropagationContractTest {

    @Test
    fun pipeline_should_forward_selection_criteria_unchanged_to_selector() {
        var observedCriteria: CognitiveContextSelectionCriteria? = null

        val selector = object : CognitiveContextSelector {
            override fun select(
                snapshot: CognitiveContextSnapshot,
                criteria: CognitiveContextSelectionCriteria
            ): CognitiveContextSelection {
                observedCriteria = criteria

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

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("value" to "payload")
                )
            }
        }

        val criteria = CognitiveContextSelectionCriteria(
            minimumRelevance = 0.73,
            minimumImportance = 0.61,
            minimumConfidence = 0.87,
            maximumAgeMillis = 12_345L
        )

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = criteria
        )

        assertSame(criteria, observedCriteria)
        assertEquals(criteria, observedCriteria)
    }

    @Test
    fun pipeline_should_forward_default_criteria_when_none_are_supplied() {
        var observedCriteria: CognitiveContextSelectionCriteria? = null

        val selector = object : CognitiveContextSelector {
            override fun select(
                snapshot: CognitiveContextSnapshot,
                criteria: CognitiveContextSelectionCriteria
            ): CognitiveContextSelection {
                observedCriteria = criteria

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

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = emptyList()
        )

        assertEquals(
            CognitiveContextSelectionCriteria(),
            observedCriteria
        )
    }
}
