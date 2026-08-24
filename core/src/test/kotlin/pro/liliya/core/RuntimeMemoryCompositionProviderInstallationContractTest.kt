package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryComposition

class RuntimeMemoryCompositionProviderInstallationContractTest {

    @Test
    fun composition_should_install_default_memory_providers() {

        val composition =
            DefaultRuntimeMemoryComposition()

        assertNotNull(
            composition.registry()
                .provider(
                    RuntimeMemoryType.SEMANTIC
                )
        )
    }
}
