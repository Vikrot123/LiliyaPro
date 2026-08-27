package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceContextDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionExperienceContextDeriverContractTest {

    private val deriver =
        DefaultRuntimeAutonomousExecutionExperienceContextDeriver()

    @Test
    fun consistent_reflection_preserves_exact_intelligence_context() {
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
                    source = "v1.487-context",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.487-context",
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

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val context =
            deriver.derive(analysis)
                ?: error("consistent analysis must produce experience context")

        assertSame(
            execution.intelligence.selfModel,
            context.selfModel
        )

        assertSame(
            execution.intelligence.meaning,
            context.meaning
        )
    }

    @Test
    fun inconsistent_reflection_cannot_enter_experience_boundary() {
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
                    source = "v1.487-inconsistent",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.487-inconsistent",
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
                .derive(evaluation.assessment)

        val inconsistentFeedback =
            validFeedback.copy(
                state =
                    RuntimeAutonomousExecutionFeedbackState.NEGATIVE,
                command =
                    RuntimeCommand.RECOVER,
                message =
                    "foreign experience semantics"
            )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = inconsistentFeedback
            )

        assertNull(
            deriver.derive(analysis)
        )
    }
}
