package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.DefaultMutableRuntimeCapabilityRegistry
import pro.liliya.core.runtime.capability.RuntimeCapabilityDefinition
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeCapabilityRegistryMutationTest {

    private val registry =
        DefaultMutableRuntimeCapabilityRegistry()


    @Test
    fun `registry can register capability`() {
        val definition =
            RuntimeCapabilityDefinition(
                command = RuntimeCommand.HEALTH_CHECK,
                minimumAuthority = RuntimeAuthorityLevel.USER,
                description = "Dynamic health capability"
            )

        registry.register(definition)

        val result =
            registry.find(RuntimeCommand.HEALTH_CHECK)

        assertNotNull(result)
        assertEquals(
            "Dynamic health capability",
            result?.description
        )
    }


    @Test
    fun `registry can unregister capability`() {
        val definition =
            RuntimeCapabilityDefinition(
                command = RuntimeCommand.HEALTH_CHECK,
                minimumAuthority = RuntimeAuthorityLevel.USER,
                description = "Temporary capability"
            )

        registry.register(definition)

        registry.unregister(
            RuntimeCommand.HEALTH_CHECK
        )

        val result =
            registry.find(RuntimeCommand.HEALTH_CHECK)

        assertNull(result)
    }
}
