package pro.liliya.interaction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LiliyaInteractionPublicFacadeContractTest {

    @Test
    fun public_facade_owns_interaction_lifecycle_and_processing() {
        LiliyaInteraction.stop()

        assertEquals(
            LiliyaInteractionRuntimeState.STOPPED,
            LiliyaInteraction.state()
        )

        assertFailsWith<LiliyaInteractionRuntimeException> {
            LiliyaInteraction.process(
                request(
                    "public-facade-before-start"
                )
            )
        }

        LiliyaInteraction.start()

        try {
            assertEquals(
                LiliyaInteractionRuntimeState.RUNNING,
                LiliyaInteraction.state()
            )

            val result =
                LiliyaInteraction.process(
                    request(
                        "public-facade-running"
                    )
                )

            assertEquals(
                "public-facade-running",
                result.source
            )

            assertTrue(
                result.interpretation.isNotBlank()
            )

            assertTrue(
                result.confidence >= 0.0
            )
        } finally {
            LiliyaInteraction.stop()
        }

        assertEquals(
            LiliyaInteractionRuntimeState.STOPPED,
            LiliyaInteraction.state()
        )
    }

    @Test
    fun public_facade_supports_runtime_restart() {
        LiliyaInteraction.stop()
        LiliyaInteraction.start()

        assertEquals(
            LiliyaInteractionRuntimeState.RUNNING,
            LiliyaInteraction.state()
        )

        LiliyaInteraction.stop()

        assertEquals(
            LiliyaInteractionRuntimeState.STOPPED,
            LiliyaInteraction.state()
        )

        LiliyaInteraction.start()

        try {
            val result =
                LiliyaInteraction.process(
                    request(
                        "public-facade-after-restart"
                    )
                )

            assertTrue(
                result.interpretation.isNotBlank()
            )
        } finally {
            LiliyaInteraction.stop()
        }
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
