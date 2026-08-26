package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipelineResult
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrationResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeExperienceKnowledgeOrchestratorContractTest {

    private val experience =
        RuntimeExperience(
            description = "orchestration experience",
            meaning = RuntimeMeaningResult(
                interpretation = "orchestration meaning",
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.WARNING,
                generatedAt = 1L
            ),
            importance = RuntimeExperienceImportance.MEDIUM,
            createdAt = 1L
        )

    private val pipelineResult =
        RuntimeExperienceKnowledgePipelineResult(
            experienceResult = RuntimeExperiencePipelineResult(
                experience = experience,
                decision = RuntimeExperienceDecision(
                    shouldRemember = true,
                    reason = "Useful for orchestration contract"
                )
            ),
            consolidation = null,
            knowledgeResult = null
        )

    @Test
    fun orchestration_result_preserves_pipeline_result() {
        val result =
            RuntimeExperienceKnowledgeOrchestrationResult(
                pipelineResult = pipelineResult
            )

        assertSame(
            pipelineResult,
            result.pipelineResult
        )
    }
}
