package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.ModuleState

class CoreRuntimeSnapshotContractTest {

    @Test
    fun runtimeSnapshotExposesCurrentRuntimeState() {

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

        CoreRuntime.start()

        val snapshot = CoreRuntime.snapshot()

        assertEquals(
            CoreRuntimeState.RUNNING,
            snapshot.runtimeState
        )

        CoreRuntime.stop()
    }
}
