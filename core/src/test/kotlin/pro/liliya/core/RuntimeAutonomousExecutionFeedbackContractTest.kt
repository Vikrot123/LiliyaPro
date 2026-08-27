package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.DefaultRuntimeAutonomousExecutionFeedbackDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionFeedbackContractTest {

    private fun feedbackFor(
        significance: RuntimeMeaningSignificance,
        confidence: Double,
        authorityLevel: RuntimeAuthorityLevel,
        source: String
    ) = DefaultRuntimeAutonomousExecutionFeedbackDeriver()
        .derive(
            DefaultRuntimeComposition()
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    DefaultRuntimeComposition()
                        .autonomousExecutionPipeline()
                        .process(
                            intelligence =
                                RuntimeIntelligenceFixture.result(
                                    significance = significance,
                                    confidence = confidence
                                ),
                            source = source,
                            authority =
                                RuntimeActionAuthorityContext(
                                    source = source,
                                    level = authorityLevel
                                )
                        )
                )
                .assessment
        )

    @Test
    fun no_action_assessment_derives_no_feedback_required() {
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
                    source = "feedback-stable-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "feedback-stable-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(executionResult)

        val feedback =
            DefaultRuntimeAutonomousExecutionFeedbackDeriver()
                .derive(evaluation.assessment)

        assertEquals(
            RuntimeAutonomousExecutionFeedbackState.NO_FEEDBACK_REQUIRED,
            feedback.state
        )

        assertEquals(
            evaluation.assessment.state,
            feedback.assessmentState
        )

        assertEquals(
            evaluation.assessment.outcomeState,
            feedback.outcomeState
        )

        assertEquals(
            evaluation.assessment.proposalState,
            feedback.proposalState
        )

        assertEquals(
            evaluation.assessment.command,
            feedback.command
        )

        assertEquals(
            evaluation.assessment.actionAttempted,
            feedback.actionAttempted
        )

        assertEquals(
            evaluation.assessment.actionSucceeded,
            feedback.actionSucceeded
        )

        assertEquals(
            evaluation.assessment.previousRuntimeState,
            feedback.previousRuntimeState
        )

        assertEquals(
            evaluation.assessment.currentRuntimeState,
            feedback.currentRuntimeState
        )

        assertEquals(
            evaluation.assessment.message,
            feedback.message
        )
    }

    @Test
    fun effective_assessment_derives_positive_feedback() {
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
                    source = "feedback-effective-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "feedback-effective-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(executionResult)

        val feedback =
            DefaultRuntimeAutonomousExecutionFeedbackDeriver()
                .derive(evaluation.assessment)

        assertEquals(
            RuntimeAutonomousExecutionFeedbackState.POSITIVE,
            feedback.state
        )
    }

    @Test
    fun ineffective_assessment_derives_negative_feedback() {
        val composition =
            DefaultRuntimeComposition()

        val executionResult =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.CRITICAL,
                            confidence = 0.95
                        ),
                    source = "feedback-ineffective-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "feedback-ineffective-test",
                            level = RuntimeAuthorityLevel.USER
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(executionResult)

        val feedback =
            DefaultRuntimeAutonomousExecutionFeedbackDeriver()
                .derive(evaluation.assessment)

        assertEquals(
            RuntimeAutonomousExecutionFeedbackState.NEGATIVE,
            feedback.state
        )
    }
}
