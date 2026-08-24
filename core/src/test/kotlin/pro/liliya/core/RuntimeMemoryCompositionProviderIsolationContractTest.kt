package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryComposition

class RuntimeMemoryCompositionProviderIsolationContractTest {

    @Test
    fun separate_compositions_should_not_share_memory_providers() {

        val first =
            DefaultRuntimeMemoryComposition()

        val second =
            DefaultRuntimeMemoryComposition()

        val firstProvider =
            first.registry()
                .provider(
                    RuntimeMemoryType.SEMANTIC
                )

        val secondProvider =
            second.registry()
                .provider(
                    RuntimeMemoryType.SEMANTIC
                )

        assertNotNull(
            firstProvider
        )

        assertNotNull(
            secondProvider
        )

        assertNotSame(
            firstProvider,
            secondProvider
        )
    }
}
