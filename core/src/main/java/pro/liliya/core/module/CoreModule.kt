package pro.liliya.core.module

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.runtime.module.CapabilityAwareModule
import pro.liliya.core.runtime.capability.RuntimeCapabilityProvider
import pro.liliya.core.runtime.capability.RuntimeCapabilityDefinition
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreModule : LiliyaModule, CapabilityAwareModule {

    override val descriptor = ModuleDescriptor(
        name = "CORE_MODULE",
        version = "0.3",
        critical = true
    )

    override var state: ModuleState =
        ModuleState.CREATED

    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "CoreModule",
        method = "lifecycle"
    )

    override fun capabilityProvider(): RuntimeCapabilityProvider {
        return object : RuntimeCapabilityProvider {
            override fun capabilities(): List<RuntimeCapabilityDefinition> {
                return listOf(
                    RuntimeCapabilityDefinition(
                        command = RuntimeCommand.START,
                        minimumAuthority = RuntimeAuthorityLevel.SYSTEM,
                        description = "Core runtime start capability"
                    )
                )
            }
        }
    }


    override fun init() {
        logger.info(
            LogConfig.MODULE_INIT,
            "Core module init"
        )

        state = ModuleState.INITIALIZED
    }

    override fun start() {
        logger.info(
            LogConfig.MODULE_READY,
            "Core module started"
        )

        state = ModuleState.RUNNING
    }

    override fun stop() {
        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core module stopped"
        )

        state = ModuleState.STOPPED
    }
}
