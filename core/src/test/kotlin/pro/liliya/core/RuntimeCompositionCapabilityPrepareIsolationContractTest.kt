package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.capability.RuntimeManagedCapability
import pro.liliya.core.runtime.capability.RuntimeCapabilityDefinition
import pro.liliya.core.runtime.capability.RuntimeCapabilityState
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCapabilityPrepareIsolationContractTest {

    @Test
    fun capability_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val lifecycleManager =
            composition
                .capabilityInfrastructure()
                .lifecycleManager()

        lifecycleManager.register(
            RuntimeManagedCapability(
                definition = RuntimeCapabilityDefinition(
                    command = RuntimeCommand.START,
                    minimumAuthority = RuntimeAuthorityLevel.SYSTEM,
                    description = "test capability"
                ),
                state = RuntimeCapabilityState.ACTIVE
            )
        )

        assertNotNull(
            lifecycleManager.find(RuntimeCommand.START)
        )

        composition.prepareRuntime()

        assertNull(
            lifecycleManager.find(RuntimeCommand.START)
        )
    }
}
