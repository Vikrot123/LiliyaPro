package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticsContractTest {

    @Test
    fun runtimeDiagnosticsReturnsCurrentRuntimeSnapshot() {
        val diagnostics = CoreRuntimeDiagnostics()

        val snapshot = diagnostics.snapshot()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )
    }
}
