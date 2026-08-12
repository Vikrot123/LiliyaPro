package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleDependencyFailureTest {

    @Test
    fun failedDependencyPreventsDependentModuleFromStarting() {

        val events = mutableListOf<String>()

        val failing = FailingLifecycleModule(
            name = "FAILING",
            dependencies = emptyList(),
            events = events
        )

        val dependent = LifecycleModule(
            name = "DEPENDENT",
            dependencies = listOf("FAILING"),
            events = events
        )

        val registry = ModuleRegistry()

        registry.register(dependent)
        registry.register(failing)

        registry.initAll()
        registry.startAll()

        assertEquals(
            ModuleState.FAILED,
            failing.state
        )

        assertEquals(
            ModuleState.FAILED,
            dependent.state
        )

        assertEquals(
            listOf(
                "FAILING:init",
                "DEPENDENT:init",
                "FAILING:start"
            ),
            events
        )
    }

    private class FailingLifecycleModule(
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
            state = ModuleState.FAILED
            throw IllegalStateException("Intentional failure")
        }

        override fun stop() {
            events.add("$name:stop")
            state = ModuleState.STOPPED
        }
    }

    private class LifecycleModule(
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
