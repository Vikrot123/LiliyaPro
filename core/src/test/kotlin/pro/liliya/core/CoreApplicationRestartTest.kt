package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class CoreApplicationRestartTest {

    @Test
    fun coreApplicationCanRestartWithoutDuplicatingModules() {
        val firstLog = File("build/liliya-restart-first.log")
        val secondLog = File("build/liliya-restart-second.log")

        firstLog.delete()
        secondLog.delete()

        CoreApplication.start(firstLog)
        CoreApplication.stop()

        CoreApplication.start(secondLog)
        CoreApplication.stop()

        assertEquals(
            1,
            countOccurrences(secondLog.readText(), "Core module init"),
            "Second start must initialize exactly one CORE_MODULE"
        )

        assertEquals(
            1,
            countOccurrences(secondLog.readText(), "Core module started"),
            "Second start must start exactly one CORE_MODULE"
        )

        assertEquals(
            1,
            countOccurrences(secondLog.readText(), "Core module stopped"),
            "Second stop must stop exactly one CORE_MODULE"
        )
    }

    private fun countOccurrences(text: String, value: String): Int {
        return text.windowed(value.length).count { it == value }
    }
}
