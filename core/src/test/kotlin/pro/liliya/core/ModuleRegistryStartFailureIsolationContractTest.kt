package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState

class ModuleRegistryStartFailureIsolationContractTest {

    private class FailingStartModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "FAILING_START",
            version = "1.0",
            critical = false
        )

        override var state: ModuleState = ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            throw IllegalStateException("start failure")
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }

    private class HealthyModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "HEALTHY",
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
    fun nonCriticalStartFailureDoesNotStopOtherModules() {

        val received = mutableListOf<ModuleEvent>()

        ModuleEventBus.clear()

        ModuleEventBus.subscribe {
            received.add(it)
        }

        val registry = ModuleRegistry()

        registry.register(FailingStartModule())
        registry.register(HealthyModule())

        registry.initAll()
        registry.startAll()


        assertEquals(
            true,
            received.contains(
                ModuleEvent.Failed(
                    "FAILING_START",
                    "start",
                    "start failure"
                )
            )
        )

        assertEquals(
            true,
            received.contains(
                ModuleEvent.Started("HEALTHY")
            )
        )

        ModuleEventBus.clear()
    }
}
