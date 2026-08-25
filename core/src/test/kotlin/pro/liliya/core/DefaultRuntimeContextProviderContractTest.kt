package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.CoreRuntimeStateHolder
import pro.liliya.core.runtime.intelligence.context.DefaultRuntimeContextProvider

class DefaultRuntimeContextProviderContractTest {

    @Test
    fun provider_reads_registered_runtime_services() {

        val registry = RuntimeServiceRegistry()

        registry.register(
            object : RuntimeService {

                override val name = "context-service"

                override var state =
                    RuntimeServiceState.RUNNING

                override fun start() {}

                override fun stop() {}
            }
        )

        val provider =
            DefaultRuntimeContextProvider(
                registry = registry,
                runtimeStateHolder = CoreRuntimeStateHolder()
            )

        val snapshot =
            provider.currentContext().snapshot()

        assertEquals(
            listOf("context-service"),
            snapshot.activeServices
        )
    }


    @Test
    fun provider_should_expose_runtime_state_from_state_holder() {
        val registry = RuntimeServiceRegistry()
        val stateHolder = CoreRuntimeStateHolder()

        val provider = DefaultRuntimeContextProvider(
            registry = registry,
            runtimeStateHolder = stateHolder
        )

        assertEquals(
            CoreRuntimeState.STOPPED.name,
            provider.currentContext().snapshot().runtimeState
        )

        stateHolder.setState(CoreRuntimeState.STARTING)

        assertEquals(
            CoreRuntimeState.STARTING.name,
            provider.currentContext().snapshot().runtimeState
        )

        stateHolder.setState(CoreRuntimeState.RUNNING)

        assertEquals(
            CoreRuntimeState.RUNNING.name,
            provider.currentContext().snapshot().runtimeState
        )

        stateHolder.setState(CoreRuntimeState.FAILED)

        assertEquals(
            CoreRuntimeState.FAILED.name,
            provider.currentContext().snapshot().runtimeState
        )
    }

}
