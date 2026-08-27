package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.postexecution.RuntimeAutonomousExecutionPostExecutionLearningPipeline
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionAutonomousPostExecutionLearningOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_post_execution_learning_pipeline() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .autonomousPostExecutionLearningPipeline()

        val second =
            composition
                .autonomousPostExecutionLearningPipeline()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_post_execution_learning_pipeline() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousPostExecutionLearningPipeline(),
            second.autonomousPostExecutionLearningPipeline()
        )
    }

    @Test
    fun root_exposes_post_execution_pipeline_as_expected_contract_type() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline:
            RuntimeAutonomousExecutionPostExecutionLearningPipeline =
            composition.autonomousPostExecutionLearningPipeline()

        assertSame(
            pipeline,
            composition.autonomousPostExecutionLearningPipeline()
        )
    }

    @Test
    fun prepare_runtime_preserves_post_execution_pipeline_owner_identity() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition
                .autonomousPostExecutionLearningPipeline()

        composition.prepareRuntime()

        val after =
            composition
                .autonomousPostExecutionLearningPipeline()

        assertSame(
            before,
            after
        )
    }

    @Test
    fun distinct_execution_snapshots_are_committed_independently() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            composition
                .autonomousPostExecutionLearningPipeline()

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
                        "v1.495-distinct-first",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.495-distinct-first",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val secondExecution =
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
                        "v1.495-distinct-second",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.495-distinct-second",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertNotSame(
            firstExecution,
            secondExecution
        )

        val firstEvaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    firstExecution
                )

        val secondEvaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    secondExecution
                )

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val before =
            store.experiences().size

        val first =
            pipeline.process(
                firstEvaluation
            )

        val second =
            pipeline.process(
                secondEvaluation
            )

        val firstCommit =
            first.commitResult
                ?: error("first execution must reach commit boundary")

        val secondCommit =
            second.commitResult
                ?: error("second execution must reach commit boundary")

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            firstCommit.state
        )

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            secondCommit.state
        )

        assertEquals(
            before + 2,
            store.experiences().size,
            "distinct execution snapshots must remain independent commit identities"
        )
    }


    @Test
    fun prepare_runtime_preserves_post_execution_commit_idempotency() {
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
                            confidence =
                                0.90
                        ),
                    source =
                        "v1.495-idempotency",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.495-idempotency",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val pipeline =
            composition
                .autonomousPostExecutionLearningPipeline()

        val first =
            pipeline.process(
                evaluation
            )

        val firstCommit =
            first.commitResult
                ?: error("first post-execution learning must commit")

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            firstCommit.state
        )

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val afterFirst =
            store.experiences().size

        composition.prepareRuntime()

        val second =
            composition
                .autonomousPostExecutionLearningPipeline()
                .process(
                    evaluation
                )

        val secondCommit =
            second.commitResult
                ?: error("second processing must reach commit boundary")

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.ALREADY_COMMITTED,
            secondCommit.state
        )

        assertEquals(
            afterFirst,
            store.experiences().size,
            "prepareRuntime must not reset autonomous commit idempotency while experience store survives"
        )
    }
}
