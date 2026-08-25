package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelection
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelector

class CognitiveContextPipelineSelectorFailurePropagationContractTest {

    @Test
    fun pipeline_should_propagate_selector_failure() {
        val failure = IllegalStateException("intentional selector failure")

        val selector = object : CognitiveContextSelector {
            override fun select(
                snapshot: CognitiveContextSnapshot,
                criteria: CognitiveContextSelectionCriteria
            ): CognitiveContextSelection {
                throw failure
            }
        }

        val pipeline = DefaultCognitiveContextPipeline(
            builder = DefaultCognitiveContextBuilder(),
            selector = selector
        )

        val observed = assertFailsWith<IllegalStateException> {
            pipeline.process(
                type = CognitiveContextType.TASK,
                sources = emptyList()
            )
        }

        assertSame(failure, observed)
    }
}
