package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextProcessSourceFailureIsolationContractTest {

    @Test
    fun process_should_continue_when_one_registered_source_fails() {
        val composition = DefaultCognitiveContextComposition()

        val healthy = source("healthy", "available")
        val failing = failingSource()

        assertTrue(composition.service().registerSource(healthy))
        assertTrue(composition.service().registerSource(failing))

        val result = composition.service().process(
            type = CognitiveContextType.WORKING
        )

        assertEquals(2, result.selectedCount)
        assertEquals(0, result.rejectedCount)

        assertNotNull(result.values["source_0"])
        assertNotNull(result.values["source_1"])

        val runtimeSnapshot =
            result.values["source_0"] as CognitiveContextSnapshot

        val healthySnapshot =
            result.values["source_1"] as CognitiveContextSnapshot

        assertEquals(
            CoreRuntimeState.STOPPED.name,
            runtimeSnapshot.values["runtimeState"]
        )

        assertEquals(
            "available",
            healthySnapshot.values["healthy"]
        )

        assertTrue(!result.values.containsKey("source_2"))
    }

    @Test
    fun process_should_continue_with_sources_registered_after_failure() {
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
                source("last", "two")
            )
        )

        val result = composition.service().process(
            type = CognitiveContextType.TASK
        )

        assertEquals(3, result.selectedCount)
        assertEquals(0, result.rejectedCount)

        assertNotNull(result.values["source_0"])
        assertNotNull(result.values["source_1"])
        assertNotNull(result.values["source_3"])

        val first =
            result.values["source_1"] as CognitiveContextSnapshot

        val last =
            result.values["source_3"] as CognitiveContextSnapshot

        assertEquals(
            "one",
            first.values["first"]
        )

        assertEquals(
            "two",
            last.values["last"]
        )

        assertTrue(!result.values.containsKey("source_2"))
    }

    @Test
    fun process_should_preserve_source_order_when_a_source_fails() {
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
                source("second", "two")
            )
        )

        val result = composition.service().process(
            type = CognitiveContextType.PROCESSOR
        )

        assertEquals(
            listOf(
                "source_0",
                "source_1",
                "source_3"
            ),
            result.values.keys.toList()
        )

        assertTrue(!result.values.containsKey("source_2"))

        val first =
            result.values["source_1"] as CognitiveContextSnapshot

        val second =
            result.values["source_3"] as CognitiveContextSnapshot

        assertEquals(
            "one",
            first.values["first"]
        )

        assertEquals(
            "two",
            second.values["second"]
        )
    }

    @Test
    fun process_should_ignore_null_source_snapshots() {
        val composition = DefaultCognitiveContextComposition()

        assertTrue(
            composition.service().registerSource(
                source("first", "one")
            )
        )

        assertTrue(
            composition.service().registerSource(
                nullSource()
            )
        )

        assertTrue(
            composition.service().registerSource(
                source("last", "two")
            )
        )

        val result = composition.service().process(
            type = CognitiveContextType.GLOBAL
        )

        assertEquals(3, result.selectedCount)
        assertEquals(0, result.rejectedCount)

        assertNotNull(result.values["source_0"])
        assertNotNull(result.values["source_1"])
        assertNotNull(result.values["source_3"])

        assertTrue(!result.values.containsKey("source_2"))
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

    private fun nullSource(): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot? {
                return null
            }
        }
    }

    private fun failingSource(): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                throw IllegalStateException(
                    "intentional cognitive process source failure"
                )
            }
        }
    }
}
