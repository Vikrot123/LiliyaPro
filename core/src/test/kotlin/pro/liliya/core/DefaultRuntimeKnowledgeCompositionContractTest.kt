package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.composition.DefaultRuntimeKnowledgeComposition

class DefaultRuntimeKnowledgeCompositionContractTest {

    @Test
    fun composition_owns_stable_knowledge_extractor() {
        val composition = DefaultRuntimeKnowledgeComposition()

        val extractor = composition.knowledgeExtractor()

        assertSame(
            extractor,
            composition.knowledgeExtractor()
        )
    }

    @Test
    fun separate_compositions_do_not_share_knowledge_extractor() {
        val first = DefaultRuntimeKnowledgeComposition()
        val second = DefaultRuntimeKnowledgeComposition()

        assertNotSame(
            first.knowledgeExtractor(),
            second.knowledgeExtractor()
        )
    }
}
