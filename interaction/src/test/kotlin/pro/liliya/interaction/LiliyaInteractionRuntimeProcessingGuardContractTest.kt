package pro.liliya.interaction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LiliyaInteractionRuntimeProcessingGuardContractTest {

    @Test
    fun processing_fails_closed_while_runtime_is_stopped() {
        val runtime =
            LiliyaInteractionRuntimeFactory.create()

        runtime.stop()

        assertEquals(
            LiliyaInteractionRuntimeState.STOPPED,
            runtime.state()
        )

        val error =
            assertFailsWith<LiliyaInteractionRuntimeException> {
                runtime.process(
                    request(
                        "interaction-stopped"
                    )
                )
            }

        assertTrue(
            error.message.orEmpty()
                .contains("STOPPED")
        )
    }

    @Test
    fun processing_is_available_while_runtime_is_running() {
        val runtime =
            LiliyaInteractionRuntimeFactory.create()

        runtime.stop()
        runtime.start()

        try {
            assertEquals(
                LiliyaInteractionRuntimeState.RUNNING,
                runtime.state()
            )

            val result =
                runtime.process(
                    request(
                        "interaction-running"
                    )
                )

            assertTrue(
                result.interpretation.isNotBlank()
            )
        } finally {
            runtime.stop()
        }
    }

    @Test
    fun processing_fails_closed_again_after_runtime_stop() {
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

        assertFailsWith<LiliyaInteractionRuntimeException> {
            runtime.process(
                request(
                    "interaction-after-stop"
                )
            )
        }
    }

    @Test
    fun processing_recovers_after_runtime_restart() {
        val runtime =
            LiliyaInteractionRuntimeFactory.create()

        runtime.stop()

        assertFailsWith<LiliyaInteractionRuntimeException> {
            runtime.process(
                request(
                    "interaction-before-restart"
                )
            )
        }

        runtime.start()

        try {
            val result =
                runtime.process(
                    request(
                        "interaction-after-restart"
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

    private fun request(
        source: String
    ): LiliyaInteractionRequest {
        return LiliyaInteractionRequest(
            source = source,
            authority =
                LiliyaInteractionAuthority.USER
        )
    }
}
