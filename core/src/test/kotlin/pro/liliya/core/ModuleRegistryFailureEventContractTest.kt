package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleRegistry
import pro.liliya.core.module.ModuleState
import pro.liliya.core.module.LiliyaModule

class ModuleRegistryFailureEventContractTest {

    private class FailingInitModule : LiliyaModule {

        override val descriptor = ModuleDescriptor(
            name = "FAILING_INIT",
            version = "1.0",
            critical = false
        )

        override var state: ModuleState = ModuleState.CREATED

        override fun init() {
            throw IllegalStateException("init failure")
        }

        override fun start() {
            state = ModuleState.RUNNING
        }

        override fun stop() {
            state = ModuleState.STOPPED
        }
    }

    @Test
    fun failedInitPublishesFailureEvent() {
        val received = mutableListOf<ModuleEvent>()

        ModuleEventBus.clear()

        ModuleEventBus.subscribe {
            received.add(it)
        }

        val registry = ModuleRegistry()

        registry.register(FailingInitModule())
        registry.initAll()

        println("RECEIVED EVENTS = $received")

        assertEquals(
            2,
            received.size
        )

        assertEquals(
            ModuleEvent.Loaded("FAILING_INIT"),
            received[0]
        )

        assertEquals(
            ModuleEvent.Failed(
                moduleName = "FAILING_INIT",
                phase = "init",
                reason = "init failure"
            ),
            received[1]
        )

        ModuleEventBus.clear()
    }
}
