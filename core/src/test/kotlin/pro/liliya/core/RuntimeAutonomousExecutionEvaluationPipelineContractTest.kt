package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.DefaultRuntimeAutonomousExecutionAssessmentDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.DefaultRuntimeAutonomousExecutionEvaluationPipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.DefaultRuntimeAutonomousExecutionOutcomeDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionEvaluationPipelineContractTest {

    private fun pipeline() =
        DefaultRuntimeAutonomousExecutionEvaluationPipeline(
            outcomeDeriver =
                DefaultRuntimeAutonomousExecutionOutcomeDeriver(),
            assessmentDeriver =
                DefaultRuntimeAutonomousExecutionAssessmentDeriver()
        )

    @Test
    fun evaluation_preserves_exact_execution_result_snapshot() {
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
                    source = "evaluation-identity-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "evaluation-identity-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val result =
            pipeline().evaluate(
                executionResult
            )

        assertSame(
            executionResult,
            result.executionResult
        )
    }

    @Test
    fun stable_execution_evaluates_to_no_action_not_required() {
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
                    source = "evaluation-stable-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "evaluation-stable-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val result =
            pipeline().evaluate(
                executionResult
            )

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.NO_ACTION,
            result.outcome.state
        )

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.NOT_REQUIRED,
            result.assessment.state
        )
    }

    @Test
    fun successful_health_check_evaluates_to_effective() {
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
                    source = "evaluation-success-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "evaluation-success-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val result =
            pipeline().evaluate(
                executionResult
            )

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.SUCCEEDED,
            result.outcome.state
        )

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.EFFECTIVE,
            result.assessment.state
        )
    }

    @Test
    fun denied_recovery_evaluates_to_failed_and_ineffective() {
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
                    source = "evaluation-denied-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "evaluation-denied-test",
                            level = RuntimeAuthorityLevel.USER
                        )
                )

        val result =
            pipeline().evaluate(
                executionResult
            )

        assertEquals(
            RuntimeAutonomousExecutionOutcomeState.FAILED,
            result.outcome.state
        )

        assertEquals(
            RuntimeAutonomousExecutionAssessmentState.INEFFECTIVE,
            result.assessment.state
        )
    }
}
