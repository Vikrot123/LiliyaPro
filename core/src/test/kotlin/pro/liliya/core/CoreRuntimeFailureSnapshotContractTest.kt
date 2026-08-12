package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CoreRuntimeFailureSnapshotContractTest {

    @Test
    fun failedRuntimeKeepsFailedSnapshot() {

        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        assertThrows(RuntimeException::class.java) {
            CoreRuntime.start()
        }

        val snapshot = CoreRuntime.snapshot()

        assertEquals(
            CoreRuntimeState.FAILED,
            snapshot.runtimeState
        )

        CoreRuntime.resetModuleProvider()
    }
}
