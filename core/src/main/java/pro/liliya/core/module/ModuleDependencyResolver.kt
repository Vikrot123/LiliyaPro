package pro.liliya.core.module

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory

class ModuleDependencyResolver {

    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "ModuleDependencyResolver",
        method = "resolve"
    )

    fun resolve(
        modules: List<LiliyaModule>
    ): List<LiliyaModule> {

        logger.info(
            LogConfig.MODULE_DEPENDENCY_CHECK,
            "Resolving module dependency order"
        )

        val result = mutableListOf<LiliyaModule>()
        val visiting = mutableSetOf<String>()
        val visited = mutableSetOf<String>()

        val moduleMap = modules.associateBy {
            it.name
        }

        fun visit(module: LiliyaModule) {

            if (module.name in visited) {
                return
            }

            if (module.name in visiting) {
                logger.error(
                    LogConfig.MODULE_DEPENDENCY_CYCLE,
                    "Dependency cycle detected: ${module.name}"
                )

                throw IllegalStateException(
                    "Dependency cycle: ${module.name}"
                )
            }

            visiting.add(module.name)

            module.descriptor.dependencies.forEach { dependency ->

                logger.info(
                    LogConfig.MODULE_DEPENDENCY_CHECK,
                    "Checking dependency: ${module.name} -> $dependency"
                )

                val dependencyModule = moduleMap[dependency]

                if (dependencyModule == null) {

                    logger.error(
                        LogConfig.MODULE_DEPENDENCY_FAILED,
                        "Missing dependency: ${module.name} -> $dependency"
                    )

                    throw IllegalStateException(
                        "Missing dependency: $dependency"
                    )
                }

                visit(dependencyModule)
            }

            visiting.remove(module.name)
            visited.add(module.name)
            result.add(module)

            logger.info(
                LogConfig.MODULE_DEPENDENCY_RESOLVED,
                "Resolved module: ${module.name}"
            )
        }

        modules.forEach {
            visit(it)
        }

        logger.info(
            LogConfig.MODULE_START_ORDER,
            "Module start order resolved: ${result.map { it.name }}"
        )

        return result
    }
}
