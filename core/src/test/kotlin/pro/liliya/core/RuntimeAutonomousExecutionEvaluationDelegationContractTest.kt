package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessment
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.DefaultRuntimeAutonomousExecutionEvaluationPipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcome
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipelineResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionEvaluationDelegationContractTest {

    @Test
    fun assessment_receives_exact_outcome_created_for_execution_snapshot() {
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
                    source = "evaluation-delegation-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "evaluation-delegation-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        var receivedExecution:
            RuntimeAutonomousExecutionPipelineResult? =
            null

        var receivedOutcome:
            RuntimeAutonomousExecutionOutcome? =
            null

        val expectedOutcome =
            RuntimeAutonomousExecutionOutcome(
                state =
                    RuntimeAutonomousExecutionOutcomeState.NO_ACTION,
                proposalState =
                    executionResult.proposal.state,
                command =
                    executionResult.execution.decision.command,
                actionAttempted = false,
                actionSucceeded = null,
                previousRuntimeState = null,
                currentRuntimeState = null,
                message = null
            )

        val expectedAssessment =
            RuntimeAutonomousExecutionAssessment(
                state =
                    RuntimeAutonomousExecutionAssessmentState.NOT_REQUIRED,
                outcomeState =
                    expectedOutcome.state,
                proposalState =
                    expectedOutcome.proposalState,
                command =
                    expectedOutcome.command,
                actionAttempted =
                    expectedOutcome.actionAttempted,
                actionSucceeded =
                    expectedOutcome.actionSucceeded,
                previousRuntimeState =
                    expectedOutcome.previousRuntimeState,
                currentRuntimeState =
                    expectedOutcome.currentRuntimeState,
                message =
                    expectedOutcome.message
            )

        val pipeline =
            DefaultRuntimeAutonomousExecutionEvaluationPipeline(
                outcomeDeriver =
                    object :
                        RuntimeAutonomousExecutionOutcomeDeriver {

                        override fun derive(
                            result:
                                RuntimeAutonomousExecutionPipelineResult
                        ): RuntimeAutonomousExecutionOutcome {
                            receivedExecution =
                                result

                            return expectedOutcome
                        }
                    },
                assessmentDeriver =
                    object :
                        RuntimeAutonomousExecutionAssessmentDeriver {

                        override fun derive(
                            outcome:
                                RuntimeAutonomousExecutionOutcome
                        ): RuntimeAutonomousExecutionAssessment {
                            receivedOutcome =
                                outcome

                            return expectedAssessment
                        }
                    }
            )

        val result =
            pipeline.evaluate(
                executionResult
            )

        assertSame(
            executionResult,
            receivedExecution
        )

        assertSame(
            expectedOutcome,
            receivedOutcome
        )

        assertSame(
            executionResult,
            result.executionResult
        )

        assertSame(
            expectedOutcome,
            result.outcome
        )

        assertSame(
            expectedAssessment,
            result.assessment
        )
    }

    @Test
    fun evaluation_does_not_reexecute_autonomous_action() {
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
                    source = "evaluation-single-execution-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "evaluation-single-execution-test",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val auditBefore =
            composition
                .actionAuditProvider()
                .snapshot()
                .size

        DefaultRuntimeAutonomousExecutionEvaluationPipeline(
            outcomeDeriver =
                pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.DefaultRuntimeAutonomousExecutionOutcomeDeriver(),
            assessmentDeriver =
                pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.DefaultRuntimeAutonomousExecutionAssessmentDeriver()
        ).evaluate(
            executionResult
        )

        val auditAfter =
            composition
                .actionAuditProvider()
                .snapshot()
                .size

        assertEquals(
            auditBefore,
            auditAfter,
            "post-execution evaluation must not dispatch an action again"
        )
    }
}
