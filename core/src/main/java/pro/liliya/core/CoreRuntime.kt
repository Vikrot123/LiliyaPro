package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.module.CoreModuleProvider
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry


object CoreRuntime {

    private val moduleManager = ModuleManager(
        registry = ModuleRegistry(),
        provider = CoreModuleProvider()
    )


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


        moduleManager.loadModules()


        moduleManager.startModules()


        logger.info(
            LogConfig.MODULE_READY,
            "Core runtime ready"
        )
    }


    fun stop() {

        moduleManager.stopModules()


        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }
}
