package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionPrepareRuntimeIsolationContractTest {

    @Test
    fun prepare_runtime_clears_runtime_and_bootstrap_state() {
        val composition = DefaultRuntimeComposition()

        val first = composition.serviceBootstrap()

        composition.markRuntimeFailed("prepare failure")

        first.start()
        first.stop()

        composition.prepareRuntime()

        val second = composition.serviceBootstrap()

        assertNotSame(first, second)

        assertEquals(
            CoreRuntimeState.STOPPED,
            composition.runtimeState()
        )

        second.start()
        second.stop()
    }
}
