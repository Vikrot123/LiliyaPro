package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.autonomous.RuntimeAutonomousIntelligenceCyclePipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState

class RuntimeCompositionAutonomousIntelligenceCycleOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_autonomous_intelligence_cycle() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .autonomousIntelligenceCyclePipeline()

        val second =
            composition
                .autonomousIntelligenceCyclePipeline()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_autonomous_intelligence_cycle() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousIntelligenceCyclePipeline(),
            second.autonomousIntelligenceCyclePipeline()
        )
    }

    @Test
    fun root_exposes_autonomous_intelligence_cycle_as_expected_contract_type() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline:
            RuntimeAutonomousIntelligenceCyclePipeline =
            composition.autonomousIntelligenceCyclePipeline()

        assertSame(
            pipeline,
            composition.autonomousIntelligenceCyclePipeline()
        )
    }

    @Test
    fun prepare_runtime_preserves_autonomous_intelligence_cycle_owner_identity() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition
                .autonomousIntelligenceCyclePipeline()

        composition.prepareRuntime()

        val after =
            composition
                .autonomousIntelligenceCyclePipeline()

        assertSame(
            before,
            after
        )
    }

    @Test
    fun root_owned_autonomous_intelligence_cycle_reuses_existing_execution_chain() {
        val composition =
            DefaultRuntimeComposition()

        val result =
            composition
                .autonomousIntelligenceCyclePipeline()
                .process(
                    source =
                        "v1.499-root-chain",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.499-root-chain",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        assertSame(
            result.intelligence,
            result.executionCycle.execution.intelligence
        )

        assertSame(
            result.executionCycle.execution,
            result.executionCycle.evaluation.executionResult
        )

        assertSame(
            result.executionCycle.evaluation,
            result.executionCycle.postExecution.evaluation
        )
    }

    @Test
    fun root_owned_autonomous_intelligence_cycle_can_reach_post_execution_commit() {
        val composition =
            DefaultRuntimeComposition()

        var committedResultFound = false

        repeat(4) { index ->
            val result =
                composition
                    .autonomousIntelligenceCyclePipeline()
                    .process(
                        source =
                            "v1.499-root-commit-$index",
                        authority =
                            RuntimeActionAuthorityContext(
                                source =
                                    "v1.499-root-commit-$index",
                                level =
                                    RuntimeAuthorityLevel.SYSTEM
                            )
                    )

            val commit =
                result
                    .executionCycle
                    .postExecution
                    .commitResult

            if (
                commit != null &&
                commit.state ==
                    RuntimeAutonomousExecutionExperienceCommitState.COMMITTED
            ) {
                committedResultFound = true
            }
        }

        assertEquals(
            true,
            committedResultFound,
            "root-owned autonomous intelligence cycle must preserve access to the post-execution commit path"
        )
    }

    @Test
    fun root_owned_cycle_preserves_exact_runtime_action_request_transport_semantics_when_action_exists() {
        val composition =
            DefaultRuntimeComposition()

        val authority =
            RuntimeActionAuthorityContext(
                source =
                    "v1.499-transport",
                level =
                    RuntimeAuthorityLevel.SYSTEM
            )

        var observedRequest = false

        repeat(4) { index ->
            val source =
                "v1.499-transport-$index"

            val requestAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        source,
                    level =
                        authority.level
                )

            val result =
                composition
                    .autonomousIntelligenceCyclePipeline()
                    .process(
                        source =
                            source,
                        authority =
                            requestAuthority
                    )

            val request =
                result
                    .executionCycle
                    .execution
                    .execution
                    .request

            if (request != null) {
                observedRequest = true

                assertEquals(
                    source,
                    request.source
                )

                assertSame(
                    requestAuthority,
                    request.authority
                )
            }
        }

        assertEquals(
            true,
            observedRequest,
            "root-owned autonomous intelligence cycle must preserve source and authority when an action request is produced"
        )
    }
}
