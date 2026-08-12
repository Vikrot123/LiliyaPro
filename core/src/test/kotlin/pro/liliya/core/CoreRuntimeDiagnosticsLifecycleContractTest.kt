package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticsLifecycleContractTest {

    @Test
    fun diagnosticsFollowsRuntimeLifecycle() {

        val diagnostics = CoreRuntimeDiagnostics()

        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            diagnostics.snapshot().runtimeState
        )

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            diagnostics.snapshot().runtimeState
        )

        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            diagnostics.snapshot().runtimeState
        )
    }
}
