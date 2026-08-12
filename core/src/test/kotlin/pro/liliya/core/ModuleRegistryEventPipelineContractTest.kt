package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.CoreModule
import pro.liliya.core.module.ModuleRegistry


class ModuleRegistryEventPipelineContractTest {

    @Test
    fun moduleLifecyclePublishesEvents() {

        val received = mutableListOf<ModuleEvent>()

        ModuleEventBus.clear()

        ModuleEventBus.subscribe {
            received.add(it)
        }

        val registry = ModuleRegistry()

        registry.register(
            CoreModule()
        )

        registry.initAll()
        registry.startAll()
        registry.stopAll()

        

        assertEquals(
            4,
            received.size
        )

        assertEquals(
            ModuleEvent.Loaded("CORE_MODULE"),
            received[0]
        )

        assertEquals(
            ModuleEvent.Initialized("CORE_MODULE"),
            received[1]
        )

        assertEquals(
            ModuleEvent.Started("CORE_MODULE"),
            received[2]
        )

        assertEquals(
            ModuleEvent.Stopped("CORE_MODULE"),
            received[3]
        )

        ModuleEventBus.clear()
    }
}
