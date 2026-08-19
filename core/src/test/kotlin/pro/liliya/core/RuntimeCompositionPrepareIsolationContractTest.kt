package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.module.ModuleProvider

class RuntimeCompositionPrepareIsolationContractTest {

    @Test
    fun runtime_prepare_does_not_leak_previous_runtime_state() {
        val composition = DefaultRuntimeComposition()

        val originalProvider = composition.runtimeServiceProvider()

        val service = object : RuntimeService {
            override val name = "prepare-isolation-service"
            override val state = RuntimeServiceState.RUNNING

            override fun start() {}
            override fun stop() {}
        }

        composition.runtimeServiceRegistry()
            .register(service)

        val customProvider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return emptyList()
            }
        }

        composition.setRuntimeServiceProvider(customProvider)

        composition.prepareRuntime()

        assertSame(
            originalProvider,
            composition.runtimeServiceProvider()
        )

        assertEquals(
            emptyMap(),
            composition.runtimeServiceRegistry().getStates()
        )

        assertNull(
            composition.moduleManager()
        )

        assertEquals(
            emptyMap(),
            composition.serviceBootstrap().getStates()
        )
    }
}
