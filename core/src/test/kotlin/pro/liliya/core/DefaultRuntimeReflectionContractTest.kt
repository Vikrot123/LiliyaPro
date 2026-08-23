package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.reflection.DefaultRuntimeReflection
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeReflectionContractTest {

    @Test
    fun reflection_detects_healthy_runtime() {

        val model = RuntimeSelfModel(
            snapshot = RuntimeContextSnapshot(
                runtimeState = "RUNNING",
                activeServices = listOf("runtime"),
                timestamp = 1L
            ),
            metadata = RuntimeContextMetadata(
                runtimeVersion = "1",
                recoveryAvailable = true,
                diagnosticsAvailable = true
            )
        )

        val result =
            DefaultRuntimeReflection()
                .analyze(model)

        assertTrue(result.healthy)
    }

    @Test
    fun reflection_detects_unhealthy_runtime() {

        val model = RuntimeSelfModel(
            snapshot = RuntimeContextSnapshot(
                runtimeState = "STOPPED",
                activeServices = emptyList(),
                timestamp = 1L
            ),
            metadata = RuntimeContextMetadata(
                runtimeVersion = "1",
                recoveryAvailable = true,
                diagnosticsAvailable = true
            )
        )

        val result =
            DefaultRuntimeReflection()
                .analyze(model)

        assertFalse(result.healthy)
    }
}
