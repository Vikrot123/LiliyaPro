package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.knowledge.DefaultRuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipeline
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.experience.orchestration.DefaultRuntimeExperienceKnowledgeOrchestrator
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipelineResult

class DefaultRuntimeExperienceKnowledgeOrchestratorContractTest {

    private val context =
        RuntimeExperienceContext(
            selfModel = pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel(
                snapshot = RuntimeContextSnapshot(
                    runtimeState = "RUNNING",
                    activeServices = listOf("runtime"),
                    timestamp = 1L
                ),
                metadata = RuntimeContextMetadata(
                    runtimeVersion = "1",
                    recoveryAvailable = true,
                    diagnosticsAvailable = true
                )
            ),
            meaning = RuntimeMeaningResult(
                interpretation = "orchestration meaning",
                confidence = 1.0,
                significance = RuntimeMeaningSignificance.WARNING,
                generatedAt = 1L
            )
        )

    private val experience =
        RuntimeExperience(
            description = "orchestration experience",
            meaning = context.meaning,
            importance = RuntimeExperienceImportance.MEDIUM,
            createdAt = 1L
        )

    private val pipelineResult =
        RuntimeExperienceKnowledgePipelineResult(
            experienceResult = RuntimeExperiencePipelineResult(
                experience = experience,
                decision = RuntimeExperienceDecision(
                    shouldRemember = true,
                    reason = "Useful for orchestration"
                )
            ),
            consolidation = null,
            knowledgeResult = null
        )

    private class FakePipeline(
        private val result: RuntimeExperienceKnowledgePipelineResult
    ) : RuntimeExperienceKnowledgePipeline {

        var calls = 0
        var received: RuntimeExperienceContext? = null

        override fun process(
            context: RuntimeExperienceContext
        ): RuntimeExperienceKnowledgePipelineResult {
            calls++
            received = context
            return result
        }
    }

    @Test
    fun orchestrator_delegates_context_to_pipeline() {
        val pipeline = FakePipeline(pipelineResult)

        val orchestrator =
            DefaultRuntimeExperienceKnowledgeOrchestrator(
                pipeline = pipeline
            )

        orchestrator.process(context)

        assertEquals(1, pipeline.calls)
        assertSame(context, pipeline.received)
    }

    @Test
    fun orchestrator_preserves_pipeline_result_without_modification() {
        val pipeline = FakePipeline(pipelineResult)

        val orchestrator =
            DefaultRuntimeExperienceKnowledgeOrchestrator(
                pipeline = pipeline
            )

        val result = orchestrator.process(context)

        assertSame(pipelineResult, result.pipelineResult)
    }
}
