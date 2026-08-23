package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.context.RuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot

class RuntimeContextProviderContractTest {

    @Test
    fun provider_returns_runtime_context() {

        val provider = object : RuntimeContextProvider {

            override fun currentContext() =
                object : pro.liliya.core.runtime.intelligence.context.RuntimeContext {

                    override fun snapshot(): RuntimeContextSnapshot {
                        return RuntimeContextSnapshot(
                            runtimeState = "RUNNING",
                            activeServices = listOf("runtime"),
                            timestamp = 1L
                        )
                    }
                }
        }

        val snapshot =
            provider.currentContext().snapshot()

        assertEquals(
            "RUNNING",
            snapshot.runtimeState
        )

        assertEquals(
            listOf("runtime"),
            snapshot.activeServices
        )
    }
}
