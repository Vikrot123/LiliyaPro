package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipelineResult

class RuntimeKnowledgePipelineContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "Consolidated runtime knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun result_preserves_knowledge() {
        val knowledge = knowledge()

        val result = RuntimeKnowledgePipelineResult(
            knowledge = knowledge
        )

        assertEquals(
            knowledge,
            result.knowledge
        )
    }

    @Test
    fun consolidation_model_remains_independent_from_result() {
        val consolidation = RuntimeExperienceConsolidation(
            summary = "Consolidated 2 runtime experiences",
            processedCount = 2,
            createdAt = 1L
        )

        val knowledge = knowledge()

        val result = RuntimeKnowledgePipelineResult(
            knowledge = knowledge
        )

        assertEquals(
            "Consolidated 2 runtime experiences",
            consolidation.summary
        )
        assertEquals(
            2,
            consolidation.processedCount
        )
        assertEquals(
            knowledge,
            result.knowledge
        )
    }
}
