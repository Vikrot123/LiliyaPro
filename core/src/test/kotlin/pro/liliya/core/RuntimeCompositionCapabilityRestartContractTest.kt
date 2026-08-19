package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCompositionCapabilityRestartContractTest {

    @Test
    fun capability_is_restored_after_runtime_restart() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val lifecycleManager =
            composition
                .capabilityInfrastructure()
                .lifecycleManager()

        assertNotNull(
            lifecycleManager.find(RuntimeCommand.START)
        )

        composition.stopRuntime()

        composition.prepareRuntime()

        assertNull(
            lifecycleManager.find(RuntimeCommand.START)
        )

        composition.startRuntime()

        assertNotNull(
            lifecycleManager.find(RuntimeCommand.START)
        )

        composition.stopRuntime()
    }
}
