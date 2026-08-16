package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.RuntimeCapabilityDefinition
import pro.liliya.core.runtime.capability.RuntimeCapabilityManager
import pro.liliya.core.runtime.capability.DefaultMutableRuntimeCapabilityRegistry
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeCapabilityManagerTest {

    private val manager = RuntimeCapabilityManager(
        DefaultMutableRuntimeCapabilityRegistry()
    )

    @Test
    fun `manager registers capability`() {

        val definition =
            RuntimeCapabilityDefinition(
                command = RuntimeCommand.HEALTH_CHECK,
                minimumAuthority = RuntimeAuthorityLevel.USER,
                description = "Managed health capability"
            )

        manager.register(definition)

        val result =
            manager.find(RuntimeCommand.HEALTH_CHECK)

        assertNotNull(result)
        assertEquals(
            "Managed health capability",
            result?.description
        )
    }

    @Test
    fun `manager unregisters capability`() {

        manager.register(
            RuntimeCapabilityDefinition(
                command = RuntimeCommand.HEALTH_CHECK,
                minimumAuthority = RuntimeAuthorityLevel.USER,
                description = "Temporary capability"
            )
        )

        manager.unregister(
            RuntimeCommand.HEALTH_CHECK
        )

        val result =
            manager.find(RuntimeCommand.HEALTH_CHECK)

        assertNull(result)
    }
}
