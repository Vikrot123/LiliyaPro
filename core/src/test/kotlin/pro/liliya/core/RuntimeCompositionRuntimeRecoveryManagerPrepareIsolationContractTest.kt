package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeRecoveryManagerPrepareIsolationContractTest {

    @Test
    fun runtime_recovery_manager_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.prepareRuntime()

        val snapshot = composition
            .healthReportProvider()
            .createReport(
                state = CoreRuntimeState.STOPPED,
                telemetry = composition.telemetryObserver().snapshot(),
                failure = composition.failureTracker().snapshot(),
                recovery = composition.recoveryTracker().snapshot()
            )

        assertFalse(snapshot.recovery.recovered)
        assertNull(snapshot.recovery.recoveredAt)
    }
}
