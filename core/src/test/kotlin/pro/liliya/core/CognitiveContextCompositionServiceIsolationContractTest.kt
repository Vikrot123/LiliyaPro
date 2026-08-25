package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextCompositionServiceIsolationContractTest {

    private fun source(value: String): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("value" to value)
                )
            }
        }
    }

    @Test
    fun separate_compositions_should_not_share_source_registry_state() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        val firstSource = source("first")

        assertTrue(
            first.service().registerSource(firstSource)
        )

        assertEquals(
            2,
            first.service().sourceRegistry().size()
        )

        assertEquals(
            1,
            second.service().sourceRegistry().size()
        )
    }

    @Test
    fun separate_compositions_should_not_share_context_state() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        val firstSource = source("first")

        first.service().registerSource(firstSource)

        val firstResult = first.service().process(
            CognitiveContextType.TASK
        )

        val secondResult = second.service().process(
            CognitiveContextType.TASK
        )

        assertEquals(2, firstResult.selectedCount)
        assertEquals(1, secondResult.selectedCount)

        assertTrue(
            firstResult.values.values.any {
                it is CognitiveContextSnapshot &&
                    it.values["value"] == "first"
            }
        )

        assertTrue(
            secondResult.values.values.none {
                it is CognitiveContextSnapshot &&
                    it.values["value"] == "first"
            }
        )
    }

    @Test
    fun separate_compositions_should_not_share_pipeline_instance() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        assertNotSame(
            first.service().pipeline(),
            second.service().pipeline()
        )
    }

    @Test
    fun unregistering_source_from_one_composition_should_not_affect_another() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        val firstSource = source("first")

        first.service().registerSource(firstSource)
        second.service().registerSource(firstSource)

        assertTrue(
            first.service().unregisterSource(firstSource)
        )

        assertEquals(
            1,
            first.service().sourceRegistry().size()
        )

        assertEquals(
            2,
            second.service().sourceRegistry().size()
        )
    }
}
