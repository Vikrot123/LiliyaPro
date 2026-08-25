package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextMultiSourceAggregationContractTest {

    @Test
    fun context_should_expose_all_registered_sources() {
        val composition = DefaultCognitiveContextComposition()

        val runtimeSource =
            composition.sourceRegistry()
                .sources()
                .first()

        assertNotNull(runtimeSource)

        val extraSource = source(
            "extra",
            "extra-value"
        )

        assertTrue(
            composition.service().registerSource(extraSource)
        )

        assertEquals(
            2,
            composition.context().sources().size
        )

        assertTrue(
            composition.context().sources().contains(runtimeSource)
        )

        assertTrue(
            composition.context().sources().contains(extraSource)
        )
    }

    @Test
    fun context_snapshot_should_aggregate_values_from_all_sources() {
        val composition = DefaultCognitiveContextComposition()

        val extraSource = source(
            "extra",
            "extra-value"
        )

        composition.service().registerSource(extraSource)

        val snapshot =
            composition.service()
                .snapshot(CognitiveContextType.WORKING)

        assertEquals(
            CognitiveContextType.WORKING,
            snapshot.type
        )

        assertTrue(
            snapshot.values.containsKey("runtimeState")
        )

        assertEquals(
            "extra-value",
            snapshot.values["extra"]
        )
    }

    @Test
    fun context_snapshot_should_ignore_sources_returning_null() {
        val composition = DefaultCognitiveContextComposition()

        val unavailableSource =
            object : CognitiveContextSource {
                override fun snapshot(
                    type: CognitiveContextType
                ): CognitiveContextSnapshot? {
                    return null
                }
            }

        assertTrue(
            composition.service().registerSource(unavailableSource)
        )

        val snapshot =
            composition.service()
                .snapshot(CognitiveContextType.TASK)

        assertEquals(
            CognitiveContextType.TASK,
            snapshot.type
        )

        assertTrue(
            snapshot.values.containsKey("runtimeState")
        )

        assertEquals(
            5,
            snapshot.values.size
        )
    }

    @Test
    fun unregistering_source_should_remove_it_from_future_context_snapshots() {
        val composition = DefaultCognitiveContextComposition()

        val extraSource = source(
            "extra",
            "temporary-value"
        )

        assertTrue(
            composition.service().registerSource(extraSource)
        )

        val before =
            composition.service()
                .snapshot(CognitiveContextType.WORKING)

        assertEquals(
            "temporary-value",
            before.values["extra"]
        )

        assertTrue(
            composition.service().unregisterSource(extraSource)
        )

        val after =
            composition.service()
                .snapshot(CognitiveContextType.WORKING)

        assertTrue(
            !after.values.containsKey("extra")
        )

        assertTrue(
            after.values.containsKey("runtimeState")
        )
    }

    @Test
    fun source_registration_order_should_define_snapshot_aggregation_order() {
        val composition = DefaultCognitiveContextComposition()

        val first = source(
            "first",
            "one"
        )

        val second = source(
            "second",
            "two"
        )

        composition.service().registerSource(first)
        composition.service().registerSource(second)

        val snapshot =
            composition.service()
                .snapshot(CognitiveContextType.TASK)

        val keys = snapshot.values.keys.toList()

        assertEquals(
            listOf(
                "runtimeState",
                "activeServices",
                "moduleStates",
                "failureReason",
                "runtimeTimestamp",
                "first",
                "second"
            ),
            keys
        )
    }

    private fun source(
        key: String,
        value: Any?
    ): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf(
                        key to value
                    )
                )
            }
        }
    }
}
