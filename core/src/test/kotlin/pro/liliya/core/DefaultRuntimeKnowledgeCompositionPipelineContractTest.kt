package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.composition.DefaultRuntimeKnowledgeComposition
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline

class DefaultRuntimeKnowledgeCompositionPipelineContractTest {

    @Test
    fun composition_owns_stable_knowledge_pipeline() {
        val composition = DefaultRuntimeKnowledgeComposition()

        val pipeline = composition.knowledgePipeline()

        assertSame(
            pipeline,
            composition.knowledgePipeline()
        )
    }

    @Test
    fun composition_exposes_expected_pipeline_contract_type() {
        val composition = DefaultRuntimeKnowledgeComposition()

        val pipeline: RuntimeKnowledgePipeline =
            composition.knowledgePipeline()

        assertSame(
            pipeline,
            composition.knowledgePipeline()
        )
    }
}
