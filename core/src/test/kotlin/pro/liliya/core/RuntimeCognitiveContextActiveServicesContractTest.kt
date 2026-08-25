package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.intelligence.context.DefaultRuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.source.DefaultRuntimeCognitiveContextSource

class RuntimeCognitiveContextActiveServicesContractTest {

    @Test
    fun active_services_should_contain_only_running_services() {
        val registry = RuntimeServiceRegistry()

        registry.register(TestService("created", RuntimeServiceState.CREATED))
        registry.register(TestService("running", RuntimeServiceState.RUNNING))
        registry.register(TestService("stopped", RuntimeServiceState.STOPPED))
        registry.register(TestService("failed", RuntimeServiceState.FAILED))

        val provider = DefaultRuntimeContextProvider(
            registry = registry,
            runtimeStateHolder = CoreRuntimeStateHolder()
        )

        val source = DefaultRuntimeCognitiveContextSource(provider)

        val snapshot = source.snapshot(CognitiveContextType.WORKING)

        assertEquals(
            listOf("running"),
            snapshot.values["activeServices"]
        )
    }

    private class TestService(
        override val name: String,
        override var state: RuntimeServiceState
    ) : RuntimeService {

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
