package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.history.DefaultRuntimeReflectionHistory

class RuntimeReflectionHistoryContractTest {

    @Test
    fun history_records_reflection_snapshots_in_order() {

        val history =
            DefaultRuntimeReflectionHistory()

        val first =
            RuntimeReflectionSnapshot(
                summary = "first",
                healthy = true,
                analyzedAt = 1L
            )

        val second =
            RuntimeReflectionSnapshot(
                summary = "second",
                healthy = false,
                analyzedAt = 2L
            )

        history.record(first)
        history.record(second)

        val snapshots =
            history.snapshots()

        assertEquals(
            listOf(first, second),
            snapshots
        )
    }

    @Test
    fun history_does_not_expose_internal_storage() {

        val history =
            DefaultRuntimeReflectionHistory()

        history.record(
            RuntimeReflectionSnapshot(
                summary = "state",
                healthy = true,
                analyzedAt = 1L
            )
        )

        val first =
            history.snapshots()

        val second =
            history.snapshots()

        assertNotSame(
            first,
            second
        )
    }
}
