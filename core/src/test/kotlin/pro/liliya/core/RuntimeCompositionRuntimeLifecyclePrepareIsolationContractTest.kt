package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleEvent

class RuntimeCompositionRuntimeLifecyclePrepareIsolationContractTest {

    @Test
    fun runtime_lifecycle_history_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.recordRuntimeStarted()

        val before = composition.lifecycleRecorder()
            .records()

        assertEquals(1, before.size)
        assertNotNull(composition.lifecycleRecorder().last())

        composition.prepareRuntime()

        val afterReset = composition.lifecycleRecorder()
            .records()

        assertEquals(0, afterReset.size)

        composition.recordRuntimeStopped()

        val afterRestart = composition.lifecycleRecorder()
            .records()

        assertEquals(1, afterRestart.size)
        assertEquals(
            RuntimeLifecycleEvent.STOPPED,
            afterRestart.last().event
        )
    }
}
