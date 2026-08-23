package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class RuntimeSelfModelContractTest {

    @Test
    fun self_model_combines_context_snapshot_and_metadata() {

        val snapshot = RuntimeContextSnapshot(
            runtimeState = "RUNNING",
            activeServices = listOf("runtime"),
            timestamp = 1L
        )

        val metadata = RuntimeContextMetadata(
            runtimeVersion = "1.186",
            recoveryAvailable = true,
            diagnosticsAvailable = true
        )

        val model = RuntimeSelfModel(
            snapshot = snapshot,
            metadata = metadata
        )

        assertEquals(
            "RUNNING",
            model.snapshot.runtimeState
        )

        assertEquals(
            "1.186",
            model.metadata.runtimeVersion
        )
    }
}
