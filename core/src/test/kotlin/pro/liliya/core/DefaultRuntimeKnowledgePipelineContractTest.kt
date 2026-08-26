package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeExtractor
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.DefaultRuntimeKnowledgePipeline

class DefaultRuntimeKnowledgePipelineContractTest {

    private val consolidation = RuntimeExperienceConsolidation(
        summary = "Consolidated runtime experiences",
        processedCount = 2,
        createdAt = 1L
    )

    private val knowledge = RuntimeKnowledge(
        statement = "Runtime learned from experience",
        confidence = 0.8,
        source = RuntimeKnowledgeSource.CONSOLIDATION,
        createdAt = 1L
    )

    private class FakeKnowledgeExtractor(
        private val result: RuntimeKnowledge
    ) : RuntimeKnowledgeExtractor {

        var calls = 0
        var received: RuntimeExperienceConsolidation? = null

        override fun extract(
            consolidation: RuntimeExperienceConsolidation
        ): RuntimeKnowledge {
            calls++
            received = consolidation
            return result
        }
    }

    @Test
    fun pipeline_extracts_knowledge_from_consolidation() {
        val extractor = FakeKnowledgeExtractor(knowledge)

        val pipeline = DefaultRuntimeKnowledgePipeline(
            knowledgeExtractor = extractor
        )

        val result = pipeline.process(consolidation)

        assertEquals(1, extractor.calls)
        assertSame(consolidation, extractor.received)
        assertSame(knowledge, result.knowledge)
    }

    @Test
    fun pipeline_preserves_extractor_result_without_modification() {
        val extracted = RuntimeKnowledge(
            statement = "Specific extracted knowledge",
            confidence = 0.95,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 2L
        )

        val extractor = FakeKnowledgeExtractor(extracted)

        val pipeline = DefaultRuntimeKnowledgePipeline(
            knowledgeExtractor = extractor
        )

        val result = pipeline.process(consolidation)

        assertSame(extracted, result.knowledge)
        assertEquals(
            "Specific extracted knowledge",
            result.knowledge.statement
        )
        assertEquals(
            0.95,
            result.knowledge.confidence
        )
    }
}
