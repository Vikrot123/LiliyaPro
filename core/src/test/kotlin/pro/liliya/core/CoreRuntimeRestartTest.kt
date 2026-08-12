package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.logging.LogInitializer
import java.io.File

class CoreRuntimeRestartTest {

    @Test
    fun coreRuntimeCanRestartWithFreshModuleRegistry() {
        val logFile = File("build/liliya-runtime-restart.log")

        if (logFile.exists()) {
            logFile.delete()
        }

        LogInitializer.initialize(logFile)

        CoreRuntime.start()
        CoreRuntime.stop()

        CoreRuntime.start()
        CoreRuntime.stop()

        val text = logFile.readText()

        assertEquals(
            2,
            countOccurrences(text, "Core module init"),
            "CORE_MODULE must be initialized exactly once per runtime start"
        )

        assertEquals(
            2,
            countOccurrences(text, "Core module started"),
            "CORE_MODULE must be started exactly once per runtime start"
        )

        assertEquals(
            2,
            countOccurrences(text, "Core module stopped"),
            "CORE_MODULE must be stopped exactly once per runtime stop"
        )

        assertEquals(
            2,
            countOccurrences(text, "Module loaded: CORE_MODULE"),
            "A fresh registry must load exactly one CORE_MODULE per start"
        )
    }

    private fun countOccurrences(text: String, value: String): Int {
        return text.windowed(value.length).count { it == value }
    }
}
