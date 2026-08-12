package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreDiagnosticProviderContractTest {

    @Test
    fun diagnosticProviderReturnsRuntimeSnapshot() {

        val provider = CoreDiagnosticProvider()

        val snapshot = provider.snapshot()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )
    }
}
