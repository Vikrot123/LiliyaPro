package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeRecoverySupervisorPrepareIsolationContractTest {

    @Test
    fun runtime_supervisor_restart_counters_are_cleared_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val service = object : RuntimeService {
            override val name = "test-service"

            override val state =
                RuntimeServiceState.RUNNING

            override fun start() {
            }

            override fun stop() {
            }
        }

        composition.runtimeServiceRegistry()
            .register(service)

        val recovered =
            composition.runtimeSupervisor()
                .recover("test-service")

        assertEquals(true, recovered)

        assertEquals(
            1,
            composition.runtimeSupervisor()
                .getRestartCount("test-service")
        )

        composition.prepareRuntime()

        assertEquals(
            0,
            composition.runtimeSupervisor()
                .getRestartCount("test-service")
        )
    }
}
