package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCompositionCapabilityRebindContractTest {

    @Test
    fun capability_is_rebound_after_runtime_restart() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val lifecycleManager =
            composition
                .capabilityInfrastructure()
                .lifecycleManager()

        val first =
            lifecycleManager.find(RuntimeCommand.START)

        assertNotNull(first)

        composition.stopRuntime()

        composition.prepareRuntime()

        composition.startRuntime()

        val second =
            lifecycleManager.find(RuntimeCommand.START)

        assertNotNull(second)

        assertNotSame(
            first,
            second
        )

        composition.stopRuntime()
    }
}
