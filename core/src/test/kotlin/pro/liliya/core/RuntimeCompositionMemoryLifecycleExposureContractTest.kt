package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionMemoryLifecycleExposureContractTest {

    @Test
    fun runtime_should_expose_memory_lifecycle() {

        val runtime =
            DefaultRuntimeComposition()

        assertNotNull(
            runtime.memoryLifecycle()
        )
    }

    @Test
    fun exposed_memory_lifecycle_should_match_memory_composition_lifecycle() {

        val runtime =
            DefaultRuntimeComposition()

        assertSame(
            runtime.memoryComposition()
                .lifecycle(),

            runtime.memoryLifecycle()
        )
    }

    @Test
    fun separate_runtime_instances_should_not_share_memory_lifecycle() {

        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.memoryLifecycle(),
            second.memoryLifecycle()
        )
    }
}
