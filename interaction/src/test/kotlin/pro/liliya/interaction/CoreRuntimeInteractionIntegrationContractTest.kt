package pro.liliya.interaction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.CoreRuntime
import pro.liliya.core.CoreRuntimeState

class CoreRuntimeInteractionIntegrationContractTest {

    @Test
    fun interaction_gateway_runs_real_core_without_exposing_core_result() {
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
                committedResult(
                    gateway = gateway,
                    prefix = "interaction-public-contract"
                )

            assertTrue(
                result.interpretation.isNotBlank()
            )

            assertTrue(
                result.confidence >= 0.0
            )

            assertTrue(
                result.experienceCommitted
            )

            assertTrue(
                result.knowledgeProduced
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

    private fun committedResult(
        gateway: LiliyaInteractionGateway,
        prefix: String
    ): LiliyaInteractionResult {
        return (1..16)
            .asSequence()
            .map { index ->
                gateway.process(
                    LiliyaInteractionRequest(
                        source =
                            "   $prefix-$index   ",
                        authority =
                            LiliyaInteractionAuthority.SYSTEM
                    )
                )
            }
            .firstOrNull { result ->
                result.knowledgeProduced
            }
            ?: error(
                "interaction must eventually produce knowledge"
            )
    }
}
