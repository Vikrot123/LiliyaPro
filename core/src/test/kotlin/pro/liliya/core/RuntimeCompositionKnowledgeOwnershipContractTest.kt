package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.composition.RuntimeKnowledgeComposition

class RuntimeCompositionKnowledgeOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_knowledge_composition() {
        val composition = DefaultRuntimeComposition()

        val knowledge = composition.knowledgeComposition()

        assertSame(
            knowledge,
            composition.knowledgeComposition()
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_knowledge_composition() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.knowledgeComposition(),
            second.knowledgeComposition()
        )
    }

    @Test
    fun root_knowledge_composition_exposes_expected_contract_type() {
        val composition = DefaultRuntimeComposition()

        val knowledge: RuntimeKnowledgeComposition =
            composition.knowledgeComposition()

        assertSame(
            knowledge,
            composition.knowledgeComposition()
        )
    }
}
