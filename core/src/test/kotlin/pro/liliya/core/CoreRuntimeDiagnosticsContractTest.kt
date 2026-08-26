package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticsContractTest {

    @Test
    fun runtimeDiagnosticsReturnsCurrentRuntimeSnapshot() {
        val snapshot = CoreRuntime.diagnostics()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )
    }
}
