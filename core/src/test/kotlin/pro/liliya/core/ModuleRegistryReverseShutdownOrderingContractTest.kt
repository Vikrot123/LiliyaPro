package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleRegistryReverseShutdownOrderingContractTest {

    @Test
    fun dependentModuleMustStopBeforeDependency() {

        val registry = ModuleRegistry()
        val events = mutableListOf<String>()

        val base = TestModule(
            "BASE",
            emptyList(),
            events
        )

        val child = TestModule(
            "CHILD",
            listOf("BASE"),
            events
        )

        registry.register(child)
        registry.register(base)

        registry.initAll()
        registry.startAll()
        events.clear()

        registry.stopAll()

        assertEquals(
            listOf(
                "CHILD:stop",
                "BASE:stop"
            ),
            events
        )
    }


    private class TestModule(
        private val moduleName: String,
        dependencies: List<String>,
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor =
            ModuleDescriptor(
                name = moduleName,
                version = "1.0",
                critical = false,
                dependencies = dependencies
            )

        override var state =
            ModuleState.CREATED


        override fun init() {
            state = ModuleState.INITIALIZED
        }


        override fun start() {
            state = ModuleState.RUNNING
        }


        override fun stop() {
            events.add("$moduleName:stop")
            state = ModuleState.STOPPED
        }
    }
}
