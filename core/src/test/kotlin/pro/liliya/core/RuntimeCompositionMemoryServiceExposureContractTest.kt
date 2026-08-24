package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionMemoryServiceExposureContractTest {

    @Test
    fun runtime_composition_should_expose_memory_service() {

        val runtime =
            DefaultRuntimeComposition()

        assertNotNull(
            runtime.memoryService()
        )
    }


    @Test
    fun runtime_memory_service_should_match_memory_composition_service() {

        val runtime =
            DefaultRuntimeComposition()

        assertSame(
            runtime.memoryComposition()
                .service(),
            runtime.memoryService()
        )
    }


    @Test
    fun separate_runtime_compositions_should_not_share_memory_service() {

        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.memoryService(),
            second.memoryService()
        )
    }
}
