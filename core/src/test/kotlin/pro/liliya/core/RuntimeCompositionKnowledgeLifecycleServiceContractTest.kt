package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionKnowledgeLifecycleServiceContractTest {

    @Test
    fun composition_exposes_knowledge_lifecycle_service() {
        val composition =
            DefaultRuntimeComposition()

        val service =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleService()

        assertNotNull(service)
    }

    @Test
    fun composition_returns_same_knowledge_lifecycle_service() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleService()

        val second =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleService()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun separate_compositions_do_not_share_knowledge_services() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first
                .knowledgeLifecycleComposition()
                .lifecycleService(),

            second
                .knowledgeLifecycleComposition()
                .lifecycleService()
        )
    }
}
