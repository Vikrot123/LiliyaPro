package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionReflectionEvidenceContractTest {

    private val deriver =
        DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver()

    @Test
    fun warning_execution_preserves_exact_decision_and_feedback_semantics() {
        val composition =
            DefaultRuntimeComposition()

        val executionResult =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "autonomous-reflection-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-reflection-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    executionResult
                )

        val feedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val evidence =
            deriver.derive(
                evaluation = evaluation,
                feedback = feedback
            )

        assertSame(
            evaluation,
            evidence.evaluation
        )

        assertSame(
            feedback,
            evidence.feedback
        )

        assertSame(
            executionResult.execution.decision,
            evidence.decision
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            evidence.command
        )

        assertEquals(
            executionResult.execution.decision.reason,
            evidence.decisionReason
        )

        assertEquals(
            executionResult.execution.decision.confidence,
            evidence.confidence
        )

        assertTrue(
            evidence.actionAttempted
        )

        assertEquals(
            true,
            evidence.actionSucceeded
        )

        assertEquals(
            RuntimeAutonomousExecutionFeedbackState.POSITIVE,
            evidence.feedbackState
        )
    }

    @Test
    fun autonomous_evidence_does_not_invent_knowledge_provenance() {
        val composition =
            DefaultRuntimeComposition()

        val executionResult =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "autonomous-reflection-knowledge-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-reflection-knowledge-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    executionResult
                )

        val feedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val evidence =
            deriver.derive(
                evaluation = evaluation,
                feedback = feedback
            )

        assertNull(
            evidence.decision.knowledgeSelection
        )

        assertFalse(
            evidence.knowledgeUsed
        )
    }

    @Test
    fun feedback_must_belong_to_same_evaluation_semantics() {
        val composition =
            DefaultRuntimeComposition()

        val executionResult =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.STABLE,
                            confidence = 0.90
                        ),
                    source = "autonomous-reflection-consistency-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "autonomous-reflection-consistency-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    executionResult
                )

        val foreignFeedback =
            RuntimeAutonomousExecutionFeedback(
                state =
                    RuntimeAutonomousExecutionFeedbackState.NEGATIVE,
                assessmentState =
                    evaluation.assessment.state,
                outcomeState =
                    evaluation.outcome.state,
                proposalState =
                    evaluation.executionResult.proposal.state,
                command =
                    RuntimeCommand.RECOVER,
                actionAttempted =
                    true,
                actionSucceeded =
                    false,
                previousRuntimeState =
                    null,
                currentRuntimeState =
                    null,
                message =
                    "foreign feedback"
            )

        val evidence =
            deriver.derive(
                evaluation = evaluation,
                feedback = foreignFeedback
            )

        assertFalse(
            evidence.consistent
        )
    }
}
