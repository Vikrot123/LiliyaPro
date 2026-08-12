package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.module.ModuleState

class CoreDiagnosticSnapshotContractTest {

    @Test
    fun diagnosticSnapshotContainsRuntimeAndModuleStates() {

        val snapshot = CoreDiagnosticSnapshot(
            runtimeState = CoreRuntimeState.RUNNING,
            moduleStates = mapOf(
                "CORE" to ModuleState.RUNNING
            )
        )

        assertEquals(
            CoreRuntimeState.RUNNING,
            snapshot.runtimeState
        )

        assertEquals(
            ModuleState.RUNNING,
            snapshot.moduleStates["CORE"]
        )
    }
}
