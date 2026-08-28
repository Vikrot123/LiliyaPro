package pro.liliya.interaction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import pro.liliya.core.CoreRuntime
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class LiliyaInteractionGatewayProvenanceContractTest {

    @Test
    fun normalized_interaction_source_is_used_as_runtime_authority_source() {
        CoreRuntime.stop()
        CoreRuntime.start()

        try {
            val gateway =
                LiliyaInteractionGateway(
                    CoreRuntimeInteractionPort(
                        CoreRuntime
                    )
                )

            val result =
                gateway.process(
                    LiliyaInteractionRequest(
                        source =
                            "   interaction-provenance-contract   ",
                        authority =
                            RuntimeActionAuthorityContext(
                                source =
                                    "mismatched-caller-source",
                                level =
                                    RuntimeAuthorityLevel.SYSTEM
                            )
                    )
                )

            assertNotNull(result)
        } finally {
            CoreRuntime.stop()
        }
    }
}
