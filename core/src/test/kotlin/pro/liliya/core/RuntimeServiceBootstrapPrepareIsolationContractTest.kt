package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeServiceBootstrapPrepareIsolationContractTest {

    @Test
    fun runtime_services_are_cleared_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val service = object : RuntimeService {
            override val name = "prepare-test-service"

            override val state =
                RuntimeServiceState.RUNNING

            override fun start() {
            }

            override fun stop() {
            }
        }

        composition.registerRuntimeService(service)

        composition.prepareRuntime()

        assertFalse(
            composition
                .runtimeServiceStates()
                .containsKey("prepare-test-service")
        )
    }

    @Test
    fun new_bootstrap_does_not_keep_previous_services() {
        val composition = DefaultRuntimeComposition()

        val service = object : RuntimeService {
            override val name = "old-service"

            override val state =
                RuntimeServiceState.RUNNING

            override fun start() {
            }

            override fun stop() {
            }
        }

        composition.registerRuntimeService(service)

        composition.prepareRuntime()

        assertTrue(
            composition
                .runtimeServiceStates()
                .isEmpty()
        )
    }
}
