package pro.liliya.interaction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import pro.liliya.core.CoreRuntime
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.intelligence.autonomous.RuntimeAutonomousIntelligenceCyclePipelineResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitState

class CoreRuntimeInteractionIntegrationContractTest {

    @Test
    fun interaction_gateway_runs_real_public_core_intelligence_cycle() {
        CoreRuntime.stop()
        CoreRuntime.start()

        try {
            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val gateway =
                LiliyaInteractionGateway(
                    CoreRuntimeInteractionPort(
                        CoreRuntime
                    )
                )

            val result =
                committedCycleThroughGateway(
                    gateway = gateway,
                    prefix = "interaction-real-core"
                )

            val postExecution =
                result.executionCycle.postExecution

            val commit =
                assertNotNull(
                    postExecution.commitResult,
                    "interaction request must reach autonomous experience commit boundary"
                )

            assertEquals(
                RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
                commit.state
            )

            val knowledgeBridge =
                assertNotNull(
                    postExecution.committedExperienceKnowledge,
                    "committed interaction experience must enter knowledge pipeline"
                )

            assertSame(
                commit.experience,
                knowledgeBridge.experience,
                "interaction boundary must preserve committed experience identity"
            )

            assertSame(
                result.intelligence,
                result.executionCycle.execution.intelligence,
                "interaction boundary must preserve intelligence identity into execution"
            )

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )
        } finally {
            CoreRuntime.stop()
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )
    }

    private fun committedCycleThroughGateway(
        gateway: LiliyaInteractionGateway,
        prefix: String
    ): RuntimeAutonomousIntelligenceCyclePipelineResult {

        return (1..16)
            .asSequence()
            .map { index ->
                val source =
                    "   $prefix-$index   "

                gateway.process(
                    LiliyaInteractionRequest(
                        source = source,
                        authority =
                            RuntimeActionAuthorityContext(
                                source = source.trim(),
                                level = RuntimeAuthorityLevel.SYSTEM
                            )
                    )
                )
            }
            .firstOrNull { result ->
                result
                    .executionCycle
                    .postExecution
                    .committedExperienceKnowledge != null
            }
            ?: error(
                "interaction gateway must eventually produce committed experience-derived knowledge"
            )
    }
}
