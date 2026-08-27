package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionEngine
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrationResult
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipelineResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeDecisionEngineContractTest {

    private fun intelligence(
        significance: RuntimeMeaningSignificance,
        confidence: Double = 0.9,
        knowledgeSelection: RuntimeKnowledgeSelectionResult? = null
    ): RuntimeIntelligenceOrchestrationResult {

        val meaning = RuntimeMeaningResult(
            interpretation = "test",
            confidence = confidence,
            significance = significance,
            generatedAt = 1L,
            knowledgeSelection = knowledgeSelection
        )

        val experience = RuntimeExperience(
            description = "test experience",
            meaning = meaning,
            importance = RuntimeExperienceImportance.MEDIUM,
            createdAt = 1L
        )

        val experiencePipelineResult = RuntimeExperiencePipelineResult(
            experience = experience,
            decision = RuntimeExperienceDecision(
                shouldRemember = true,
                reason = "test"
            )
        )

        val experienceKnowledgePipelineResult =
            RuntimeExperienceKnowledgePipelineResult(
                experienceResult = experiencePipelineResult,
                consolidation = null,
                knowledgeResult = null
            )

        return RuntimeIntelligenceOrchestrationResult(
            selfModel = RuntimeSelfModel(
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
            reflection = RuntimeReflectionSnapshot(
                summary = "test",
                healthy = significance == RuntimeMeaningSignificance.STABLE,
                analyzedAt = 1L
            ),
            trend = RuntimeReflectionTrend(
                stability = when (significance) {
                    RuntimeMeaningSignificance.STABLE ->
                        RuntimeReflectionStability.STABLE
                    RuntimeMeaningSignificance.WARNING ->
                        RuntimeReflectionStability.DEGRADED
                    RuntimeMeaningSignificance.CRITICAL ->
                        RuntimeReflectionStability.UNSTABLE
                    RuntimeMeaningSignificance.UNKNOWN ->
                        RuntimeReflectionStability.UNKNOWN
                },
                healthyRatio = 1.0,
                improving = false
            ),
            meaning = meaning,
            experienceKnowledge =
                RuntimeExperienceKnowledgeOrchestrationResult(
                    pipelineResult = experienceKnowledgePipelineResult
                )
        )
    }

    @Test
    fun stable_runtime_requires_no_action() {
        val result = DefaultRuntimeDecisionEngine()
            .decide(intelligence(RuntimeMeaningSignificance.STABLE))

        assertNull(result.command)
        assertEquals(0.9, result.confidence)
    }

    @Test
    fun warning_runtime_requests_health_check() {
        val result = DefaultRuntimeDecisionEngine()
            .decide(intelligence(RuntimeMeaningSignificance.WARNING))

        assertEquals(RuntimeCommand.HEALTH_CHECK, result.command)
    }

    @Test
    fun critical_runtime_requests_recovery() {
        val result = DefaultRuntimeDecisionEngine()
            .decide(intelligence(RuntimeMeaningSignificance.CRITICAL))

        assertEquals(RuntimeCommand.RECOVER, result.command)
    }

    @Test
    fun unknown_runtime_requests_health_check() {
        val result = DefaultRuntimeDecisionEngine()
            .decide(intelligence(RuntimeMeaningSignificance.UNKNOWN))

        assertEquals(RuntimeCommand.HEALTH_CHECK, result.command)
    }

    @Test
    fun decision_preserves_meaning_confidence() {
        val result = DefaultRuntimeDecisionEngine()
            .decide(
                intelligence(
                    significance = RuntimeMeaningSignificance.WARNING,
                    confidence = 0.73
                )
            )

        assertEquals(0.73, result.confidence)
    }
    @Test
    fun decision_preserves_knowledge_selection_without_using_it_as_policy_signal() {
        val knowledge =
            RuntimeKnowledge(
                statement = "runtime knowledge",
                confidence = 1.0,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        val selection =
            RuntimeKnowledgeSelectionResult(
                knowledge = knowledge,
                relevantPoolUsed = true,
                reason = "test selection",
                selectionReason =
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
                relevanceScore = 1.0
            )

        val result =
            DefaultRuntimeDecisionEngine()
                .decide(
                    intelligence(
                        significance =
                            RuntimeMeaningSignificance.STABLE,
                        confidence = 0.73,
                        knowledgeSelection = selection
                    )
                )

        assertNull(
            result.command,
            "knowledge diagnostics must not change STABLE policy"
        )

        assertEquals(
            "Runtime is stable; no action required",
            result.reason
        )

        assertEquals(
            0.73,
            result.confidence
        )

        assertEquals(
            selection,
            result.knowledgeSelection
        )
    }


}
