package pro.liliya.core

import pro.liliya.core.logging.LoggerFactory
import pro.liliya.core.logging.LogConfig

object CoreBootstrap {

    private val logger = LoggerFactory.create(
        module = "CORE",
        component = "CoreBootstrap",
        method = "start"
    )

    fun start() {

        logger.info(
            LogConfig.SYSTEM_START,
            "Core system starting"
        )

        logger.info(
            LogConfig.MODULE_READY,
            "Core logging ready"
        )
    }
}
