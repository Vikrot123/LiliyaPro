package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach

class CoreRuntimeStateContractTest {

    @AfterEach
    fun cleanupCoreRuntimeAfterTest() {
        CoreRuntime.resetModuleProvider()
        CoreRuntime.stop()
    }

    @Test
    fun runtimeMustExposeFailedStateAfterStartupFailure() {
        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        try {
            CoreRuntime.start()
        } catch (_: RuntimeException) {
            // Expected.
        }

        assertEquals(
            CoreRuntimeState.FAILED,
            CoreRuntime.state(),
            "Runtime must remain in FAILED state after startup failure"
        )

        CoreRuntime.resetModuleProvider()

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.state(),
            "Runtime must become RUNNING after successful restart"
        )

        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state(),
            "Runtime must become STOPPED after stop"
        )
    }
}
