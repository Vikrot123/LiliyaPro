package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeContextDiagnosticsContractTest {

    @Test
    fun contextDiagnosticServiceReturnsRuntimeSnapshot() {
        val context = CoreRuntimeContext()

        CoreRuntime.stop()

        val snapshot = context.diagnosticService.snapshot()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )
    }
}
