package pro.liliya.core

import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrationResult
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipelineResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

object RuntimeIntelligenceFixture {

    fun result(
        significance: RuntimeMeaningSignificance,
        confidence: Double
    ): RuntimeIntelligenceOrchestrationResult {

        val selfModel =
            RuntimeSelfModel(
                snapshot =
                    RuntimeContextSnapshot(
                        runtimeState = "RUNNING",
                        activeServices =
                            listOf("core"),
                        timestamp = 1L
                    ),
                metadata =
                    RuntimeContextMetadata(
                        runtimeVersion = "fixture",
                        recoveryAvailable = true,
                        diagnosticsAvailable = true
                    )
            )

        val reflection =
            RuntimeReflectionSnapshot(
                summary =
                    "fixture reflection",
                healthy =
                    significance ==
                        RuntimeMeaningSignificance.STABLE,
                analyzedAt = 1L
            )

        val trend =
            RuntimeReflectionTrend(
                stability =
                    when (significance) {
                        RuntimeMeaningSignificance.STABLE ->
                            RuntimeReflectionStability.STABLE

                        RuntimeMeaningSignificance.WARNING ->
                            RuntimeReflectionStability.DEGRADED

                        RuntimeMeaningSignificance.CRITICAL ->
                            RuntimeReflectionStability.UNSTABLE

                        RuntimeMeaningSignificance.UNKNOWN ->
                            RuntimeReflectionStability.UNKNOWN
                    },
                healthyRatio =
                    if (
                        significance ==
                        RuntimeMeaningSignificance.STABLE
                    ) {
                        1.0
                    } else {
                        0.0
                    },
                improving = false
            )

        val meaning =
            RuntimeMeaningResult(
                interpretation =
                    "fixture meaning",
                confidence =
                    confidence,
                significance =
                    significance,
                generatedAt = 1L,
                knowledgeSelection = null
            )

        val experience =
            RuntimeExperience(
                description =
                    "fixture experience",
                meaning =
                    meaning,
                importance =
                    RuntimeExperienceImportance.MEDIUM,
                createdAt = 1L
            )

        val experienceDecision =
            RuntimeExperienceDecision(
                shouldRemember = false,
                reason =
                    "fixture experience decision"
            )

        val experienceResult =
            RuntimeExperiencePipelineResult(
                experience =
                    experience,
                decision =
                    experienceDecision
            )

        val experienceKnowledgeResult =
            RuntimeExperienceKnowledgePipelineResult(
                experienceResult =
                    experienceResult,
                consolidation = null,
                knowledgeResult = null
            )

        return RuntimeIntelligenceOrchestrationResult(
            selfModel =
                selfModel,
            reflection =
                reflection,
            trend =
                trend,
            meaning =
                meaning,
            experienceKnowledge =
                RuntimeExperienceKnowledgeOrchestrationResult(
                    pipelineResult =
                        experienceKnowledgeResult
                ),
            knowledgeMaintenance = null
        )
    }
}
