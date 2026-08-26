package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrator

class RuntimeCompositionExperienceKnowledgeOrchestratorOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_experience_knowledge_orchestrator() {
        val composition = DefaultRuntimeComposition()

        val orchestrator =
            composition.experienceKnowledgeOrchestrator()

        assertSame(
            orchestrator,
            composition.experienceKnowledgeOrchestrator()
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_experience_knowledge_orchestrator() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.experienceKnowledgeOrchestrator(),
            second.experienceKnowledgeOrchestrator()
        )
    }

    @Test
    fun root_exposes_orchestrator_as_expected_contract_type() {
        val composition = DefaultRuntimeComposition()

        val orchestrator: RuntimeExperienceKnowledgeOrchestrator =
            composition.experienceKnowledgeOrchestrator()

        assertSame(
            orchestrator,
            composition.experienceKnowledgeOrchestrator()
        )
    }

    @Test
    fun root_orchestrator_is_stable_independently_of_pipeline_access() {
        val composition = DefaultRuntimeComposition()

        val pipeline: RuntimeExperienceKnowledgePipeline =
            composition.experienceKnowledgePipeline()

        val orchestrator =
            composition.experienceKnowledgeOrchestrator()

        assertSame(
            pipeline,
            composition.experienceKnowledgePipeline()
        )

        assertSame(
            orchestrator,
            composition.experienceKnowledgeOrchestrator()
        )
    }
}
