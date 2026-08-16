package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CoreRuntimeFailureRecoveryIsolationContractTest {

    @Test
    fun failedStartupStateMustNotLeakIntoRecoveredRuntime() {

        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        assertThrows(RuntimeException::class.java) {
            CoreRuntime.start()
        }

        assertEquals(
            CoreRuntimeState.FAILED,
            CoreRuntime.state()
        )

        val failedSnapshot = CoreRuntime.snapshot()

        assertEquals(
            CoreRuntimeState.FAILED,
            failedSnapshot.runtimeState
        )

        CoreRuntime.resetModuleProvider()

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.state()
        )

        val recoveredSnapshot = CoreRuntime.snapshot()

        assertFalse(
            recoveredSnapshot.moduleStates.containsKey(
                "CRITICAL_STARTUP_FAILING_MODULE"
            ),
            "Failed module state must not leak into recovered runtime"
        )

        CoreRuntime.stop()
    }
}
