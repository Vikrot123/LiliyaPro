package pro.liliya.core.logging

import java.io.File

object LogInitializer {

    fun initialize(
        file: File
    ) {

        LoggerProvider.initialize(
            FileLogWriter(file)
        )
    }
}
