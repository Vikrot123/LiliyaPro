package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.memory.factory.DefaultRuntimeMemoryCompositionFactory

class RuntimeMemoryCompositionFactoryContractTest {

    @Test
    fun factory_should_create_memory_composition() {

        val factory =
            DefaultRuntimeMemoryCompositionFactory()

        val composition =
            factory.create()

        assertNotNull(
            composition
        )
    }

    @Test
    fun factory_should_create_isolated_compositions() {

        val factory =
            DefaultRuntimeMemoryCompositionFactory()

        val first =
            factory.create()

        val second =
            factory.create()

        assertNotSame(
            first,
            second
        )
    }
}
