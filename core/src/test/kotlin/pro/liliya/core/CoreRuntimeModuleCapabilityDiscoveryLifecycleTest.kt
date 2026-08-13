package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.*
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.module.DefaultRuntimeModuleCapabilityLifecycle

class CoreRuntimeModuleCapabilityDiscoveryLifecycleTest {

    private val lifecycleManager =
        DefaultRuntimeCapabilityLifecycleManager()

    private val binder =
        DefaultRuntimeModuleCapabilityBinder(
            lifecycleManager
        )

    private val fakeProvider =
        object : RuntimeCapabilityProvider {
            override fun capabilities():
                List<RuntimeCapabilityDefinition> =
                listOf(
                    RuntimeCapabilityDefinition(
                        command = RuntimeCommand.HEALTH_CHECK,
                        minimumAuthority = RuntimeAuthorityLevel.USER,
                        description = "Discovery injected capability"
                    )
                )
        }

    private val discovery =
        object : RuntimeCapabilityDiscovery {

            override fun discover(
                module: LiliyaModule
            ): RuntimeCapabilityProvider {
                return fakeProvider
            }
        }

    private val lifecycle =
        DefaultRuntimeModuleCapabilityLifecycle(
            binder,
            discovery
        )

    private val module =
        object : LiliyaModule {

            override val descriptor =
                ModuleDescriptor(
                    name = "DiscoveryLifecycleModule",
                    version = "1.0",
                    critical = false
                )

            override var state =
                ModuleState.CREATED

            override fun init() {}
            override fun start() {}
            override fun stop() {}
        }


    @Test
    fun `lifecycle uses discovery provider`() {

        lifecycle.onModuleInit(module)

        val capability =
            lifecycleManager.find(
                RuntimeCommand.HEALTH_CHECK
            )

        assertEquals(
            RuntimeCapabilityState.ACTIVE,
            capability?.state
        )
    }
}
