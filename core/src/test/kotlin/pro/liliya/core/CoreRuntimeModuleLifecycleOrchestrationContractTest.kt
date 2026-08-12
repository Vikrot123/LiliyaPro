package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class CoreRuntimeModuleLifecycleOrchestrationContractTest {

    @Test
    fun coreRuntimeMustOrchestrateFullModuleLifecycle() {

        val events = mutableListOf<String>()

        CoreRuntime.setModuleProvider(
            TestModuleProvider(events)
        )

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.state()
        )

        assertEquals(
            listOf(
                "TEST:init",
                "TEST:start"
            ),
            events
        )

        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )

        assertEquals(
            listOf(
                "TEST:init",
                "TEST:start",
                "TEST:stop"
            ),
            events
        )

        CoreRuntime.resetModuleProvider()
    }


    private class TestModuleProvider(
        private val events: MutableList<String>
    ) : ModuleProvider {

        override fun provideModules(): List<LiliyaModule> {
            return listOf(
                TestModule(events)
            )
        }
    }


    private class TestModule(
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor =
            ModuleDescriptor(
                name = "TEST",
                version = "1.0",
                critical = false,
                dependencies = emptyList()
            )

        override var state =
            ModuleState.CREATED


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
