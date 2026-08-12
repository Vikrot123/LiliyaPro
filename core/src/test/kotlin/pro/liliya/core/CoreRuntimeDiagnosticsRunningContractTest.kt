package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticsRunningContractTest {

    @Test
    fun runtimeDiagnosticsReturnsRunningRuntimeSnapshot() {
        CoreRuntime.stop()

        CoreRuntime.start()

        val diagnostics = CoreRuntimeDiagnostics()
        val snapshot = diagnostics.snapshot()

        assertEquals(
            CoreRuntimeState.RUNNING,
            snapshot.runtimeState
        )

        CoreRuntime.stop()
    }
}
