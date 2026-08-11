package pro.liliya.core

import org.junit.jupiter.api.Test
import java.io.File

class CoreApplicationTest {

    @Test
    fun fullCoreLifecycle() {

        val logFile = File(
            "build/liliya-test.log"
        )

        if (logFile.exists()) {
            logFile.delete()
        }

        CoreApplication.start(logFile)

        CoreApplication.stop()

        println(
            "LOG FILE = ${logFile.absolutePath}"
        )

        require(logFile.exists()) {
            "Log file was not created"
        }

        val text = logFile.readText()

        require(
            text.contains("SYSTEM_START")
        ) {
            "Missing SYSTEM_START marker"
        }

        require(
            text.contains("MODULE_INIT")
        ) {
            "Missing MODULE_INIT marker"
        }

        require(
            text.contains("MODULE_READY")
        ) {
            "Missing MODULE_READY marker"
        }

        require(
            text.contains("SYSTEM_STOP")
        ) {
            "Missing SYSTEM_STOP marker"
        }
    }
}
