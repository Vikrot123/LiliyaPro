package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleRegistryCriticalFailureContractTest {

    private class CriticalFailModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "CRITICAL_FAIL",
            version = "1.0",
            critical = true
        )

        override var state: ModuleState = ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            throw IllegalStateException("critical failure")
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }

    private class ShouldNotStartModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "SHOULD_NOT_START",
            version = "1.0",
            critical = false
        )

        override var state: ModuleState = ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            state = ModuleState.RUNNING
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }

    @Test
    fun criticalFailureStopsStartupPipeline() {

        val received = mutableListOf<ModuleEvent>()

        ModuleEventBus.clear()

        ModuleEventBus.subscribe {
            received.add(it)
        }

        val registry = ModuleRegistry()

        registry.register(CriticalFailModule())
        registry.register(ShouldNotStartModule())

        registry.initAll()

        assertThrows(IllegalStateException::class.java) {
            registry.startAll()
        }

        assertEquals(
            true,
            received.contains(
                ModuleEvent.Failed(
                    "CRITICAL_FAIL",
                    "start",
                    "critical failure"
                )
            )
        )

        assertEquals(
            false,
            received.contains(
                ModuleEvent.Started("SHOULD_NOT_START")
            )
        )

        ModuleEventBus.clear()
    }
}
