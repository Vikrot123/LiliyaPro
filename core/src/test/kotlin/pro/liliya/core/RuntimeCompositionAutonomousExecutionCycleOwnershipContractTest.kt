package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.cycle.RuntimeAutonomousExecutionCyclePipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionAutonomousExecutionCycleOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_autonomous_execution_cycle() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .autonomousExecutionCyclePipeline()

        val second =
            composition
                .autonomousExecutionCyclePipeline()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_autonomous_execution_cycle() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousExecutionCyclePipeline(),
            second.autonomousExecutionCyclePipeline()
        )
    }

    @Test
    fun root_exposes_autonomous_execution_cycle_as_expected_contract_type() {
        val composition =
            DefaultRuntimeComposition()

        val cycle:
            RuntimeAutonomousExecutionCyclePipeline =
            composition.autonomousExecutionCyclePipeline()

        assertSame(
            cycle,
            composition.autonomousExecutionCyclePipeline()
        )
    }

    @Test
    fun autonomous_cycle_reuses_root_owned_execution_chain() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.WARNING,
                confidence =
                    0.90
            )

        val result =
            composition
                .autonomousExecutionCyclePipeline()
                .process(
                    intelligence =
                        intelligence,
                    source =
                        "v1.497-root-chain",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.497-root-chain",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertSame(
            intelligence,
            result.execution.intelligence
        )

        assertSame(
            result.execution,
            result.evaluation.executionResult
        )

        assertSame(
            result.evaluation,
            result.postExecution.evaluation
        )

        val commit =
            result.postExecution.commitResult
                ?: error("warning autonomous cycle must reach commit")

        assertEquals(
            RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            commit.state
        )
    }

    @Test
    fun prepare_runtime_preserves_autonomous_cycle_owner_identity() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition
                .autonomousExecutionCyclePipeline()

        composition.prepareRuntime()

        val after =
            composition
                .autonomousExecutionCyclePipeline()

        assertSame(
            before,
            after
        )
    }

    @Test
    fun prepare_runtime_preserves_cycle_commit_idempotency_for_same_execution() {
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
                        "v1.497-idempotency",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.497-idempotency",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(
                    execution
                )

        val postExecution =
            composition
                .autonomousPostExecutionLearningPipeline()

        val first =
            postExecution.process(
                evaluation
            )

        val firstCommit =
            first.commitResult
                ?: error("first processing must commit")

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
            store.experiences().size
        )
    }
}
