package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeServiceRegistryPrepareIsolationContractTest {

    @Test
    fun runtime_service_registry_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val service = object : RuntimeService {
            override val name = "test-service"

            override val state = RuntimeServiceState.RUNNING

            override fun start() {
            }

            override fun stop() {
            }
        }

        composition.runtimeServiceRegistry()
            .register(service)

        assertEquals(
            RuntimeServiceState.RUNNING,
            composition.runtimeServiceRegistry()
                .getStates()["test-service"]
        )

        composition.prepareRuntime()

        assertEquals(
            emptyMap(),
            composition.runtimeServiceRegistry()
                .getStates()
        )
    }
}
