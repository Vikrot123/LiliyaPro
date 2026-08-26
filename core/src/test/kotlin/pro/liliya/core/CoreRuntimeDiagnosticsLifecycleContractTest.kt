package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticsLifecycleContractTest {

    @Test
    fun diagnosticsFollowsRuntimeLifecycle() {

        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.diagnostics().runtimeState
        )

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.diagnostics().runtimeState
        )

        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.diagnostics().runtimeState
        )
    }
}
