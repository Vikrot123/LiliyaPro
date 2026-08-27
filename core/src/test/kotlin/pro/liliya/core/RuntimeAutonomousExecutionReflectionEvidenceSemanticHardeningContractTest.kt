package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionReflectionEvidenceSemanticHardeningContractTest {

    private val deriver =
        DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver()

    @Test
    fun exact_feedback_semantics_are_consistent() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "reflection-hardening",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-hardening",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val feedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(evaluation.assessment)

        val evidence =
            deriver.derive(
                evaluation = evaluation,
                feedback = feedback
            )

        assertTrue(evidence.consistent)
    }

    @Test
    fun changed_feedback_state_fails_closed() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "reflection-state-mismatch",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-state-mismatch",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val valid =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(evaluation.assessment)

        val foreign =
            valid.copy(
                state =
                    RuntimeAutonomousExecutionFeedbackState.NEGATIVE
            )

        val evidence =
            deriver.derive(
                evaluation = evaluation,
                feedback = foreign
            )

        assertFalse(evidence.consistent)
    }

    @Test
    fun changed_command_fails_closed() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "reflection-command-mismatch",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-command-mismatch",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val valid =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(evaluation.assessment)

        val foreign =
            valid.copy(
                command =
                    RuntimeCommand.RECOVER
            )

        val evidence =
            deriver.derive(
                evaluation = evaluation,
                feedback = foreign
            )

        assertFalse(evidence.consistent)
    }

    @Test
    fun changed_feedback_message_fails_closed() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "reflection-message-mismatch",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-message-mismatch",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val valid =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(evaluation.assessment)

        val foreign =
            valid.copy(
                message =
                    "foreign semantic result"
            )

        val evidence =
            deriver.derive(
                evaluation = evaluation,
                feedback = foreign
            )

        assertFalse(evidence.consistent)
    }
}
