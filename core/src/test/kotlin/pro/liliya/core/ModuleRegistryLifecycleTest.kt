package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleRegistryLifecycleTest {

    @Test
    fun lifecycleFollowsDependencyOrder() {

        val events = mutableListOf<String>()

        val core = LifecycleTestModule(
            name = "CORE",
            dependencies = emptyList(),
            events = events
        )

        val memory = LifecycleTestModule(
            name = "MEMORY",
            dependencies = listOf("CORE"),
            events = events
        )

        val ai = LifecycleTestModule(
            name = "AI",
            dependencies = listOf("MEMORY"),
            events = events
        )

        val registry = ModuleRegistry()

        registry.register(ai)
        registry.register(memory)
        registry.register(core)

        registry.initAll()

        assertEquals(
            listOf(
                "CORE:init",
                "MEMORY:init",
                "AI:init"
            ),
            events
        )

        registry.startAll()

        assertEquals(
            listOf(
                "CORE:init",
                "MEMORY:init",
                "AI:init",
                "CORE:start",
                "MEMORY:start",
                "AI:start"
            ),
            events
        )

        registry.stopAll()

        assertEquals(
            listOf(
                "CORE:init",
                "MEMORY:init",
                "AI:init",
                "CORE:start",
                "MEMORY:start",
                "AI:start",
                "AI:stop",
                "MEMORY:stop",
                "CORE:stop"
            ),
            events
        )
    }

    private class LifecycleTestModule(
        override val name: String,
        dependencies: List<String>,
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = name,
            version = "1.0",
            critical = false,
            dependencies = dependencies
        )

        override var state = ModuleState.CREATED

        override fun init() {
            events.add("$name:init")
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            events.add("$name:start")
            state = ModuleState.RUNNING
        }

        override fun stop() {
            events.add("$name:stop")
            state = ModuleState.STOPPED
        }
    }
}
