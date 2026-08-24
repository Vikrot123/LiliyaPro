package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.memory.access.DefaultRuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability
import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry

class RuntimeMemoryCapabilityGuardContractTest {

    @Test
    fun provider_without_write_capability_should_not_receive_store() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val provider =
            CountingMemoryProvider()

        registry.register(
            RuntimeMemoryType.WORKING,
            provider,
            emptySet()
        )

        val access =
            DefaultRuntimeMemoryAccess(
                registry
            )

        access.store(
            RuntimeMemoryType.WORKING,
            RuntimeMemoryEntry(
                id = "1",
                content = "blocked",
                type = RuntimeMemoryType.WORKING,
                confidence = 1.0,
                createdAt = 1L
            )
        )

        assertEquals(
            0,
            provider.storeCount
        )
    }


    @Test
    fun provider_without_read_capability_should_not_return_data() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val provider =
            CountingMemoryProvider()

        registry.register(
            RuntimeMemoryType.SEMANTIC,
            provider,
            emptySet()
        )

        val access =
            DefaultRuntimeMemoryAccess(
                registry
            )

        val result =
            access.search(
                RuntimeMemoryType.SEMANTIC,
                "test"
            )

        assertEquals(
            0,
            result.size
        )
    }


    @Test
    fun provider_with_capabilities_should_allow_access() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val provider =
            CountingMemoryProvider()

        registry.register(
            RuntimeMemoryType.WORKING,
            provider,
            setOf(
                RuntimeMemoryCapability.READ,
                RuntimeMemoryCapability.WRITE
            )
        )

        val access =
            DefaultRuntimeMemoryAccess(
                registry
            )

        access.store(
            RuntimeMemoryType.WORKING,
            RuntimeMemoryEntry(
                id = "1",
                content = "allowed",
                type = RuntimeMemoryType.WORKING,
                confidence = 1.0,
                createdAt = 1L
            )
        )

        assertEquals(
            1,
            provider.storeCount
        )

        val result =
            access.search(
                RuntimeMemoryType.WORKING,
                "test"
            )

        assertEquals(
            1,
            result.size
        )
    }


    private class CountingMemoryProvider :
        RuntimeMemoryProvider {

        var storeCount = 0

        override fun store(
            entry: RuntimeMemoryEntry
        ) {
            storeCount++
        }

        override fun search(
            query: String
        ): List<RuntimeMemoryEntry> {

            return listOf(
                RuntimeMemoryEntry(
                    id = "1",
                    content = "memory",
                    type = RuntimeMemoryType.SEMANTIC,
                    confidence = 1.0,
                    createdAt = 1L
                )
            )
        }

        override fun clear() {
        }
    }
}
