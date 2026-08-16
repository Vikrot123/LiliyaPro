package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticServiceContractTest {

    @Test
    fun diagnosticServiceReturnsRuntimeSnapshot() {

        CoreRuntime.stop()

        val snapshot = CoreRuntime.diagnostics()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )
    }
}
