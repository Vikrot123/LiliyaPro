package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleRegistryLifecycleIsolationContractTest {

    @Test
    fun moduleRegistryMustNotDuplicateModulesAcrossLifecycle() {

        val registry = ModuleRegistry()

        ModuleEventBus.clear()

        val module = TestModule()

        registry.register(module)

        registry.initAll()
        registry.startAll()
        registry.stopAll()

        registry.initAll()
        registry.startAll()
        registry.stopAll()

        val states = registry.getStates()

        assertEquals(
            1,
            states.size
        )

        assertEquals(
            ModuleState.STOPPED,
            states["TEST_MODULE"]
        )

        ModuleEventBus.clear()
    }


    private class TestModule : pro.liliya.core.module.LiliyaModule {

        override val descriptor =
            pro.liliya.core.module.ModuleDescriptor(
                name = "TEST_MODULE",
                version = "1.0",
                critical = false,
                dependencies = emptyList()
            )

        override var state =
            pro.liliya.core.module.ModuleState.CREATED

        override fun init() {
            state = pro.liliya.core.module.ModuleState.INITIALIZED
        }

        override fun start() {
            state = pro.liliya.core.module.ModuleState.RUNNING
        }

        override fun stop() {
            state = pro.liliya.core.module.ModuleState.STOPPED
        }
    }
}
