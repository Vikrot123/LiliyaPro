package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeRecoveryPrepareIsolationContractTest {

    @Test
    fun runtime_recovery_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.markRuntimeRecovered()

        val before = composition.recoveryTracker()
            .snapshot()

        assertTrue(before.recovered)

        composition.prepareRuntime()

        val afterReset = composition.recoveryTracker()
            .snapshot()

        assertFalse(afterReset.recovered)
        assertFalse(afterReset.recoveredAt != null)
    }
}
