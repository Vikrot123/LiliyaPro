package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
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

        println("===== FAILURE TEST LOG =====")
        println(failedText)
        println("===== END FAILURE TEST LOG =====")

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

        assertEquals(
            CoreRuntimeState.FAILED,
            CoreRuntime.getRuntimeState(),
            "Failed startup must leave runtime in FAILED state"
        )

        CoreRuntime.resetModuleProvider()

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.getRuntimeState(),
            "Runtime must recover after failed startup"
        )

        assertTrue(
            logFile.readText().contains("Core runtime ready"),
            "Successful restart must report READY"
        )

        CoreRuntime.stop()
    }
}
