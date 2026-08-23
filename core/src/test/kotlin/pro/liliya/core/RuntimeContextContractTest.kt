package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.context.RuntimeContext
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot

class RuntimeContextContractTest {

    @Test
    fun context_provides_runtime_snapshot() {

        val context = object : RuntimeContext {

            override fun snapshot(): RuntimeContextSnapshot {
                return RuntimeContextSnapshot(
                    runtimeState = "RUNNING",
                    activeServices = listOf("service"),
                    timestamp = 1L
                )
            }
        }

        val snapshot = context.snapshot()

        assertEquals(
            "RUNNING",
            snapshot.runtimeState
        )

        assertEquals(
            listOf("service"),
            snapshot.activeServices
        )

        assertEquals(
            1L,
            snapshot.timestamp
        )
    }
}
