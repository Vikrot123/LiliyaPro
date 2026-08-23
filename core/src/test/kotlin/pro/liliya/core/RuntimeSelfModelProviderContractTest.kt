package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.context.*
import pro.liliya.core.runtime.intelligence.selfmodel.DefaultRuntimeSelfModelProvider

class RuntimeSelfModelProviderContractTest {

    @Test
    fun provider_builds_self_model_from_context_sources() {

        val contextProvider =
            object : RuntimeContextProvider {

                override fun currentContext() =
                    object : RuntimeContext {

                        override fun snapshot() =
                            RuntimeContextSnapshot(
                                runtimeState = "RUNNING",
                                activeServices = listOf("runtime"),
                                timestamp = 1L
                            )
                    }
            }

        val provider =
            DefaultRuntimeSelfModelProvider(
                contextProvider
            ) {
                RuntimeContextMetadata(
                    runtimeVersion = "1.187",
                    recoveryAvailable = true,
                    diagnosticsAvailable = true
                )
            }

        val model =
            provider.currentSelfModel()

        assertEquals(
            "RUNNING",
            model.snapshot.runtimeState
        )

        assertEquals(
            "1.187",
            model.metadata.runtimeVersion
        )
    }
}
