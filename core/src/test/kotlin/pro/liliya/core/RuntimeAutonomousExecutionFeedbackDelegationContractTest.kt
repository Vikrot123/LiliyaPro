package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.DefaultRuntimeAutonomousExecutionFeedbackDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionFeedbackDelegationContractTest {

    @Test
    fun feedback_derivation_does_not_reexecute_autonomous_action() {
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
                    source = "feedback-single-execution-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "feedback-single-execution-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(executionResult)

        val auditBefore =
            composition
                .actionAuditProvider()
                .snapshot()
                .size

        val feedback =
            DefaultRuntimeAutonomousExecutionFeedbackDeriver()
                .derive(evaluation.assessment)

        val auditAfter =
            composition
                .actionAuditProvider()
                .snapshot()
                .size

        assertEquals(
            RuntimeAutonomousExecutionFeedbackState.POSITIVE,
            feedback.state
        )

        assertEquals(
            auditBefore,
            auditAfter,
            "feedback derivation must not execute or redispatch an action"
        )
    }

    @Test
    fun feedback_derivation_does_not_record_decision_reflection() {
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
                    source = "feedback-reflection-boundary-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "feedback-reflection-boundary-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(executionResult)

        val before =
            composition
                .decisionReflectionHistory()
                .records()
                .size

        DefaultRuntimeAutonomousExecutionFeedbackDeriver()
            .derive(evaluation.assessment)

        val after =
            composition
                .decisionReflectionHistory()
                .records()
                .size

        assertEquals(
            before,
            after,
            "feedback foundation must not record reflection"
        )
    }
}
