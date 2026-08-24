package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionKnowledgeLifecycleOwnershipContractTest {

    @Test
    fun composition_owns_single_knowledge_lifecycle_instance() {
        val composition = DefaultRuntimeComposition()

        val first =
            composition.knowledgeLifecycleComposition()

        val second =
            composition.knowledgeLifecycleComposition()

        assertSame(first, second)
    }

    @Test
    fun different_compositions_own_isolated_knowledge_lifecycle_instances() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.knowledgeLifecycleComposition(),
            second.knowledgeLifecycleComposition()
        )
    }
}
