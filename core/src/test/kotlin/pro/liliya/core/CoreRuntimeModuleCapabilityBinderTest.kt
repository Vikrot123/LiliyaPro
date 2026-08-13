package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.DefaultRuntimeModuleCapabilityBinder
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityLifecycleManager
import pro.liliya.core.runtime.capability.RuntimeCapabilityDefinition
import pro.liliya.core.runtime.capability.RuntimeCapabilityLifecycleManager
import pro.liliya.core.runtime.capability.RuntimeCapabilityProvider
import pro.liliya.core.runtime.capability.RuntimeCapabilityState
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeModuleCapabilityBinderTest {

    private val lifecycleManager =
        DefaultRuntimeCapabilityLifecycleManager()

    private val binder =
        DefaultRuntimeModuleCapabilityBinder(
            lifecycleManager
        )

    private val provider =
        object : RuntimeCapabilityProvider {

            override fun capabilities() =
                listOf(
                    RuntimeCapabilityDefinition(
                        command = RuntimeCommand.HEALTH_CHECK,
                        minimumAuthority = RuntimeAuthorityLevel.USER,
                        description = "Module health capability"
                    )
                )
        }

    @Test
    fun `binding module activates capabilities`() {

        binder.bind(provider)

        val capability =
            lifecycleManager.find(
                RuntimeCommand.HEALTH_CHECK
            )

        assertNotNull(capability)

        assertEquals(
            RuntimeCapabilityState.ACTIVE,
            capability?.state
        )
    }

    @Test
    fun `unbinding module removes capabilities`() {

        binder.bind(provider)

        binder.unbind(provider)

        val capability =
            lifecycleManager.find(
                RuntimeCommand.HEALTH_CHECK
            )

        assertEquals(
            RuntimeCapabilityState.REMOVED,
            capability?.state
        )
    }
}
