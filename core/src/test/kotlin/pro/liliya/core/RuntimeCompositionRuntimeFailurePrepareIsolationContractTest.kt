package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeFailurePrepareIsolationContractTest {

    @Test
    fun runtime_failure_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.recordRuntimeFailure(
            "initial failure",
            "test-module"
        )

        val before = composition.failureTracker()
            .snapshot()

        assertTrue(before.failed)
        assertEquals(
            "initial failure",
            before.failureReason
        )

        composition.prepareRuntime()

        val afterReset = composition.failureTracker()
            .snapshot()

        assertFalse(afterReset.failed)
        assertEquals(null, afterReset.failureReason)
        assertEquals(null, afterReset.failedModule)
    }
}
