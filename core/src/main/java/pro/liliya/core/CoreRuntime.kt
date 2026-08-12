package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.module.CoreModuleProvider
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry

object CoreRuntime {

    private var moduleManager: ModuleManager? = null

    private var started = false

    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "CoreRuntime",
        method = "lifecycle"
    )

    fun start() {

        if (started) {
            return
        }

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

        started = true

        logger.info(
            LogConfig.MODULE_READY,
            "Core runtime ready"
        )
    }

    fun stop() {

        if (!started) {
            return
        }

        moduleManager?.stopModules()

        moduleManager = null
        started = false

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }
}
