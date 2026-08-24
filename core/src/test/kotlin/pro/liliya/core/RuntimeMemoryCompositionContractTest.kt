package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryComposition

class RuntimeMemoryCompositionContractTest {

    @Test
    fun composition_should_create_memory_components() {

        val composition =
            DefaultRuntimeMemoryComposition()

        assertNotNull(
            composition.registry()
        )

        assertNotNull(
            composition.access()
        )
    }

    @Test
    fun separate_compositions_should_not_share_memory_state() {

        val first =
            DefaultRuntimeMemoryComposition()

        val second =
            DefaultRuntimeMemoryComposition()

        assertNotSame(
            first.registry(),
            second.registry()
        )
    }
    @Test
    fun composition_should_keep_same_owned_instances() {

        val composition =
            DefaultRuntimeMemoryComposition()

        assertNotSame(
            composition.registry(),
            DefaultRuntimeMemoryComposition().registry()
        )

        val firstRegistry =
            composition.registry()

        val secondRegistry =
            composition.registry()

        assertEquals(
            firstRegistry,
            secondRegistry
        )

        val firstAccess =
            composition.access()

        val secondAccess =
            composition.access()

        assertEquals(
            firstAccess,
            secondAccess
        )
    }


}
