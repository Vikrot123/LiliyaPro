package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionMemoryLifecycleContractTest {

    @Test
    fun prepare_runtime_should_not_recreate_memory_composition() {

        val runtime =
            DefaultRuntimeComposition()

        val before =
            runtime.memoryComposition()

        runtime.prepareRuntimeStartup()

        val after =
            runtime.memoryComposition()

        assertSame(
            before,
            after
        )
    }

    @Test
    fun separate_runtime_instances_should_keep_isolated_memory() {

        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.memoryComposition(),
            second.memoryComposition()
        )
    }
}
