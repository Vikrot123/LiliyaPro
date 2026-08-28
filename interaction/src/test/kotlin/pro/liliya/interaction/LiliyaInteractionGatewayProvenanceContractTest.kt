package pro.liliya.interaction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LiliyaInteractionGatewayProvenanceContractTest {

    @Test
    fun gateway_passes_normalized_source_and_interaction_authority() {
        var receivedSource: String? = null
        var receivedAuthority:
            LiliyaInteractionAuthority? = null

        val expected =
            LiliyaInteractionResult(
                source = "interaction provenance",
                interpretation = "result",
                confidence = 1.0,
                experienceCommitted = false,
                knowledgeProduced = false
            )

        val port =
            object : LiliyaInteractionPort {
                override fun process(
                    source: String,
                    authority: LiliyaInteractionAuthority
                ): LiliyaInteractionResult {
                    receivedSource = source
                    receivedAuthority = authority
                    return expected
                }
            }

        val result =
            LiliyaInteractionGateway(port)
                .process(
                    LiliyaInteractionRequest(
                        source =
                            "   interaction provenance   ",
                        authority =
                            LiliyaInteractionAuthority.USER
                    )
                )

        assertEquals(
            "interaction provenance",
            receivedSource
        )

        assertEquals(
            LiliyaInteractionAuthority.USER,
            receivedAuthority
        )

        assertSame(
            expected,
            result
        )
    }
}
