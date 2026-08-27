package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionAutonomousCommitFailureRecoveryIsolationContractTest {

    @Test
    fun new_execution_after_failure_recovery_remains_independent_commit_identity() {
        val composition =
            DefaultRuntimeComposition()

        val executionPipeline =
            composition.autonomousExecutionPipeline()

        val evaluationPipeline =
            composition.autonomousExecutionEvaluationPipeline()

        val postExecutionPipeline =
            composition.autonomousPostExecutionLearningPipeline()

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val before =
            store.experiences().size

        val firstExecution =
            executionPipeline.process(
                intelligence =
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.WARNING,
                        confidence =
                            0.90
                    ),
                source =
                    "v1.510-before-failure",
                authority =
                    RuntimeActionAuthorityContext(
                        source =
                            "v1.510-before-failure",
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )
            )

        val firstEvaluation =
            evaluationPipeline.evaluate(
                firstExecution
            )

        val firstPostExecution =
            postExecutionPipeline.process(
                firstEvaluation
            )

        val firstCommit =
            firstPostExecution.commitResult
                ?: error(
                    "pre-failure execution must reach commit boundary"
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            firstCommit.state
        )

        assertEquals(
            before + 1,
            store.experiences().size
        )

        composition.markRuntimeFailed(
            "v1.510-failure-boundary"
        )

        assertEquals(
            CoreRuntimeState.FAILED,
            composition.runtimeState()
        )

        composition.prepareRuntime()

        val sameExecutionAgain =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(
                    firstEvaluation
                )

        val sameExecutionCommit =
            sameExecutionAgain.commitResult
                ?: error(
                    "same pre-failure execution must still reach commit boundary"
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.ALREADY_COMMITTED,
            sameExecutionCommit.state,
            "failure recovery reset must preserve idempotency for old execution identity"
        )

        assertEquals(
            before + 1,
            store.experiences().size,
            "old execution identity must not duplicate learning state after recovery reset"
        )

        val recoveredExecution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence =
                                0.90
                        ),
                    source =
                        "v1.510-recovered",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.510-recovered",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertNotSame(
            firstExecution,
            recoveredExecution,
            "recovered execution must have a new commit identity"
        )

        val recoveredEvaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    recoveredExecution
                )

        val recoveredPostExecution =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(
                    recoveredEvaluation
                )

        val recoveredCommit =
            recoveredPostExecution.commitResult
                ?: error(
                    "recovered execution must reach commit boundary"
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            recoveredCommit.state,
            "new recovered execution must not be mistaken for an already committed execution"
        )

        assertEquals(
            before + 2,
            store.experiences().size,
            "recovered execution must create exactly one independent learning record"
        )

        val recoveredAgain =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(
                    recoveredEvaluation
                )

        val recoveredAgainCommit =
            recoveredAgain.commitResult
                ?: error(
                    "repeated recovered execution must reach commit boundary"
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.ALREADY_COMMITTED,
            recoveredAgainCommit.state,
            "recovered execution must become idempotent after its first commit"
        )

        assertEquals(
            before + 2,
            store.experiences().size,
            "reprocessing recovered execution must not duplicate learning state"
        )
    }
}
