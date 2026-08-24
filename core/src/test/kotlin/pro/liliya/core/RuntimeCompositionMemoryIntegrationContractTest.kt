package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionMemoryIntegrationContractTest {

    @Test
    fun runtime_composition_should_expose_memory_composition() {

        val runtime =
            DefaultRuntimeComposition()

        assertNotNull(
            runtime.memoryComposition()
        )
    }

    @Test
    fun runtime_composition_should_expose_same_memory_instance_from_holder() {

        val runtime =
            DefaultRuntimeComposition()

        assertSame(
            runtime.memoryComposition(),
            runtime.memoryCompositionHolder()
                .composition()
        )
    }

    @Test
    fun separate_runtime_compositions_should_not_share_memory_state() {

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
