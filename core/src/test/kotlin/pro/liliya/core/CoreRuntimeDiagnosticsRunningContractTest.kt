package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticsRunningContractTest {

    @Test
    fun runtimeDiagnosticsReturnsRunningRuntimeSnapshot() {
        CoreRuntime.stop()

        CoreRuntime.start()

        val snapshot = CoreRuntime.diagnostics()

        assertEquals(
            CoreRuntimeState.RUNNING,
            snapshot.runtimeState
        )

        CoreRuntime.stop()
    }
}
