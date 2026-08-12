package pro.liliya.core.module

import pro.liliya.core.ModuleEvent
import pro.liliya.core.ModuleEventBus

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory

class ModuleRegistry {

    private val modules = mutableListOf<LiliyaModule>()

    private val exceptionHandler =
        ModuleExceptionHandler()

    private val dependencyResolver =
        ModuleDependencyResolver()

    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "ModuleRegistry",
        method = "lifecycle"
    )

    fun register(
        module: LiliyaModule
    ) {
        modules.add(module)

        ModuleEventBus.publish(
            ModuleEvent.Loaded(module.name)
        )

        logger.info(
            LogConfig.MODULE_INIT,
            "Registered module: ${module.name}"
        )
    }

    fun initAll() {

        val orderedModules =
            dependencyResolver.resolve(modules)

        logger.info(
            LogConfig.MODULE_INIT,
            "Module init order resolved: ${orderedModules.map { it.name }}"
        )

        orderedModules.forEach { module ->

            try {
                module.init()

                ModuleEventBus.publish(
                    ModuleEvent.Initialized(module.name)
                )
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

        val orderedModules =
            dependencyResolver.resolve(modules)

        logger.info(
            LogConfig.MODULE_INIT,
            "Module start order resolved: ${orderedModules.map { it.name }}"
        )

        orderedModules.forEach { module ->

            if (module.state == ModuleState.FAILED) {

                logger.info(
                    LogConfig.SYSTEM_STOP,
                    "Skipping failed module: ${module.name}"
                )

                return@forEach
            }

            val failedDependency =
                module.descriptor.dependencies
                    .mapNotNull { dependencyName ->
                        modules.find { it.name == dependencyName }
                    }
                    .firstOrNull { dependency ->
                        dependency.state == ModuleState.FAILED
                    }

            if (failedDependency != null) {

                logger.error(
                    LogConfig.MODULE_DEPENDENCY_FAILED,
                    "Cannot start module: ${module.name}, dependency failed: ${failedDependency.name}"
                )

                exceptionHandler.handle(
                    module,
                    "dependency",
                    IllegalStateException(
                        "Dependency failed: ${failedDependency.name}"
                    )
                )

                return@forEach
            }

              try {
                  module.start()

                ModuleEventBus.publish(
                    ModuleEvent.Started(module.name)
                )
              } catch (e: Exception) {
                  exceptionHandler.handle(
                      module,
                      "start",
                      e
                  )

                  if (module.descriptor.critical) {
                      logger.error(
                          LogConfig.MODULE_DEPENDENCY_FAILED,
                          "Critical module failed: ${module.name}. Aborting startup."
                      )

                      throw IllegalStateException(
                          "Critical module failed: ${module.name}",
                          e
                      )
                  }
              }
          }
      }

    fun stopAll() {

        val orderedModules =
            dependencyResolver.resolve(modules)
                .asReversed()

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Module stop order resolved: ${orderedModules.map { it.name }}"
        )

        orderedModules.forEach { module ->
            val wasFailed = module.state == ModuleState.FAILED

            try {
                module.stop()

                if (!wasFailed) {
                    ModuleEventBus.publish(
                        ModuleEvent.Stopped(module.name)
                    )
                }

                if (wasFailed) {
                    module.state = ModuleState.FAILED
                }
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
