package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.DefaultRuntimeKnowledgeSelector

class DefaultRuntimeKnowledgeSelectorContractTest {

    private val selector =
        DefaultRuntimeKnowledgeSelector()

    @Test
    fun relevance_precedes_confidence() {
        val relevant =
            knowledge(
                "runtime operational state remained stable",
                0.8,
                1L
            )

        val irrelevant =
            knowledge(
                "network recovery completed",
                0.99,
                2L
            )

        assertEquals(
            relevant,
            selector.select(
                listOf(relevant, irrelevant),
                "Runtime maintains stable operational state"
            )
        )
    }

    @Test
    fun confidence_then_recency_is_used_inside_selection_pool() {
        val older =
            knowledge(
                "first knowledge",
                0.9,
                1L
            )

        val newer =
            knowledge(
                "second knowledge",
                0.9,
                2L
            )

        assertEquals(
            newer,
            selector.select(
                listOf(older, newer),
                "unmatched interpretation"
            )
        )
    }


    @Test
    fun empty_knowledge_returns_null() {
        assertEquals(
            null,
            selector.select(
                emptyList(),
                "Runtime maintains stable operational state"
            )
        )
    }

    @Test
    fun flexible_relevance_matches_related_wording() {
        val related =
            knowledge(
                "runtime operational state remained stable",
                0.80,
                1L
            )

        val unrelated =
            knowledge(
                "network recovery completed",
                0.99,
                2L
            )

        assertEquals(
            related,
            selector.select(
                listOf(related, unrelated),
                "Runtime maintains stable operational state"
            )
        )
    }

    @Test
    fun one_shared_generic_token_is_not_relevant() {
        val related =
            knowledge(
                "stable operational state observed",
                0.80,
                1L
            )

        val falsePositive =
            knowledge(
                "runtime backup completed",
                0.99,
                2L
            )

        assertEquals(
            related,
            selector.select(
                listOf(related, falsePositive),
                "Runtime maintains stable operational state"
            )
        )
    }

    private fun knowledge(
        statement: String,
        confidence: Double,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = createdAt
        )
}
