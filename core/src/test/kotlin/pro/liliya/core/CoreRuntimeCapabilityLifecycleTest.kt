package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityLifecycleManager
import pro.liliya.core.runtime.capability.RuntimeCapabilityDefinition
import pro.liliya.core.runtime.capability.RuntimeCapabilityState
import pro.liliya.core.runtime.capability.RuntimeManagedCapability
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeCapabilityLifecycleTest {

    private val manager =
        DefaultRuntimeCapabilityLifecycleManager()

    private fun createCapability() =
        RuntimeManagedCapability(
            RuntimeCapabilityDefinition(
                command = RuntimeCommand.HEALTH_CHECK,
                minimumAuthority = RuntimeAuthorityLevel.USER,
                description = "Lifecycle health capability"
            )
        )

    @Test
    fun `capability lifecycle transitions correctly`() {

        manager.register(
            createCapability()
        )

        var capability =
            manager.find(RuntimeCommand.HEALTH_CHECK)

        assertNotNull(capability)
        assertEquals(
            RuntimeCapabilityState.REGISTERED,
            capability?.state
        )

        manager.activate(
            RuntimeCommand.HEALTH_CHECK
        )

        capability =
            manager.find(RuntimeCommand.HEALTH_CHECK)

        assertEquals(
            RuntimeCapabilityState.ACTIVE,
            capability?.state
        )

        manager.disable(
            RuntimeCommand.HEALTH_CHECK
        )

        capability =
            manager.find(RuntimeCommand.HEALTH_CHECK)

        assertEquals(
            RuntimeCapabilityState.DISABLED,
            capability?.state
        )

        manager.remove(
            RuntimeCommand.HEALTH_CHECK
        )

        capability =
            manager.find(RuntimeCommand.HEALTH_CHECK)

        assertEquals(
            RuntimeCapabilityState.REMOVED,
            capability?.state
        )
    }
}
