package pro.liliya.core.module

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory

class CoreModule : LiliyaModule {

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
