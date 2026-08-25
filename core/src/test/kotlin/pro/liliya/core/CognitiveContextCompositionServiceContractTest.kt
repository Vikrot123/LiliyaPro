package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition
import pro.liliya.core.runtime.intelligence.context.cognitive.service.CognitiveContextService
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class CognitiveContextCompositionServiceContractTest {

    @Test
    fun composition_should_expose_service() {
        val composition = DefaultCognitiveContextComposition()

        assertNotNull(composition.service())
    }

    @Test
    fun composition_should_keep_same_service_instance() {
        val composition = DefaultCognitiveContextComposition()

        assertSame(
            composition.service(),
            composition.service()
        )
    }

    @Test
    fun service_should_use_composition_context() {
        val composition = DefaultCognitiveContextComposition()

        assertSame(
            composition.context(),
            composition.service().context()
        )
    }

    @Test
    fun service_should_delegate_to_composition_context() {
        val composition = DefaultCognitiveContextComposition()

        val snapshot = composition.service().snapshot(
            CognitiveContextType.TASK
        )

        assertEquals(
            CognitiveContextType.TASK,
            snapshot.type
        )
    }

    @Test
    fun separate_compositions_should_not_share_service() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        assertNotSame(
            first.service(),
            second.service()
        )
    }
}
