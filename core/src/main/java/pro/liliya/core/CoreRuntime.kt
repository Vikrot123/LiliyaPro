package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.module.CoreModuleProvider
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry


object CoreRuntime {

    private var moduleManager: ModuleManager? = null


    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "CoreRuntime",
        method = "lifecycle"
    )


    fun start() {
        logger.info(
            LogConfig.SYSTEM_START,
            "Core runtime starting"
        )

        val manager = ModuleManager(
            registry = ModuleRegistry(),
            provider = CoreModuleProvider()
        )

        moduleManager = manager

        manager.loadModules()
        manager.startModules()

        logger.info(
            LogConfig.MODULE_READY,
            "Core runtime ready"
        )
    }

    fun stop() {
        moduleManager?.stopModules()
        moduleManager = null

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }
}
