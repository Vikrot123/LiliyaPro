package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionKnowledgeLifecycleRestartIsolationContractTest {

    @Test
    fun stop_runtime_keeps_knowledge_lifecycle_composition_owner() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition.knowledgeLifecycleComposition()

        composition.stopRuntime()

        val after =
            composition.knowledgeLifecycleComposition()

        assertSame(
            before,
            after
        )
    }

    @Test
    fun separate_runtime_compositions_do_not_share_lifecycle_owner() {
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
