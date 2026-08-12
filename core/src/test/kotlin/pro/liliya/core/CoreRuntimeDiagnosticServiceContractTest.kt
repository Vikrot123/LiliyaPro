package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticServiceContractTest {

    @Test
    fun diagnosticServiceReturnsRuntimeSnapshot() {

        val service = CoreRuntimeDiagnosticService()

        CoreRuntime.stop()

        val snapshot = service.snapshot()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )
    }
}
