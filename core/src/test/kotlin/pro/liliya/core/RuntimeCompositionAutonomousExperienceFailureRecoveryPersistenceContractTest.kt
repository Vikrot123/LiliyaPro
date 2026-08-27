package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionAutonomousExperienceFailureRecoveryPersistenceContractTest {

    @Test
    fun committed_autonomous_experience_survives_failure_recovery_without_duplication() {
        val composition =
            DefaultRuntimeComposition()

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val storeBefore =
            store

        val before =
            store.experiences().size

        val firstExecution =
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
                        "v1.511-before-failure",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.511-before-failure",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val firstEvaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    firstExecution
                )

        val firstPostExecution =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(
                    firstEvaluation
                )

        val firstCommit =
            firstPostExecution.commitResult
                ?: error(
                    "pre-failure autonomous execution must commit experience"
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            firstCommit.state
        )

        val firstExperience =
            firstCommit.experience

        assertEquals(
            before + 1,
            store.experiences().size
        )

        assertTrue(
            store.experiences().any {
                it === firstExperience
            },
            "committed pre-failure experience must be stored by identity"
        )

        composition.markRuntimeFailed(
            "v1.511-failure-boundary"
        )

        assertEquals(
            CoreRuntimeState.FAILED,
            composition.runtimeState()
        )

        composition.prepareRuntime()

        val storeAfter =
            composition
                .experienceComposition()
                .experienceStore()

        assertSame(
            storeBefore,
            storeAfter,
            "failure recovery reset must preserve experience store owner identity"
        )

        assertEquals(
            before + 1,
            storeAfter.experiences().size,
            "failure recovery reset must preserve committed autonomous experience"
        )

        assertTrue(
            storeAfter.experiences().any {
                it === firstExperience
            },
            "pre-failure committed experience identity must survive recovery reset"
        )

        val sameExecutionAgain =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(
                    firstEvaluation
                )

        val sameExecutionCommit =
            sameExecutionAgain.commitResult
                ?: error(
                    "old execution must still reach commit boundary after recovery reset"
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.ALREADY_COMMITTED,
            sameExecutionCommit.state,
            "old execution identity must remain idempotent after recovery reset"
        )

        assertEquals(
            before + 1,
            storeAfter.experiences().size,
            "reprocessing old execution must not duplicate persisted experience"
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
                        "v1.511-recovered",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.511-recovered",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertNotSame(
            firstExecution,
            recoveredExecution,
            "recovered execution must remain independent from pre-failure execution"
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
                    "recovered autonomous execution must commit experience"
                )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            recoveredCommit.state,
            "new recovered execution must commit independently"
        )

        val recoveredExperience =
            recoveredCommit.experience

        assertNotSame(
            firstExperience,
            recoveredExperience,
            "recovered commit must create independent experience identity"
        )

        assertEquals(
            before + 2,
            storeAfter.experiences().size,
            "recovered execution must append exactly one new experience"
        )

        assertTrue(
            storeAfter.experiences().any {
                it === firstExperience
            },
            "pre-failure experience must remain present after recovered commit"
        )

        assertTrue(
            storeAfter.experiences().any {
                it === recoveredExperience
            },
            "recovered experience must be present after recovered commit"
        )
    }
}
