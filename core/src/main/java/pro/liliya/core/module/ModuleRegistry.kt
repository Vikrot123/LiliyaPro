package pro.liliya.core.module

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory


class ModuleRegistry {


    private val modules = mutableListOf<LiliyaModule>()

    private val exceptionHandler = ModuleExceptionHandler()


    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "ModuleRegistry",
        method = "lifecycle"
    )


    fun register(
        module: LiliyaModule
    ) {

        modules.add(module)

        logger.info(
            LogConfig.MODULE_INIT,
            "Registered module: ${module.name}"
        )
    }


    fun initAll() {

        modules.forEach { module ->

            try {

                module.init()

            } catch (e: Exception) {

                exceptionHandler.handle(
                    module,
                    "init",
                    e
                )
            }
        }
    }


    fun startAll() {

        modules.forEach { module ->

            if (module.state == ModuleState.FAILED) {

                logger.info(
                    LogConfig.SYSTEM_STOP,
                    "Skipping failed module: ${module.name}"
                )

                return@forEach
            }

            try {

                module.start()

            } catch (e: Exception) {

                exceptionHandler.handle(
                    module,
                    "start",
                    e
                )
            }
        }
    }

    fun stopAll() {

        modules.forEach { module ->

            try {

                module.stop()

            } catch (e: Exception) {

                exceptionHandler.handle(
                    module,
                    "stop",
                    e
                )
            }
        }
    }


    fun getStates(): Map<String, ModuleState> {

        return modules.associate { module ->

            module.name to module.state
        }
    }
}
