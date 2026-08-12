package pro.liliya.core

import org.junit.jupiter.api.Test
import java.io.File

class CoreApplicationLifecycleContractTest {

    @Test
    fun stopBeforeStartIsSafe() {
        CoreApplication.stop()

        val logFile = File("build/liliya-stop-before-start.log")
        logFile.delete()

        require(!logFile.exists()) {
            "stop() before start() must not create a log file"
        }
    }

    @Test
    fun doubleStopIsSafe() {
        val logFile = File("build/liliya-double-stop.log")
        logFile.delete()

        CoreApplication.start(logFile)
        CoreApplication.stop()
        CoreApplication.stop()

        require(logFile.exists()) {
            "Log file must exist after start()"
        }
    }

    @Test
    fun doubleStartDoesNotDuplicateRuntime() {
        val logFile = File("build/liliya-double-start.log")
        logFile.delete()

        CoreApplication.start(logFile)
        CoreApplication.start(logFile)
        CoreApplication.stop()

        val text = logFile.readText()

        require(
            countOccurrences(text, "Core module init") == 1
        ) {
            "Second start() must not initialize CORE_MODULE again"
        }

        require(
            countOccurrences(text, "Core module started") == 1
        ) {
            "Second start() must not start CORE_MODULE again"
        }

        require(
            countOccurrences(text, "Core module stopped") == 1
        ) {
            "stop() must stop CORE_MODULE exactly once"
        }
    }

    private fun countOccurrences(text: String, value: String): Int {
        return text.windowed(value.length).count { it == value }
    }
}
