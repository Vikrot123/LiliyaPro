package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleRegistryCriticalFailureAbortContractTest {

    @Test
    fun criticalModuleFailureMustAbortStartup() {

        val registry = ModuleRegistry()

        val events = mutableListOf<String>()

        val critical = CriticalFailingModule(events)

        val after = TrackingModule(events)

        registry.register(critical)
        registry.register(after)

        registry.initAll()

        assertThrows(IllegalStateException::class.java) {
            registry.startAll()
        }

        assertTrue(
            events.contains("CRITICAL:start")
        )

        assertFalse(
            events.contains("AFTER:start")
        )
    }


    private class CriticalFailingModule(
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor =
            ModuleDescriptor(
                name = "CRITICAL",
                version = "1.0",
                critical = true,
                dependencies = emptyList()
            )

        override var state =
            ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            events.add("CRITICAL:start")
            throw RuntimeException("critical failure")
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }


    private class TrackingModule(
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor =
            ModuleDescriptor(
                name = "AFTER",
                version = "1.0",
                critical = false,
                dependencies = emptyList()
            )

        override var state =
            ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            events.add("AFTER:start")
            state = ModuleState.RUNNING
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }
}
