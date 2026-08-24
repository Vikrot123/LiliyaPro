package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryComposition

class RuntimeMemoryCompositionServiceContractTest {

    @Test
    fun composition_should_expose_memory_service() {

        val composition =
            DefaultRuntimeMemoryComposition()

        assertNotNull(
            composition.service()
        )
    }


    @Test
    fun composition_service_should_be_stable_instance() {

        val composition =
            DefaultRuntimeMemoryComposition()

        val first =
            composition.service()

        val second =
            composition.service()

        assertSame(
            first,
            second
        )
    }


    @Test
    fun separate_compositions_should_not_share_services() {

        val first =
            DefaultRuntimeMemoryComposition()

        val second =
            DefaultRuntimeMemoryComposition()

        assertNotSame(
            first.service(),
            second.service()
        )
    }
}
