package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryCompositionHolder
import pro.liliya.core.runtime.intelligence.memory.factory.DefaultRuntimeMemoryCompositionFactory

class RuntimeMemoryCompositionHolderContractTest {

    @Test
    fun holder_should_expose_memory_composition() {

        val holder =
            DefaultRuntimeMemoryCompositionHolder(
                DefaultRuntimeMemoryCompositionFactory()
            )

        assertNotNull(
            holder.composition()
        )
    }

    @Test
    fun holder_should_keep_same_owned_composition_instance() {

        val holder =
            DefaultRuntimeMemoryCompositionHolder(
                DefaultRuntimeMemoryCompositionFactory()
            )

        val first =
            holder.composition()

        val second =
            holder.composition()

        assertSame(
            first,
            second
        )
    }
}
