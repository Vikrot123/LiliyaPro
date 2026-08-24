package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionKnowledgeLifecycleObserverOwnershipContractTest {

    @Test
    fun composition_keeps_single_observer_provider_path() {
        val composition = DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val first =
            lifecycle
                .lifecycleService()

        val second =
            lifecycle
                .lifecycleService()

        assertSame(first, second)
    }

    @Test
    fun separate_compositions_do_not_share_observer_state() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.knowledgeLifecycleComposition()
                .lifecycleService(),

            second.knowledgeLifecycleComposition()
                .lifecycleService()
        )
    }

    @Test
    fun reset_keeps_observer_ownership_inside_composition() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val before =
            lifecycle.lifecycleService()

        lifecycle.reset()

        val after =
            lifecycle.lifecycleService()

        assertNotSame(
            before,
            after
        )
    }
}
