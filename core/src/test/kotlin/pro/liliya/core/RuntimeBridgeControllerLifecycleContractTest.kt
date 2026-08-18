package pro.liliya.core

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import kotlin.test.Test
import kotlin.test.assertEquals

class RuntimeBridgeControllerLifecycleContractTest {

    @Test
    fun moduleEventBridgeInstallMustBeIdempotent() {
        RuntimeEventBus.clear()
        ModuleEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.subscribe { event ->
            events.add(event)
        }

        val composition = DefaultRuntimeComposition()

        composition.installModuleEventBridge()
        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "TEST_MODULE",
                phase = "START",
                reason = "failure"
            )
        )

        assertEquals(
            1,
            events.count { it is RuntimeEvent.ModuleFailed }
        )

        composition.uninstallModuleEventBridge()
        composition.uninstallModuleEventBridge()

        RuntimeEventBus.clear()
        ModuleEventBus.clear()
    }

    @Test
    fun moduleEventBridgeCanRestartWithoutDuplicateDelivery() {
        RuntimeEventBus.clear()
        ModuleEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.subscribe { event ->
            events.add(event)
        }

        val composition = DefaultRuntimeComposition()

        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "TEST_MODULE",
                phase = "START",
                reason = "first"
            )
        )

        composition.uninstallModuleEventBridge()

        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "TEST_MODULE",
                phase = "START",
                reason = "second"
            )
        )

        assertEquals(
            2,
            events.count { it is RuntimeEvent.ModuleFailed }
        )

        composition.uninstallModuleEventBridge()

        RuntimeEventBus.clear()
        ModuleEventBus.clear()
    }
}
