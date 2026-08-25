package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextSourceFailureIsolationContractTest {

    @Test
    fun failing_source_should_not_prevent_other_sources_from_contributing() {
        val composition = DefaultCognitiveContextComposition()

        val healthySource = source(
            "healthy",
            "available"
        )

        val failingSource = failingSource()

        assertTrue(
            composition.service().registerSource(healthySource)
        )

        assertTrue(
            composition.service().registerSource(failingSource)
        )

        val snapshot = composition
            .service()
            .snapshot(CognitiveContextType.WORKING)

        assertEquals(
            "available",
            snapshot.values["healthy"]
        )

        assertTrue(
            snapshot.values.containsKey("runtimeState")
        )
    }

    @Test
    fun failing_source_should_not_prevent_sources_registered_after_it() {
        val composition = DefaultCognitiveContextComposition()

        val firstHealthy = source(
            "first",
            "one"
        )

        val failing = failingSource()

        val secondHealthy = source(
            "second",
            "two"
        )

        assertTrue(
            composition.service().registerSource(firstHealthy)
        )

        assertTrue(
            composition.service().registerSource(failing)
        )

        assertTrue(
            composition.service().registerSource(secondHealthy)
        )

        val snapshot = composition
            .service()
            .snapshot(CognitiveContextType.TASK)

        assertEquals(
            "one",
            snapshot.values["first"]
        )

        assertEquals(
            "two",
            snapshot.values["second"]
        )

        assertTrue(
            snapshot.values.containsKey("runtimeState")
        )
    }

    @Test
    fun multiple_failing_sources_should_not_destroy_healthy_context() {
        val composition = DefaultCognitiveContextComposition()

        assertTrue(
            composition.service().registerSource(
                source("first", "one")
            )
        )

        assertTrue(
            composition.service().registerSource(
                failingSource()
            )
        )

        assertTrue(
            composition.service().registerSource(
                failingSource()
            )
        )

        assertTrue(
            composition.service().registerSource(
                source("last", "two")
            )
        )

        val snapshot = composition
            .service()
            .snapshot(CognitiveContextType.PROCESSOR)

        assertEquals(
            "one",
            snapshot.values["first"]
        )

        assertEquals(
            "two",
            snapshot.values["last"]
        )

        assertTrue(
            snapshot.values.containsKey("runtimeState")
        )
    }

    @Test
    fun failing_source_should_not_remove_already_aggregated_values() {
        val composition = DefaultCognitiveContextComposition()

        val first = source(
            "beforeFailure",
            "preserved"
        )

        assertTrue(
            composition.service().registerSource(first)
        )

        assertTrue(
            composition.service().registerSource(
                failingSource()
            )
        )

        val snapshot = composition
            .service()
            .snapshot(CognitiveContextType.GLOBAL)

        assertEquals(
            "preserved",
            snapshot.values["beforeFailure"]
        )
    }

    @Test
    fun source_failure_should_be_isolated_to_the_failing_source() {
        val composition = DefaultCognitiveContextComposition()

        val healthy = source(
            "healthy",
            "ok"
        )

        val failing = failingSource()

        assertTrue(
            composition.service().registerSource(healthy)
        )

        assertTrue(
            composition.service().registerSource(failing)
        )

        val snapshot = composition
            .service()
            .snapshot(CognitiveContextType.TEMPORARY)

        assertEquals(
            CognitiveContextType.TEMPORARY,
            snapshot.type
        )

        assertEquals(
            "ok",
            snapshot.values["healthy"]
        )

        assertTrue(
            snapshot.values.keys.contains("runtimeState")
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

    private fun failingSource(): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                throw IllegalStateException(
                    "intentional cognitive source failure"
                )
            }
        }
    }
}
