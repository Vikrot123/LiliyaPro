package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextCompositionPipelineContractTest {

    @Test
    fun composition_should_own_pipeline() {
        val composition = DefaultCognitiveContextComposition()

        assertNotNull(composition.pipeline())
        assertSame(
            composition.pipeline(),
            composition.pipeline()
        )
    }

    @Test
    fun service_should_use_composition_pipeline() {
        val composition = DefaultCognitiveContextComposition()

        assertSame(
            composition.pipeline(),
            composition.service().pipeline()
        )
    }

    @Test
    fun service_should_process_through_pipeline() {
        val composition = DefaultCognitiveContextComposition()

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("value" to "test")
                )
            }
        }

        composition.service().registerSource(source)

        val result = composition.service().process(
            type = CognitiveContextType.TASK
        )

        assertEquals(2, result.selectedCount)
        assertEquals(0, result.rejectedCount)
    }

    @Test
    fun separate_compositions_should_not_share_pipeline() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        assertNotSame(
            first.pipeline(),
            second.pipeline()
        )
    }
}
