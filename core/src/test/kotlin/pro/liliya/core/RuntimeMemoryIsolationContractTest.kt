package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability

class RuntimeMemoryIsolationContractTest {

    @Test
    fun separate_memory_registries_should_not_share_providers() {

        val first =
            DefaultRuntimeMemoryRegistry()

        val second =
            DefaultRuntimeMemoryRegistry()


        first.register(
            RuntimeMemoryType.WORKING,
            FakeMemoryProvider(),
            setOf(
                RuntimeMemoryCapability.READ,
                RuntimeMemoryCapability.WRITE
            )
        )


        assertNotNull(
            first.provider(
                RuntimeMemoryType.WORKING
            )
        )


        assertNull(
            second.provider(
                RuntimeMemoryType.WORKING
            )
        )


        assertEquals(
            2,
            first.capabilities(
                RuntimeMemoryType.WORKING
            ).size
        )


        assertEquals(
            0,
            second.capabilities(
                RuntimeMemoryType.WORKING
            ).size
        )
    }


    private class FakeMemoryProvider :
        RuntimeMemoryProvider {

        override fun store(
            entry: RuntimeMemoryEntry
        ) {
        }

        override fun search(
            query: String
        ): List<RuntimeMemoryEntry> {
            return emptyList()
        }

        override fun clear() {
        }
    }
}
