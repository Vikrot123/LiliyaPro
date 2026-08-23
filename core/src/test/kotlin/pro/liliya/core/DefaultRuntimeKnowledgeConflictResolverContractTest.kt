package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.conflict.DefaultRuntimeKnowledgeConflictResolver
import pro.liliya.core.runtime.intelligence.knowledge.conflict.RuntimeKnowledgeConflict
import pro.liliya.core.runtime.intelligence.knowledge.conflict.RuntimeKnowledgeConflictType

class DefaultRuntimeKnowledgeConflictResolverContractTest {

    private fun knowledge(
        confidence: Double
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "runtime state",
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun higher_confidence_knowledge_is_selected() {

        val result =
            DefaultRuntimeKnowledgeConflictResolver()
                .resolve(
                    RuntimeKnowledgeConflict(
                        first = knowledge(0.9),
                        second = knowledge(0.5),
                        type = RuntimeKnowledgeConflictType.STATEMENT_CONTRADICTION,
                        createdAt = 1L
                    )
                )

        assertTrue(result.resolved)
        assertEquals(
            0.9,
            result.selectedKnowledge?.confidence
        )
    }

    @Test
    fun equal_confidence_keeps_conflict_unresolved() {

        val result =
            DefaultRuntimeKnowledgeConflictResolver()
                .resolve(
                    RuntimeKnowledgeConflict(
                        first = knowledge(0.8),
                        second = knowledge(0.8),
                        type = RuntimeKnowledgeConflictType.STATEMENT_CONTRADICTION,
                        createdAt = 1L
                    )
                )

        assertFalse(result.resolved)
        assertEquals(
            null,
            result.selectedKnowledge
        )
    }
}
