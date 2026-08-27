package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class RuntimeCompositionKnowledgeSupersessionEndToEndContractTest {

    @Test
    fun composition_retrieval_exposes_only_stronger_superseding_knowledge() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val memory =
            lifecycle.memory()

        val weak =
            RuntimeKnowledge(
                statement =
                    "runtime supersession operational knowledge",
                confidence = 0.65,
                source =
                    RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 1L
            )

        val strong =
            RuntimeKnowledge(
                statement =
                    "Runtime supersession operational knowledge.",
                confidence = 0.95,
                source =
                    RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 2L
            )

        lifecycle.create(weak)
        lifecycle.create(strong)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ARCHIVED,
            memory.getLifecycleState(weak)
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.getLifecycleState(strong)
        )

        val results =
            memory.retrieveRelevant(
                "runtime supersession operational knowledge"
            )

        assertTrue(
            results.any {
                it.knowledge == strong
            }
        )

        assertFalse(
            results.any {
                it.knowledge == weak
            }
        )
    }
}
