package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.memory.access.DefaultRuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistration

class DefaultRuntimeMemoryAccessRegistrationConsistencyContractTest {

    @Test
    fun store_must_not_mix_capabilities_and_provider_from_different_registrations() {
        val authorizedProvider =
            CountingMemoryProvider()

        val unauthorizedProvider =
            CountingMemoryProvider()

        val registry =
            SwitchingMemoryRegistry(
                authorizedProvider = authorizedProvider,
                unauthorizedProvider = unauthorizedProvider
            )

        val access =
            DefaultRuntimeMemoryAccess(
                registry = registry
            )

        access.store(
            RuntimeMemoryType.WORKING,
            RuntimeMemoryEntry(
                id = "1",
                content = "must remain registration-consistent",
                type = RuntimeMemoryType.WORKING,
                confidence = 1.0,
                createdAt = 1L
            )
        )

        assertEquals(
            0,
            unauthorizedProvider.storeCount,
            "WRITE capability from one registration must not authorize a different provider"
        )
    }

    private class SwitchingMemoryRegistry(
        private val authorizedProvider: RuntimeMemoryProvider,
        private val unauthorizedProvider: RuntimeMemoryProvider
    ) : RuntimeMemoryRegistry {

        override fun register(
            type: RuntimeMemoryType,
            provider: RuntimeMemoryProvider,
            capabilities: Set<RuntimeMemoryCapability>
        ) {
        }

        override fun unregister(
            type: RuntimeMemoryType
        ) {
        }

        override fun registration(
            type: RuntimeMemoryType
        ): RuntimeMemoryRegistration {
            return RuntimeMemoryRegistration(
                provider = authorizedProvider,
                capabilities = setOf(
                    RuntimeMemoryCapability.WRITE
                )
            )
        }

        override fun provider(
            type: RuntimeMemoryType
        ): RuntimeMemoryProvider {
            return unauthorizedProvider
        }

        override fun capabilities(
            type: RuntimeMemoryType
        ): Set<RuntimeMemoryCapability> {
            return setOf(
                RuntimeMemoryCapability.WRITE
            )
        }
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
            return emptyList()
        }

        override fun clear() {
        }
    }
}
