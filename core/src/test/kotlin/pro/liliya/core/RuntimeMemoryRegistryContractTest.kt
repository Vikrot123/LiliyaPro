package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability
import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry

class RuntimeMemoryRegistryContractTest {

    @Test
    fun registration_should_store_memory_provider() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val provider =
            FakeMemoryProvider()

        registry.register(
            RuntimeMemoryType.SEMANTIC,
            provider,
            setOf(
                RuntimeMemoryCapability.READ,
                RuntimeMemoryCapability.WRITE
            )
        )

        assertNotNull(
            registry.provider(
                RuntimeMemoryType.SEMANTIC
            )
        )

        assertEquals(
            2,
            registry.capabilities(
                RuntimeMemoryType.SEMANTIC
            ).size
        )
    }

    @Test
    fun duplicate_registration_should_not_replace_existing_provider() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val first =
            FakeMemoryProvider()

        val second =
            FakeMemoryProvider()

        registry.register(
            RuntimeMemoryType.SEMANTIC,
            first,
            setOf(
                RuntimeMemoryCapability.READ
            )
        )

        registry.register(
            RuntimeMemoryType.SEMANTIC,
            second,
            setOf(
                RuntimeMemoryCapability.WRITE
            )
        )

        assertEquals(
            first,
            registry.provider(
                RuntimeMemoryType.SEMANTIC
            )
        )

        assertEquals(
            setOf(
                RuntimeMemoryCapability.READ
            ),
            registry.capabilities(
                RuntimeMemoryType.SEMANTIC
            )
        )
    }


    @Test
    fun unregister_should_remove_memory_provider() {

        val registry =
            DefaultRuntimeMemoryRegistry()

        val provider =
            FakeMemoryProvider()

        registry.register(
            RuntimeMemoryType.SEMANTIC,
            provider,
            setOf(
                RuntimeMemoryCapability.READ
            )
        )

        registry.unregister(
            RuntimeMemoryType.SEMANTIC
        )

        assertNull(
            registry.provider(
                RuntimeMemoryType.SEMANTIC
            )
        )

        assertEquals(
            emptySet(),
            registry.capabilities(
                RuntimeMemoryType.SEMANTIC
            )
        )
    }


    @Test
    fun separate_registries_should_not_share_memory() {

        val first =
            DefaultRuntimeMemoryRegistry()

        val second =
            DefaultRuntimeMemoryRegistry()

        first.register(
            RuntimeMemoryType.SEMANTIC,
            FakeMemoryProvider(),
            setOf(
                RuntimeMemoryCapability.READ
            )
        )

        assertNotNull(
            first.provider(
                RuntimeMemoryType.SEMANTIC
            )
        )

        assertNull(
            second.provider(
                RuntimeMemoryType.SEMANTIC
            )
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
