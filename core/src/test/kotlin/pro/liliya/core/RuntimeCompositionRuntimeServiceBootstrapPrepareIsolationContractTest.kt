package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeServiceBootstrapPrepareIsolationContractTest {

    @Test
    fun runtime_service_bootstrap_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val service = object : RuntimeService {
            override val name = "bootstrap-test-service"

            override val state = RuntimeServiceState.RUNNING

            override fun start() {
            }

            override fun stop() {
            }
        }

        val bootstrap = composition.serviceBootstrap()

        bootstrap.register(service)
        bootstrap.start()

        assertEquals(
            RuntimeServiceState.RUNNING,
            bootstrap.getStates()["bootstrap-test-service"]
        )

        composition.prepareRuntime()

        val freshBootstrap = composition.serviceBootstrap()

        assertEquals(
            emptyMap(),
            freshBootstrap.getStates()
        )
    }
}
