package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleManagerStopContractTest {

    @Test
    fun moduleManagerMustStopAllLoadedModules() {
        val events = mutableListOf<String>()
        val module = TestModule(events)

        val manager = ModuleManager(
            registry = ModuleRegistry(),
            provider = TestModuleProvider(module)
        )

        manager.loadModules()
        manager.startModules()

        events.clear()

        manager.stopModules()

        assertEquals(
            listOf("TEST:stop"),
            events
        )

        assertEquals(
            mapOf("TEST" to ModuleState.STOPPED),
            manager.getModuleStates()
        )
    }

    private class TestModuleProvider(
        private val module: LiliyaModule
    ) : ModuleProvider {

        override fun provideModules(): List<LiliyaModule> {
            return listOf(module)
        }
    }

    private class TestModule(
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "TEST",
            version = "1.0",
            critical = false,
            dependencies = emptyList()
        )

        override var state = ModuleState.CREATED

        override fun init() {
            events.add("TEST:init")
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            events.add("TEST:start")
            state = ModuleState.RUNNING
        }

        override fun stop() {
            events.add("TEST:stop")
            state = ModuleState.STOPPED
        }
    }
}
