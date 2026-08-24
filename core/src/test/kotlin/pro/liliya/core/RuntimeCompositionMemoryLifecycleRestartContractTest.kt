package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionMemoryLifecycleRestartContractTest {

    @Test
    fun memory_lifecycle_should_survive_runtime_restart_cycle() {

        val runtime =
            DefaultRuntimeComposition()

        val memory =
            runtime.memoryComposition()

        val lifecycle =
            memory.lifecycle()

        runtime.startRuntime()

        assertTrue(
            lifecycle.isStarted()
        )

        runtime.stopRuntimeLifecycle()

        assertFalse(
            lifecycle.isStarted()
        )

        runtime.startRuntime()

        assertTrue(
            lifecycle.isStarted()
        )

        assertSame(
            memory,
            runtime.memoryComposition()
        )
    }
}
