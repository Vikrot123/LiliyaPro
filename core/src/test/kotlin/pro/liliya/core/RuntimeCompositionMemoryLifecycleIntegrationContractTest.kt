package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionMemoryLifecycleIntegrationContractTest {

    @Test
    fun runtime_start_should_start_memory_lifecycle() {

        val runtime =
            DefaultRuntimeComposition()

        assertFalse(
            runtime.memoryComposition()
                .lifecycle()
                .isStarted()
        )

        runtime.startRuntime()

        assertTrue(
            runtime.memoryComposition()
                .lifecycle()
                .isStarted()
        )
    }

    @Test
    fun runtime_stop_should_stop_memory_lifecycle() {

        val runtime =
            DefaultRuntimeComposition()

        runtime.startRuntime()

        runtime.stopRuntimeLifecycle()

        assertFalse(
            runtime.memoryComposition()
                .lifecycle()
                .isStarted()
        )
    }

    @Test
    fun prepare_runtime_should_reset_memory_lifecycle() {

        val runtime =
            DefaultRuntimeComposition()

        val lifecycle =
            runtime.memoryComposition()
                .lifecycle()

        lifecycle.start()

        assertTrue(
            lifecycle.isStarted()
        )

        runtime.prepareRuntimeStartup()

        assertFalse(
            lifecycle.isStarted()
        )
    }
}
