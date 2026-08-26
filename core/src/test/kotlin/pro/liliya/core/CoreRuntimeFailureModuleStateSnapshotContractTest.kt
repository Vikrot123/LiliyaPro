package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.module.ModuleState
import org.junit.jupiter.api.AfterEach

class CoreRuntimeFailureModuleStateSnapshotContractTest {

    @AfterEach
    fun cleanupCoreRuntimeAfterTest() {
        CoreRuntime.resetModuleProvider()
        CoreRuntime.stop()
    }

    @Test
    fun failedRuntimeSnapshotContainsFailedModuleState() {

        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        assertThrows(RuntimeException::class.java) {
            CoreRuntime.start()
        }

        val snapshot = CoreRuntime.snapshot()


        assertEquals(
            ModuleState.FAILED,
            snapshot.moduleStates[
                "CRITICAL_STARTUP_FAILING_MODULE"
            ]
        )

        CoreRuntime.resetModuleProvider()
    }
}
