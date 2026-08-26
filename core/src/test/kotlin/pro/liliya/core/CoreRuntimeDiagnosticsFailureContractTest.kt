package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticsFailureContractTest {

    @Test
    fun runtimeDiagnosticsReturnsFailureSnapshot() {
        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        assertThrows(RuntimeException::class.java) {
            CoreRuntime.start()
        }

        val snapshot = CoreRuntime.diagnostics()

        assertEquals(
            CoreRuntimeState.FAILED,
            snapshot.runtimeState
        )

        assertEquals(
            "Critical module failed: CRITICAL_STARTUP_FAILING_MODULE",
            snapshot.failureReason
        )

        CoreRuntime.resetModuleProvider()
    }
}
