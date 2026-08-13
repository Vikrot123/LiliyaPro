package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityLifecycleManager
import pro.liliya.core.runtime.capability.DefaultRuntimeModuleCapabilityBinder
import pro.liliya.core.runtime.capability.RuntimeCapabilityDefinition
import pro.liliya.core.runtime.capability.RuntimeCapabilityProvider
import pro.liliya.core.runtime.capability.RuntimeCapabilityState
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.module.CapabilityAwareModule
import pro.liliya.core.runtime.module.DefaultRuntimeModuleCapabilityLifecycle

class CoreRuntimeModuleCapabilityLifecycleTest {

    private val lifecycleManager =
        DefaultRuntimeCapabilityLifecycleManager()

    private val moduleLifecycle =
        DefaultRuntimeModuleCapabilityLifecycle(
            DefaultRuntimeModuleCapabilityBinder(
                lifecycleManager
            )
        )

    private val module =
        object : LiliyaModule,
            CapabilityAwareModule {

            override val descriptor =
                ModuleDescriptor(
                    name = "TestCapabilityModule",
                    version = "1.0",
                    critical = false
                )

            override var state =
                ModuleState.CREATED

            override fun init() {
                state = ModuleState.INITIALIZED
            }

            override fun start() {
                state = ModuleState.RUNNING
            }

            override fun stop() {
                state = ModuleState.STOPPED
            }

            override fun capabilityProvider() =
                object : RuntimeCapabilityProvider {

                    override fun capabilities() =
                        listOf(
                            RuntimeCapabilityDefinition(
                                command = RuntimeCommand.HEALTH_CHECK,
                                minimumAuthority = RuntimeAuthorityLevel.USER,
                                description = "Lifecycle module capability"
                            )
                        )
                }
        }

    @Test
    fun `module init activates capability`() {

        moduleLifecycle.onModuleInit(module)

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
    fun `module shutdown removes capability`() {

        moduleLifecycle.onModuleInit(module)

        moduleLifecycle.onModuleShutdown(module)

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
