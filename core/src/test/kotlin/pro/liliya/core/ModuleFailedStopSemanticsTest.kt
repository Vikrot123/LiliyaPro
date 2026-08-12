package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleFailedStopSemanticsTest {

    @Test
    fun failedModuleRemainsFailedAfterStop() {

        val events = mutableListOf<String>()

        val failing = FailingModule(events)

        val registry = ModuleRegistry()

        registry.register(failing)

        registry.initAll()
        registry.startAll()

        assertEquals(
            ModuleState.FAILED,
            failing.state
        )

        registry.stopAll()

        assertEquals(
            ModuleState.FAILED,
            failing.state
        )

        assertEquals(
            listOf(
                "FAILING:init",
                "FAILING:start",
                "FAILING:stop"
            ),
            events
        )
    }

    private class FailingModule(
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "FAILING",
            version = "1.0",
            critical = false
        )

        override var state = ModuleState.CREATED

        override fun init() {
            events.add("FAILING:init")
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            events.add("FAILING:start")
            state = ModuleState.FAILED
            throw IllegalStateException("Intentional failure")
        }

        override fun stop() {
            events.add("FAILING:stop")
            state = ModuleState.STOPPED
        }
    }
}
