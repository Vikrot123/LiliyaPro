package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.capability.*

class CoreRuntimeCapabilityDiscoveryRegistryTest {

    private val registry =
        DefaultRuntimeCapabilityDiscoveryRegistry()


    @Test
    fun `registry returns provider from registered discovery`() {

        val provider =
            object : RuntimeCapabilityProvider {

                override fun capabilities():
                    List<RuntimeCapabilityDefinition> =
                    emptyList()
            }


        registry.register(
            object : RuntimeCapabilityDiscovery {

                override fun discover(
                    module: LiliyaModule
                ): RuntimeCapabilityProvider? {
                    return provider
                }
            }
        )


        val module =
            object : LiliyaModule {

                override val descriptor =
                    ModuleDescriptor(
                        name = "RegistryTestModule",
                        version = "1.0",
                        critical = false
                    )

                override var state =
                    ModuleState.CREATED

                override fun init() {}
                override fun start() {}
                override fun stop() {}
            }


        assertNotNull(
            registry.discover(module)
        )
    }
}
