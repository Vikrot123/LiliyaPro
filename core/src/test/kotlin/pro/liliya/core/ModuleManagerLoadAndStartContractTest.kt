package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleManagerLoadAndStartContractTest {

    @Test
    fun moduleManagerMustLoadAndStartProvidedModules() {
        val events = mutableListOf<String>()

        val module = TestModule(events)

        val manager = ModuleManager(
            registry = ModuleRegistry(),
            provider = TestModuleProvider(module)
        )

        manager.loadModules()

        assertEquals(
            listOf("TEST:created"),
            events
        )

        assertEquals(
            mapOf("TEST" to ModuleState.CREATED),
            manager.getModuleStates()
        )

        manager.startModules()

        assertEquals(
            listOf(
                "TEST:created",
                "TEST:init",
                "TEST:start"
            ),
            events
        )

        assertEquals(
            mapOf("TEST" to ModuleState.RUNNING),
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

        init {
            events.add("TEST:created")
        }

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
