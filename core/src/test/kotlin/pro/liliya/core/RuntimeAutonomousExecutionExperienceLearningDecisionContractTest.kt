package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceLearningDecisionState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionExperienceLearningDecisionContractTest {

    private val engine =
        DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine()

    @Test
    fun consistent_post_execution_reflection_is_recognized_without_duplicate_reprocessing() {
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
                    source = "v1.488",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.488",
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
                .derive(
                    evaluation.assessment
                )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val decision =
            engine.decide(
                analysis
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceLearningDecisionState.ALREADY_REPRESENTED,
            decision.state
        )

        assertFalse(
            decision.shouldProcessExperience,
            "the original intelligence cycle already processed this selfModel + meaning context"
        )

        assertTrue(
            decision.reason.isNotBlank()
        )
    }

    @Test
    fun inconsistent_reflection_is_rejected_before_learning() {
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
                    source = "v1.488-inconsistent",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.488-inconsistent",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val validFeedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val inconsistentFeedback =
            validFeedback.copy(
                state =
                    RuntimeAutonomousExecutionFeedbackState.NEGATIVE,
                command =
                    RuntimeCommand.RECOVER,
                message =
                    "foreign learning semantics"
            )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = inconsistentFeedback
            )

        val decision =
            engine.decide(
                analysis
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceLearningDecisionState.REJECTED,
            decision.state
        )

        assertFalse(
            decision.shouldProcessExperience
        )
    }

    @Test
    fun learning_decision_is_read_only() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.STABLE,
                            confidence = 0.90
                        ),
                    source = "v1.488-read-only",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.488-read-only",
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
                .derive(
                    evaluation.assessment
                )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val before =
            store.experiences().size

        engine.decide(
            analysis
        )

        val after =
            store.experiences().size

        assertEquals(
            before,
            after,
            "learning decision must not write experience by itself"
        )
    }
}
