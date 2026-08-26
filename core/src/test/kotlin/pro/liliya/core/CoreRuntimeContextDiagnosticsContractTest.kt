package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeContextDiagnosticsContractTest {

    @Test
    fun contextDiagnosticSourceReturnsRuntimeSnapshot() {
        val context = CoreRuntimeContext()

        CoreRuntime.stop()

        val snapshot = context.diagnosticSource.snapshot()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )
    }
}
