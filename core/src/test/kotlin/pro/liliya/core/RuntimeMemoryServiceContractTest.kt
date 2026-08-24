package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.memory.access.RuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.service.DefaultRuntimeMemoryService

class RuntimeMemoryServiceContractTest {

    @Test
    fun service_should_delegate_store_and_search() {

        val access =
            FakeMemoryAccess()

        val service =
            DefaultRuntimeMemoryService(
                access
            )

        service.store(
            RuntimeMemoryType.WORKING,
            RuntimeMemoryEntry(
                id = "1",
                content = "test",
                type = RuntimeMemoryType.WORKING,
                confidence = 1.0,
                createdAt = 1L
            )
        )

        val result =
            service.search(
                RuntimeMemoryType.WORKING,
                "query"
            )

        assertEquals(
            1,
            access.storeCount
        )

        assertEquals(
            1,
            result.size
        )
    }


    private class FakeMemoryAccess :
        RuntimeMemoryAccess {

        var storeCount = 0

        override fun store(
            type: RuntimeMemoryType,
            entry: RuntimeMemoryEntry
        ) {
            storeCount++
        }

        override fun search(
            type: RuntimeMemoryType,
            query: String
        ): List<RuntimeMemoryEntry> {

            return listOf(
                RuntimeMemoryEntry(
                    id = "1",
                    content = "result",
                    type = type,
                    confidence = 1.0,
                    createdAt = 1L
                )
            )
        }
    }
}
