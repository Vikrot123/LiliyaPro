package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionMemoryStopRestartContractTest {

    @Test
    fun stop_should_not_recreate_memory_composition() {

        val runtime =
            DefaultRuntimeComposition()

        val before =
            runtime.memoryComposition()

        runtime.stopRuntimeLifecycle()

        val after =
            runtime.memoryComposition()

        assertSame(
            before,
            after
        )
    }

    @Test
    fun start_stop_cycle_should_keep_memory_identity() {

        val runtime =
            DefaultRuntimeComposition()

        val before =
            runtime.memoryComposition()

        runtime.startRuntime()
        runtime.stopRuntimeLifecycle()

        val after =
            runtime.memoryComposition()

        assertSame(
            before,
            after
        )
    }
}
