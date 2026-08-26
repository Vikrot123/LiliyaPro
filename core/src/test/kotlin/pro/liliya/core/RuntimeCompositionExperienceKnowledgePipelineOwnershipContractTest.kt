package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipeline

class RuntimeCompositionExperienceKnowledgePipelineOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_experience_knowledge_pipeline() {
        val composition = DefaultRuntimeComposition()

        val pipeline = composition.experienceKnowledgePipeline()

        assertSame(
            pipeline,
            composition.experienceKnowledgePipeline()
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_experience_knowledge_pipeline() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.experienceKnowledgePipeline(),
            second.experienceKnowledgePipeline()
        )
    }

    @Test
    fun root_exposes_expected_pipeline_contract_type() {
        val composition = DefaultRuntimeComposition()

        val pipeline: RuntimeExperienceKnowledgePipeline =
            composition.experienceKnowledgePipeline()

        assertSame(
            pipeline,
            composition.experienceKnowledgePipeline()
        )
    }

    @Test
    fun root_pipeline_uses_root_owned_experience_and_knowledge_components() {
        val composition = DefaultRuntimeComposition()

        val pipeline = composition.experienceKnowledgePipeline()

        assertSame(
            composition.experienceKnowledgePipeline(),
            pipeline
        )

        assertSame(
            composition.experienceComposition(),
            composition.experienceComposition()
        )

        assertSame(
            composition.knowledgeComposition(),
            composition.knowledgeComposition()
        )
    }
}
