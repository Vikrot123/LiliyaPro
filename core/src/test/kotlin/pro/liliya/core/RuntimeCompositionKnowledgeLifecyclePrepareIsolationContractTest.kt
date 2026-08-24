package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionKnowledgeLifecyclePrepareIsolationContractTest {

    @Test
    fun prepare_runtime_keeps_knowledge_lifecycle_composition_owner() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition.knowledgeLifecycleComposition()

        composition.prepareRuntime()

        val after =
            composition.knowledgeLifecycleComposition()

        assertSame(
            before,
            after
        )
    }
}
