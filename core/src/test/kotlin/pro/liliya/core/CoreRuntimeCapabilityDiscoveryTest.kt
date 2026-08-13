package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pro.liliya.core.module.LiliyaModule
import pro.liliya.core.module.ModuleDescriptor
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityDiscovery
import pro.liliya.core.runtime.capability.RuntimeCapabilityProvider
import pro.liliya.core.runtime.capability.RuntimeCapabilityDefinition
import pro.liliya.core.runtime.module.CapabilityAwareModule

class CoreRuntimeCapabilityDiscoveryTest {

    private val discovery =
        DefaultRuntimeCapabilityDiscovery()


    @Test
    fun `discovers capability provider from aware module`() {

        val module =
            object : LiliyaModule, CapabilityAwareModule {

                override val descriptor =
                    ModuleDescriptor(
                        name = "DiscoveryTestModule",
                        version = "1.0",
                        critical = false
                    )

                override var state =
                    ModuleState.CREATED

                override fun init() {}

                override fun start() {}

                override fun stop() {}

                override fun capabilityProvider() =
                    object : RuntimeCapabilityProvider {

                        override fun capabilities():
                            List<RuntimeCapabilityDefinition> =
                            emptyList()
                    }
            }

        assertNotNull(
            discovery.discover(module)
        )
    }


    @Test
    fun `returns null for module without capabilities`() {

        val module =
            object : LiliyaModule {

                override val descriptor =
                    ModuleDescriptor(
                        name = "PlainModule",
                        version = "1.0",
                        critical = false
                    )

                override var state =
                    ModuleState.CREATED

                override fun init() {}

                override fun start() {}

                override fun stop() {}
            }


        assertNull(
            discovery.discover(module)
        )
    }
}
