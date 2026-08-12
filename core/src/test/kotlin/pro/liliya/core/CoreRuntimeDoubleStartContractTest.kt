package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import pro.liliya.core.logging.LogInitializer

class CoreRuntimeDoubleStartContractTest {

    @Test
    fun secondStartMustNotReplaceActiveRuntime() {
        val logFile = File("build/liliya-runtime-double-start-contract.log")

        if (logFile.exists()) {
            logFile.delete()
        }

        LogInitializer.initialize(logFile)

        CoreRuntime.start()
        CoreRuntime.start()
        CoreRuntime.stop()

        val text = logFile.readText()

        assertEquals(
            1,
            countOccurrences(text, "Core module init"),
            "Second start must not initialize duplicate runtime"
        )

        assertEquals(
            1,
            countOccurrences(text, "Core module started"),
            "Second start must not start duplicate runtime"
        )

        assertEquals(
            1,
            countOccurrences(text, "Core module stopped"),
            "Single stop must stop active runtime exactly once"
        )
    }

    private fun countOccurrences(text: String, value: String): Int {
        return text.windowed(value.length).count { it == value }
    }
}
