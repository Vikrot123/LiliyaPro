package pro.liliya.core.module

import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory


class ModuleExceptionHandler {


    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "ModuleExceptionHandler",
        method = "lifecycle"
    )


    fun handle(
        module: LiliyaModule,
        action: String,
        error: Exception
    ) {

        module.state = ModuleState.FAILED


        logger.info(
            LogConfig.ERROR_CAUGHT,
            "Module failed: ${module.name}, action=$action, error=${error.message}"
        )
    }
}
