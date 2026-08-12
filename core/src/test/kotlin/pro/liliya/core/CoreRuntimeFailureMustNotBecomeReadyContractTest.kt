package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.logging.LogConfig
import pro.liliya.core.logging.LogInitializer
import java.io.File

class CoreRuntimeFailureMustNotBecomeReadyContractTest {

    @Test
    fun failedStartupMustNotReportRuntimeReady() {

        val logFile = File(
            "build/liliya-runtime-failure-ready-contract.log"
        )

        if (logFile.exists()) {
            logFile.delete()
        }

        LogInitializer.initialize(logFile)

        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        try {
            CoreRuntime.start()
        } catch (_: RuntimeException) {
        }

        val failedText = logFile.readText()

        assertTrue(
            failedText.contains("Core runtime starting"),
            "Startup event must be logged"
        )

        assertTrue(
            failedText.contains("Core runtime startup failed"),
            "Failure event must be logged"
        )

        assertFalse(
            failedText.contains("Core runtime ready"),
            "Failed startup must never report READY"
        )

        CoreRuntime.resetModuleProvider()

        CoreRuntime.start()
        CoreRuntime.stop()
    }
}
