package pro.liliya.core

import java.io.File
import pro.liliya.core.logging.LogInitializer
import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LoggerFactory

object CoreApplication {

    private var started = false

    fun start(logFile: File) {

        if (started) {
            return
        }

        LogInitializer.initialize(logFile)

        val logger = LoggerFactory.create(
            module = "CORE",
            component = "CoreApplication",
            method = "start"
        )

        logger.info(
            LogConfig.SYSTEM_START,
            "Liliya core application starting"
        )

        CoreRuntime.start()

        logger.info(
            LogConfig.MODULE_READY,
            "Core application started"
        )

        started = true
    }


    fun stop() {

        val logger = LoggerFactory.create(
            module = "CORE",
            component = "CoreApplication",
            method = "stop"
        )

        logger.info(
            LogConfig.SYSTEM_STOP,
            "Liliya core application stopping"
        )

        CoreRuntime.stop()

        started = false
    }
}
