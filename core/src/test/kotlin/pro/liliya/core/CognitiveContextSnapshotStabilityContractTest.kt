package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextSnapshotStabilityContractTest {

    @Test
    fun repeated_snapshot_requests_should_create_independent_snapshots() {
        val composition = DefaultCognitiveContextComposition()

        val first = composition
            .service()
            .snapshot(CognitiveContextType.WORKING)

        val second = composition
            .service()
            .snapshot(CognitiveContextType.WORKING)

        assertNotSame(first, second)
        assertEquals(first.type, second.type)

        assertEquals(
            first.values.keys,
            second.values.keys
        )

        assertEquals(
            first.values["runtimeState"],
            second.values["runtimeState"]
        )

        assertEquals(
            first.values["activeServices"],
            second.values["activeServices"]
        )

        assertEquals(
            first.values["moduleStates"],
            second.values["moduleStates"]
        )

        assertEquals(
            first.values["failureReason"],
            second.values["failureReason"]
        )

        assertTrue(
            (first.values["runtimeTimestamp"] as Long) > 0L
        )

        assertTrue(
            (second.values["runtimeTimestamp"] as Long) > 0L
        )
    }

    @Test
    fun previous_snapshot_should_not_change_when_sources_are_added_later() {
        val composition = DefaultCognitiveContextComposition()

        val before = composition
            .service()
            .snapshot(CognitiveContextType.TASK)

        val extraSource = source(
            "late",
            "added-after-snapshot"
        )

        assertTrue(
            composition.service().registerSource(extraSource)
        )

        val after = composition
            .service()
            .snapshot(CognitiveContextType.TASK)

        assertTrue(
            !before.values.containsKey("late")
        )

        assertEquals(
            "added-after-snapshot",
            after.values["late"]
        )

        assertTrue(
            !before.values.containsKey("late")
        )
    }

    @Test
    fun previous_snapshot_should_not_change_when_sources_are_removed_later() {
        val composition = DefaultCognitiveContextComposition()

        val extraSource = source(
            "temporary",
            "present"
        )

        assertTrue(
            composition.service().registerSource(extraSource)
        )

        val before = composition
            .service()
            .snapshot(CognitiveContextType.WORKING)

        assertEquals(
            "present",
            before.values["temporary"]
        )

        assertTrue(
            composition.service().unregisterSource(extraSource)
        )

        val after = composition
            .service()
            .snapshot(CognitiveContextType.WORKING)

        assertEquals(
            "present",
            before.values["temporary"]
        )

        assertTrue(
            !after.values.containsKey("temporary")
        )
    }

    @Test
    fun snapshot_values_should_preserve_source_values_at_snapshot_time() {
        var currentValue = "first"

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf(
                        "dynamic" to currentValue
                    )
                )
            }
        }

        val composition = DefaultCognitiveContextComposition()

        assertTrue(
            composition.service().registerSource(source)
        )

        val first = composition
            .service()
            .snapshot(CognitiveContextType.PROCESSOR)

        currentValue = "second"

        val second = composition
            .service()
            .snapshot(CognitiveContextType.PROCESSOR)

        assertEquals(
            "first",
            first.values["dynamic"]
        )

        assertEquals(
            "second",
            second.values["dynamic"]
        )
    }

    @Test
    fun snapshot_should_reflect_current_source_registration_order_at_request_time() {
        val composition = DefaultCognitiveContextComposition()

        val first = source(
            "first",
            "one"
        )

        val second = source(
            "second",
            "two"
        )

        assertTrue(
            composition.service().registerSource(first)
        )

        val before = composition
            .service()
            .snapshot(CognitiveContextType.TASK)

        assertEquals(
            listOf(
                "runtimeState",
                "activeServices",
                "moduleStates",
                "failureReason",
                "runtimeTimestamp",
                "first"
            ),
            before.values.keys.toList()
        )

        assertTrue(
            composition.service().registerSource(second)
        )

        val after = composition
            .service()
            .snapshot(CognitiveContextType.TASK)

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
            after.values.keys.toList()
        )

        assertEquals(
            listOf(
                "runtimeState",
                "activeServices",
                "moduleStates",
                "failureReason",
                "runtimeTimestamp",
                "first"
            ),
            before.values.keys.toList()
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
