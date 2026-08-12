package pro.liliya.core.module

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory


class ModuleManager(
    private val registry: ModuleRegistry,
    private val provider: ModuleProvider
) {


    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "ModuleManager",
        method = "lifecycle"
    )


    fun loadModules() {

        provider
            .provideModules()
            .forEach { module ->

                registry.register(module)

                logger.info(
                    LogConfig.MODULE_INIT,
                    "Module loaded: ${module.name}, state=${module.state}"
                )
            }

        logger.info(
            LogConfig.MODULE_INIT,
            "Modules loaded"
        )
    }


    fun startModules() {

        registry.initAll()

        logger.info(
            LogConfig.MODULE_INIT,
            "Modules initialized"
        )


        registry.startAll()

        logger.info(
            LogConfig.MODULE_READY,
            "Modules started"
        )


        logModuleStates()
    }


    fun stopModules() {

        registry.stopAll()

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Modules stopped"
        )


        logModuleStates()
    }


    fun getModuleStates(): Map<String, ModuleState> {
        return registry.getStates()
    }


    private fun logModuleStates() {

        logger.info(
            LogConfig.MODULE_READY,
            "MODULE_STATUS"
        )


        registry
            .getStates()
            .forEach { (name, state) ->

                logger.info(
                    LogConfig.MODULE_READY,
                    "$name = $state"
                )
            }
    }
}
