package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextCompositionServiceRegistryIdentityContractTest {

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
    fun composition_and_service_should_expose_same_registry_instance() {
        val composition = DefaultCognitiveContextComposition()

        assertSame(
            composition.sourceRegistry(),
            composition.service().sourceRegistry()
        )
    }

    @Test
    fun source_registered_through_composition_registry_should_reach_service_process() {
        val composition = DefaultCognitiveContextComposition()
        val source = source("composition")

        assertTrue(
            composition.sourceRegistry().register(source)
        )

        val result = composition.service().process(
            CognitiveContextType.TASK
        )

        assertEquals(2, result.selectedCount)

        assertTrue(
            result.values.values.any {
                it is CognitiveContextSnapshot &&
                    it.values["value"] == "composition"
            }
        )
    }

    @Test
    fun source_registered_through_service_should_reach_composition_registry() {
        val composition = DefaultCognitiveContextComposition()
        val source = source("service")

        assertTrue(
            composition.service().registerSource(source)
        )

        assertEquals(
            2,
            composition.sourceRegistry().size()
        )

        assertTrue(
            composition.sourceRegistry()
                .sources()
                .contains(source)
        )
    }

    @Test
    fun unregister_through_service_should_update_composition_registry() {
        val composition = DefaultCognitiveContextComposition()
        val source = source("shared")

        composition.sourceRegistry().register(source)

        assertTrue(
            composition.service().unregisterSource(source)
        )

        assertEquals(
            1,
            composition.sourceRegistry().size()
        )

        assertTrue(
            !composition.sourceRegistry()
                .sources()
                .contains(source)
        )
    }
}
