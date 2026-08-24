package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.memory.access.DefaultRuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability
import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry

class RuntimeMemoryAccessContractTest {

    @Test
    fun access_should_delegate_store_to_registered_provider() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val provider =
            FakeMemoryProvider()

        registry.register(
            RuntimeMemoryType.WORKING,
            provider,
            setOf(
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
                content = "working memory",
                type = RuntimeMemoryType.WORKING,
                confidence = 1.0,
                createdAt = 1L
            )
        )

        assertEquals(
            1,
            provider.storeCount
        )
    }


    @Test
    fun access_should_delegate_search_to_registered_provider() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val provider =
            SearchMemoryProvider()

        registry.register(
            RuntimeMemoryType.SEMANTIC,
            provider,
            setOf(
                RuntimeMemoryCapability.READ
            )
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
            1,
            result.size
        )

        assertEquals(
            "memory result",
            result[0].content
        )
    }


    private class FakeMemoryProvider :
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
            return emptyList()
        }

        override fun clear() {
        }
    }

    private class SearchMemoryProvider :
        RuntimeMemoryProvider {

        override fun store(
            entry: RuntimeMemoryEntry
        ) {
        }

        override fun search(
            query: String
        ): List<RuntimeMemoryEntry> {

            return listOf(
                RuntimeMemoryEntry(
                    id = "1",
                    content = "memory result",
                    type = RuntimeMemoryType.SEMANTIC,
                    confidence = 0.8,
                    createdAt = 1L
                )
            )
        }

        override fun clear() {
        }
    }

}
