package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleRegistryDependencyFailurePropagationContractTest {

    @Test
    fun failedDependencyMustPreventDependentStart() {

        val registry = ModuleRegistry()

        val events = mutableListOf<String>()

        val base = FailingModule(
            "BASE",
            emptyList(),
            events
        )

        val child = TrackingModule(
            "CHILD",
            listOf("BASE"),
            events
        )

        registry.register(child)
        registry.register(base)

        registry.initAll()
        registry.startAll()

        assertTrue(
            events.contains("BASE:start")
        )

        assertFalse(
            events.contains("CHILD:start")
        )
    }


    private class FailingModule(
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
            events.add("$moduleName:start")
            throw RuntimeException("failure")
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }


    private class TrackingModule(
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
            events.add("$moduleName:init")
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            events.add("$moduleName:start")
            state = ModuleState.RUNNING
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }
}
