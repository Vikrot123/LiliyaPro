package pro.liliya.interaction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LiliyaInteractionRuntimeLifecycleContractTest {

    @Test
    fun interaction_runtime_owns_public_core_lifecycle_and_processing() {
        val runtime =
            LiliyaInteractionRuntimeFactory.create()

        runtime.stop()

        assertEquals(
            LiliyaInteractionRuntimeState.STOPPED,
            runtime.state()
        )

        runtime.start()

        try {
            assertEquals(
                LiliyaInteractionRuntimeState.RUNNING,
                runtime.state()
            )

            val result =
                committedResult(runtime)

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
                LiliyaInteractionRuntimeState.RUNNING,
                runtime.state()
            )
        } finally {
            runtime.stop()
        }

        assertEquals(
            LiliyaInteractionRuntimeState.STOPPED,
            runtime.state()
        )
    }

    @Test
    fun interaction_runtime_can_restart_without_exposing_core_runtime() {
        val runtime =
            LiliyaInteractionRuntimeFactory.create()

        runtime.stop()
        runtime.start()

        assertEquals(
            LiliyaInteractionRuntimeState.RUNNING,
            runtime.state()
        )

        runtime.stop()

        assertEquals(
            LiliyaInteractionRuntimeState.STOPPED,
            runtime.state()
        )

        runtime.start()

        try {
            assertEquals(
                LiliyaInteractionRuntimeState.RUNNING,
                runtime.state()
            )

            val result =
                runtime.process(
                    LiliyaInteractionRequest(
                        source =
                            "interaction-runtime-restart",
                        authority =
                            LiliyaInteractionAuthority.USER
                    )
                )

            assertTrue(
                result.interpretation.isNotBlank()
            )
        } finally {
            runtime.stop()
        }

        assertEquals(
            LiliyaInteractionRuntimeState.STOPPED,
            runtime.state()
        )
    }

    private fun committedResult(
        runtime: LiliyaInteractionRuntime
    ): LiliyaInteractionResult {
        return (1..16)
            .asSequence()
            .map { index ->
                runtime.process(
                    LiliyaInteractionRequest(
                        source =
                            "interaction-runtime-cycle-$index",
                        authority =
                            LiliyaInteractionAuthority.SYSTEM
                    )
                )
            }
            .firstOrNull { result ->
                result.knowledgeProduced
            }
            ?: error(
                "interaction runtime must eventually produce knowledge"
            )
    }
}
