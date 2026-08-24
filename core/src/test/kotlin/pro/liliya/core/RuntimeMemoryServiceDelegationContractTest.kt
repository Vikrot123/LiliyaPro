package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryComposition

class RuntimeMemoryServiceDelegationContractTest {

    @Test
    fun composition_service_should_use_installed_memory_pipeline() {

        val composition =
            DefaultRuntimeMemoryComposition()

        composition.service()
            .store(
                RuntimeMemoryType.SEMANTIC,
                RuntimeMemoryEntry(
                    id = "1",
                    content = "memory",
                    type = RuntimeMemoryType.SEMANTIC,
                    confidence = 1.0,
                    createdAt = 1L
                )
            )

        val result =
            composition.service()
                .search(
                    RuntimeMemoryType.SEMANTIC,
                    "memory"
                )

        assertEquals(
            1,
            result.size
        )
    }
}
