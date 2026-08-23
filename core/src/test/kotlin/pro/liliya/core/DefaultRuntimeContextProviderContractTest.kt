package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
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
            DefaultRuntimeContextProvider(registry)

        val snapshot =
            provider.currentContext().snapshot()

        assertEquals(
            listOf("context-service"),
            snapshot.activeServices
        )
    }
}
