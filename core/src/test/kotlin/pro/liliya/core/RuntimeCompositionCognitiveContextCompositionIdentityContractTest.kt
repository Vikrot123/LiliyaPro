package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextCompositionIdentityContractTest {

    @Test
    fun runtime_composition_should_expose_same_cognitive_composition_instance() {
        val composition = DefaultRuntimeComposition()

        assertSame(
            composition.cognitiveContextComposition(),
            composition.cognitiveContextComposition()
        )
    }

    @Test
    fun separate_runtime_compositions_should_not_share_cognitive_composition() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.cognitiveContextComposition(),
            second.cognitiveContextComposition()
        )
    }
}
