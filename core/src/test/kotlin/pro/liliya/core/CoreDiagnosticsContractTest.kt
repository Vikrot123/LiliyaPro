package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreDiagnosticsContractTest {

    @Test
    fun diagnosticsReturnsSnapshotFromSource() {
        val diagnostics = CoreDiagnostics(
            CoreDiagnosticProvider()
        )

        val snapshot = diagnostics.snapshot()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )
    }
}
