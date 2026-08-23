package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata

class RuntimeContextMetadataContractTest {

    @Test
    fun metadata_preserves_intelligence_runtime_information() {

        val metadata = RuntimeContextMetadata(
            runtimeVersion = "1.185",
            recoveryAvailable = true,
            diagnosticsAvailable = true
        )

        assertEquals(
            "1.185",
            metadata.runtimeVersion
        )

        assertTrue(
            metadata.recoveryAvailable
        )

        assertTrue(
            metadata.diagnosticsAvailable
        )
    }
}
