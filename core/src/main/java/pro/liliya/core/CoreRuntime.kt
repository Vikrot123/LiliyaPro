package pro.liliya.core

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.Logger
import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.module.CoreModuleProvider
import pro.liliya.core.module.ModuleManager
import pro.liliya.core.module.ModuleRegistry

object CoreRuntime {

    private var moduleManager: ModuleManager? = null

    private var moduleProvider: pro.liliya.core.module.ModuleProvider =
        CoreModuleProvider()

    private var runtimeState = CoreRuntimeState.STOPPED

    private val logger: Logger
        get() = LoggerFactory.create(
            module = "CORE",
            component = "CoreRuntime",
            method = "lifecycle"
        )

    fun state(): CoreRuntimeState {
        return runtimeState
    }

    fun start() {
        if (runtimeState == CoreRuntimeState.RUNNING ||
            runtimeState == CoreRuntimeState.STARTING
        ) {
            return
        }

        runtimeState = CoreRuntimeState.STARTING

        logger.info(
            LogConfig.SYSTEM_START,
            "Core runtime starting"
        )

        val manager = ModuleManager(
            registry = ModuleRegistry(),
            provider = moduleProvider
        )

        try {
            moduleManager = manager

            manager.loadModules()
            manager.startModules()

            runtimeState = CoreRuntimeState.RUNNING

            logger.info(
                LogConfig.MODULE_READY,
                "Core runtime ready"
            )
        } catch (error: Exception) {
            logger.info(
                LogConfig.ERROR_CAUGHT,
                "Core runtime startup failed: ${error.message}"
            )

            try {
                manager.stopModules()
            } catch (_: Exception) {
            }

            moduleManager = null
            runtimeState = CoreRuntimeState.FAILED

            throw error
        }
    }

    internal fun setModuleProvider(
        provider: pro.liliya.core.module.ModuleProvider
    ) {
        moduleProvider = provider
    }

    internal fun resetModuleProvider() {
        moduleProvider = CoreModuleProvider()
    }

    fun stop() {
        if (runtimeState != CoreRuntimeState.RUNNING) {
            return
        }

        moduleManager?.stopModules()

        moduleManager = null
        runtimeState = CoreRuntimeState.STOPPED

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Core runtime stopped"
        )
    }
}
