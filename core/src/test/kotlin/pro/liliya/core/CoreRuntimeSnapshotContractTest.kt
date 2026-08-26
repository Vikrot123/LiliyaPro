package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.ModuleState

class CoreRuntimeSnapshotContractTest {

    @Test
    fun runtimeSnapshotExposesCurrentRuntimeState() {

        CoreRuntime.stop()

        val snapshot = CoreRuntime.snapshot()

        assertEquals(
            CoreRuntimeState.STOPPED,
            snapshot.runtimeState
        )

        assertEquals(
            emptyMap<String, ModuleState>(),
            snapshot.moduleStates
        )
    }

    @Test
    fun runtimeSnapshotContainsRunningModulesAfterStart() {

        CoreRuntime.stop()
        CoreRuntime.start()

        try {
            val snapshot = CoreRuntime.snapshot()

            assertEquals(
                CoreRuntimeState.RUNNING,
                snapshot.runtimeState
            )
        } finally {
            CoreRuntime.stop()
        }
    }
}
