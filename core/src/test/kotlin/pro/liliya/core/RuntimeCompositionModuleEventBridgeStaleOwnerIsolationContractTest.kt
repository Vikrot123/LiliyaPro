package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeStaleOwnerIsolationContractTest {

    @Test
    fun stale_previous_owner_stop_does_not_disable_current_bridge() {
        ModuleEventBus.clear()
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = { event ->
            events.add(event)
        }

        RuntimeEventBus.subscribe(listener)

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        try {
            first.installModuleEventBridge()

            second.installModuleEventBridge()

            assertFalse(
                first
                    .runtimeModuleBridgeStateHolder()
                    .isModuleEventBridgeInstalled()
            )

            assertTrue(
                second
                    .runtimeModuleBridgeStateHolder()
                    .isModuleEventBridgeInstalled()
            )

            // first is now stale. Its later shutdown must not
            // damage the bridge currently owned by second.
            first.stopRuntimeBridges()

            assertTrue(
                second
                    .runtimeModuleBridgeStateHolder()
                    .isModuleEventBridgeInstalled()
            )

            ModuleEventBus.publish(
                ModuleEvent.Failed(
                    moduleName = "current-owner-module",
                    phase = "START",
                    reason = "failure"
                )
            )

            val failures =
                events.filterIsInstance<RuntimeEvent.ModuleFailed>()

            assertEquals(
                1,
                failures.size
            )

            assertEquals(
                "current-owner-module",
                failures.single().moduleName
            )
        } finally {
            first.stopRuntimeBridges()
            second.stopRuntimeBridges()

            RuntimeEventBus.unsubscribe(listener)

            ModuleEventBus.clear()
            RuntimeEventBus.clear()
        }
    }
}
