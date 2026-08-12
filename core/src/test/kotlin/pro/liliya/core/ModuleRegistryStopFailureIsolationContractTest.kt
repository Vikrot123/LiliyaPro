package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.module.*

class ModuleRegistryStopFailureIsolationContractTest {

    @Test
    fun stopFailureMustNotPreventOtherModulesFromStopping() {
        val registry = ModuleRegistry()
        val events = mutableListOf<String>()

        val failing = FailingStopModule(events)
        val after = TrackingModule(events)

        registry.register(failing)
        registry.register(after)

        registry.initAll()
        registry.startAll()

        events.clear()

        registry.stopAll()

        assertTrue(
            events.contains("FAILING:stop")
        )

        assertTrue(
            events.contains("AFTER:stop")
        )
    }

    private class FailingStopModule(
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "FAILING",
            version = "1.0",
            critical = false,
            dependencies = emptyList()
        )

        override var state = ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            state = ModuleState.RUNNING
        }

        override fun stop() {
            events.add("FAILING:stop")
            throw RuntimeException("stop failure")
        }
    }

    private class TrackingModule(
        private val events: MutableList<String>
    ) : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "AFTER",
            version = "1.0",
            critical = false,
            dependencies = emptyList()
        )

        override var state = ModuleState.CREATED

        override fun init() {
            state = ModuleState.INITIALIZED
        }

        override fun start() {
            state = ModuleState.RUNNING
        }

        override fun stop() {
            events.add("AFTER:stop")
            state = ModuleState.STOPPED
        }
    }
}
