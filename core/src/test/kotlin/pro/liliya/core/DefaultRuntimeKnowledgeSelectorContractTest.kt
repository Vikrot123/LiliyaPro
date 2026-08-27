package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.DefaultRuntimeKnowledgeSelector
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

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


    @Test
    fun relevance_matching_is_case_insensitive() {
        val related =
            knowledge(
                "RUNTIME OPERATIONAL STATE REMAINED STABLE",
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
    fun punctuation_does_not_break_relevance_matching() {
        val related =
            knowledge(
                "runtime, operational-state: remained stable!",
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
    fun repeated_tokens_do_not_artificially_create_relevance() {
        val relevant =
            knowledge(
                "stable operational state observed",
                0.80,
                1L
            )

        val repeatedGeneric =
            knowledge(
                "runtime runtime runtime runtime backup",
                0.99,
                2L
            )

        assertEquals(
            relevant,
            selector.select(
                listOf(relevant, repeatedGeneric),
                "Runtime maintains stable operational state"
            )
        )
    }


    @Test
    fun exactly_half_of_interpretation_tokens_is_relevant() {
        val boundary =
            knowledge(
                "runtime maintains unrelated knowledge",
                0.80,
                1L
            )

        val strongerIrrelevant =
            knowledge(
                "network recovery completed",
                0.99,
                2L
            )

        assertEquals(
            boundary,
            selector.select(
                listOf(boundary, strongerIrrelevant),
                "runtime maintains stable operational"
            )
        )
    }

    @Test
    fun below_half_of_interpretation_tokens_is_not_relevant() {
        val belowThreshold =
            knowledge(
                "runtime maintains unrelated knowledge",
                0.80,
                1L
            )

        val fallbackWinner =
            knowledge(
                "network recovery completed",
                0.99,
                2L
            )

        assertEquals(
            fallbackWinner,
            selector.select(
                listOf(belowThreshold, fallbackWinner),
                "runtime maintains stable operational state"
            )
        )
    }


    @Test
    fun selection_result_reports_relevant_pool_usage() {
        val relevant =
            knowledge(
                "runtime operational state remained stable",
                0.80,
                1L
            )

        val irrelevant =
            knowledge(
                "network recovery completed",
                0.99,
                2L
            )

        val result =
            selector.selectResult(
                listOf(relevant, irrelevant),
                "Runtime maintains stable operational state"
            )

        assertEquals(
            relevant,
            result.knowledge
        )

        assertEquals(
            true,
            result.relevantPoolUsed
        )
    }


    @Test
    fun selection_result_reports_fallback_pool_usage() {
        val weaker =
            knowledge(
                "first unrelated knowledge",
                0.80,
                1L
            )

        val stronger =
            knowledge(
                "second unrelated knowledge",
                0.99,
                2L
            )

        val result =
            selector.selectResult(
                listOf(weaker, stronger),
                "Runtime maintains stable operational state"
            )

        assertEquals(
            stronger,
            result.knowledge
        )

        assertEquals(
            false,
            result.relevantPoolUsed
        )
    }


    @Test
    fun selection_result_reports_typed_relevant_reason() {
        val relevant =
            knowledge(
                "runtime operational state remained stable",
                0.80,
                1L
            )

        val result =
            selector.selectResult(
                listOf(relevant),
                "Runtime maintains stable operational state"
            )

        assertEquals(
            RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
            result.selectionReason
        )
    }

    @Test
    fun selection_result_reports_typed_fallback_reason() {
        val fallback =
            knowledge(
                "unrelated knowledge",
                0.80,
                1L
            )

        val result =
            selector.selectResult(
                listOf(fallback),
                "Runtime maintains stable operational state"
            )

        assertEquals(
            RuntimeKnowledgeSelectionReason.FALLBACK_POOL,
            result.selectionReason
        )
    }

    @Test
    fun selection_result_reports_empty_reason() {
        val result =
            selector.selectResult(
                emptyList(),
                "Runtime maintains stable operational state"
            )

        assertEquals(
            null,
            result.knowledge
        )

        assertEquals(
            RuntimeKnowledgeSelectionReason.EMPTY,
            result.selectionReason
        )
    }


    @Test
    fun exact_relevance_reports_full_score() {
        val exact =
            knowledge(
                "Runtime maintains stable operational state",
                0.80,
                1L
            )

        val result =
            selector.selectResult(
                listOf(exact),
                "Runtime maintains stable operational state"
            )

        assertEquals(
            1.0,
            result.relevanceScore
        )
    }

    @Test
    fun threshold_relevance_reports_half_score() {
        val boundary =
            knowledge(
                "runtime maintains unrelated knowledge",
                0.80,
                1L
            )

        val result =
            selector.selectResult(
                listOf(boundary),
                "runtime maintains stable operational"
            )

        assertEquals(
            0.5,
            result.relevanceScore
        )
    }

    @Test
    fun fallback_selection_reports_zero_relevance_score() {
        val fallback =
            knowledge(
                "network recovery completed",
                0.99,
                1L
            )

        val result =
            selector.selectResult(
                listOf(fallback),
                "Runtime maintains stable operational state"
            )

        assertEquals(
            RuntimeKnowledgeSelectionReason.FALLBACK_POOL,
            result.selectionReason
        )

        assertEquals(
            0.0,
            result.relevanceScore
        )
    }


    @Test
    fun relevance_score_must_not_override_confidence_inside_relevant_pool() {
        val higherRelevance =
            knowledge(
                "runtime maintains stable operational state",
                0.80,
                1L
            )

        val higherConfidence =
            knowledge(
                "runtime stable operational knowledge",
                0.95,
                2L
            )

        val result =
            selector.selectResult(
                listOf(
                    higherRelevance,
                    higherConfidence
                ),
                "runtime maintains stable operational state"
            )

        assertEquals(
            higherConfidence,
            result.knowledge,
            "relevance score must remain diagnostic and must not override confidence"
        )

        assertEquals(
            RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
            result.selectionReason
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
