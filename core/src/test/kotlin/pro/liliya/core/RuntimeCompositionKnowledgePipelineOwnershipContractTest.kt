package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline

class RuntimeCompositionKnowledgePipelineOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_knowledge_pipeline() {
        val composition = DefaultRuntimeComposition()

        val pipeline = composition
            .knowledgeComposition()
            .knowledgePipeline()

        assertSame(
            pipeline,
            composition
                .knowledgeComposition()
                .knowledgePipeline()
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_knowledge_pipeline() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.knowledgeComposition().knowledgePipeline(),
            second.knowledgeComposition().knowledgePipeline()
        )
    }

    @Test
    fun root_exposes_pipeline_as_expected_contract_type() {
        val composition = DefaultRuntimeComposition()

        val pipeline: RuntimeKnowledgePipeline =
            composition
                .knowledgeComposition()
                .knowledgePipeline()

        assertSame(
            pipeline,
            composition
                .knowledgeComposition()
                .knowledgePipeline()
        )
    }
}
