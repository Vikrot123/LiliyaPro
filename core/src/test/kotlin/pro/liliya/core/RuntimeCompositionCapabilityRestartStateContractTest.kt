package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.capability.RuntimeCapabilityState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCompositionCapabilityRestartStateContractTest {

    @Test
    fun capability_is_active_after_runtime_restart() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val lifecycleManager =
            composition
                .capabilityInfrastructure()
                .lifecycleManager()

        assertEquals(
            RuntimeCapabilityState.ACTIVE,
            lifecycleManager
                .find(RuntimeCommand.START)
                ?.state
        )

        composition.stopRuntime()

        composition.prepareRuntime()

        composition.startRuntime()

        assertNotNull(
            lifecycleManager.find(RuntimeCommand.START)
        )

        assertEquals(
            RuntimeCapabilityState.ACTIVE,
            lifecycleManager
                .find(RuntimeCommand.START)
                ?.state
        )

        composition.stopRuntime()
    }
}
