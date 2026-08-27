package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory

class RuntimeKnowledgeLifecycleSemanticRetrievalContractTest {

    @Test
    fun lifecycle_memory_exposes_semantic_retrieval_through_owned_memory() {
        val lifecycle =
            DefaultRuntimeKnowledgeLifecycleMemory()

        val knowledge =
            RuntimeKnowledge(
                statement =
                    "runtime operational state remained stable",
                confidence = 0.90,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        lifecycle.create(knowledge)
        lifecycle.activate(knowledge)

        val results =
            lifecycle
                .memory()
                .retrieveRelevant(
                    "Runtime maintains stable operational state"
                )

        assertEquals(
            knowledge,
            results.single().knowledge
        )

        assertTrue(
            results.single().relevance >= 0.5
        )
    }

    @Test
    fun archived_knowledge_is_not_returned_by_semantic_retrieval() {
        val lifecycle =
            DefaultRuntimeKnowledgeLifecycleMemory()

        val knowledge =
            RuntimeKnowledge(
                statement =
                    "runtime operational state remained stable",
                confidence = 0.90,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        lifecycle.create(knowledge)
        lifecycle.activate(knowledge)

        assertEquals(
            1,
            lifecycle
                .memory()
                .retrieveRelevant(
                    "Runtime maintains stable operational state"
                )
                .size
        )

        lifecycle.revise(knowledge)
        lifecycle.archive(knowledge)

        assertEquals(
            emptyList(),
            lifecycle
                .memory()
                .retrieveRelevant(
                    "Runtime maintains stable operational state"
                )
        )
    }
}
