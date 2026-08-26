package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.DefaultRuntimeDecisionExecutor
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

class RuntimeDecisionExecutorContractTest {

    private fun intelligence(
        significance: RuntimeMeaningSignificance
    ): RuntimeIntelligenceOrchestrationResult {

        val meaning = RuntimeMeaningResult(
            interpretation = "test",
            confidence = 0.9,
            significance = significance,
            generatedAt = 1L
        )

        val experience = RuntimeExperience(
            description = "test experience",
            meaning = meaning,
            importance = RuntimeExperienceImportance.MEDIUM,
            createdAt = 1L
        )

        val pipeline = RuntimeExperiencePipelineResult(
            experience = experience,
            decision = RuntimeExperienceDecision(
                shouldRemember = true,
                reason = "test"
            )
        )

        val knowledge =
            RuntimeExperienceKnowledgePipelineResult(
                experienceResult = pipeline,
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
                healthy =
                    significance == RuntimeMeaningSignificance.STABLE,
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
                    pipelineResult = knowledge
                )
        )
    }

    private fun executor(
        composition: DefaultRuntimeComposition
    ): DefaultRuntimeDecisionExecutor {
        return DefaultRuntimeDecisionExecutor(
            decisionEngine = DefaultRuntimeDecisionEngine(),
            requestFactory =
                DefaultRuntimeDecisionActionRequestFactory(),
            actionDispatcher = composition.actionDispatcher()
        )
    }

    @Test
    fun stable_decision_does_not_enter_action_pipeline() {
        val composition = DefaultRuntimeComposition()

        val result = executor(composition).execute(
            intelligence =
                intelligence(RuntimeMeaningSignificance.STABLE),
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "system-controller",
                level = RuntimeAuthorityLevel.SYSTEM
            )
        )

        assertNull(result.decision.command)
        assertNull(result.request)
        assertNull(result.actionResult)
        assertTrue(
            composition.actionAuditProvider()
                .snapshot()
                .isEmpty()
        )
    }

    @Test
    fun warning_decision_enters_health_action_pipeline() {
        val composition = DefaultRuntimeComposition()

        val result = executor(composition).execute(
            intelligence =
                intelligence(RuntimeMeaningSignificance.WARNING),
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "system-controller",
                level = RuntimeAuthorityLevel.SYSTEM
            )
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            result.decision.command
        )
        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            result.request?.command
        )
        assertNotNull(result.actionResult)

        assertTrue(
            composition.actionAuditProvider()
                .snapshot()
                .isNotEmpty()
        )
    }

    @Test
    fun critical_system_decision_reaches_recovery_policy() {
        val composition = DefaultRuntimeComposition()

        val result = executor(composition).execute(
            intelligence =
                intelligence(RuntimeMeaningSignificance.CRITICAL),
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "system-controller",
                level = RuntimeAuthorityLevel.SYSTEM
            )
        )

        assertEquals(
            RuntimeCommand.RECOVER,
            result.request?.command
        )

        val audit =
            composition.actionAuditProvider()
                .snapshot()
                .last()

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            audit.actualAuthority
        )
    }

    @Test
    fun critical_user_decision_cannot_bypass_policy() {
        val composition = DefaultRuntimeComposition()

        val result = executor(composition).execute(
            intelligence =
                intelligence(RuntimeMeaningSignificance.CRITICAL),
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "user-controller",
                level = RuntimeAuthorityLevel.USER
            )
        )

        assertEquals(
            RuntimeCommand.RECOVER,
            result.request?.command
        )

        val actionResult =
            assertNotNull(result.actionResult)

        assertFalse(actionResult.success)

        val audit =
            composition.actionAuditProvider()
                .snapshot()
                .last()

        assertEquals(
            RuntimeAuthorityLevel.USER,
            audit.actualAuthority
        )

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            audit.requiredAuthority
        )
    }
}
