package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability

class RuntimeCompositionMemoryPrepareIsolationContractTest {

    @Test
    fun prepare_should_keep_installed_memory_provider() {

        val runtime =
            DefaultRuntimeComposition()

        val before =
            runtime.memoryComposition()
                .registry()
                .provider(
                    RuntimeMemoryType.SEMANTIC
                )

        runtime.prepareRuntimeStartup()

        val after =
            runtime.memoryComposition()
                .registry()
                .provider(
                    RuntimeMemoryType.SEMANTIC
                )

        assertNotNull(before)
        assertNotNull(after)
    }

    @Test
    fun prepare_should_keep_memory_capabilities() {

        val runtime =
            DefaultRuntimeComposition()

        runtime.prepareRuntimeStartup()

        assertEquals(
            setOf(
                RuntimeMemoryCapability.READ,
                RuntimeMemoryCapability.WRITE
            ),
            runtime.memoryComposition()
                .registry()
                .capabilities(
                    RuntimeMemoryType.SEMANTIC
                )
        )
    }
}
