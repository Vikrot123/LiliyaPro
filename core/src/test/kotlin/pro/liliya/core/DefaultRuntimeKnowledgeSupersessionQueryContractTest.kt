package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.DefaultRuntimeKnowledgeSupersessionHistory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query.DefaultRuntimeKnowledgeSupersessionQuery

class DefaultRuntimeKnowledgeSupersessionQueryContractTest {

    @Test
    fun trace_reconstructs_multi_step_supersession_chain() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first =
            knowledge(
                "first",
                0.60,
                1L
            )

        val second =
            knowledge(
                "second",
                0.80,
                2L
            )

        val third =
            knowledge(
                "third",
                0.95,
                3L
            )

        history.record(
            first,
            second
        )

        history.record(
            second,
            third
        )

        val query =
            DefaultRuntimeKnowledgeSupersessionQuery(
                history
            )

        val trace =
            query.trace(
                first
            )

        assertEquals(
            listOf(
                first,
                second,
                third
            ),
            trace.chain
        )

        assertEquals(
            third,
            trace.currentKnowledge
        )

        assertFalse(
            trace.cycleDetected
        )
    }

    @Test
    fun knowledge_without_lineage_resolves_to_itself() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val knowledge =
            knowledge(
                "standalone",
                0.90,
                1L
            )

        val query =
            DefaultRuntimeKnowledgeSupersessionQuery(
                history
            )

        val trace =
            query.trace(
                knowledge
            )

        assertEquals(
            listOf(knowledge),
            trace.chain
        )

        assertEquals(
            knowledge,
            trace.currentKnowledge
        )

        assertFalse(
            trace.cycleDetected
        )
    }

    @Test
    fun current_knowledge_returns_final_known_winner() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first =
            knowledge(
                "first",
                0.60,
                1L
            )

        val second =
            knowledge(
                "second",
                0.80,
                2L
            )

        val third =
            knowledge(
                "third",
                0.95,
                3L
            )

        history.record(first, second)
        history.record(second, third)

        val query =
            DefaultRuntimeKnowledgeSupersessionQuery(
                history
            )

        assertEquals(
            third,
            query.currentKnowledge(first)
        )
    }

    @Test
    fun cycle_is_detected_without_infinite_traversal() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first =
            knowledge(
                "first",
                0.80,
                1L
            )

        val second =
            knowledge(
                "second",
                0.90,
                2L
            )

        history.record(
            first,
            second
        )

        history.record(
            second,
            first
        )

        val trace =
            DefaultRuntimeKnowledgeSupersessionQuery(
                history
            ).trace(
                first
            )

        assertEquals(
            listOf(
                first,
                second
            ),
            trace.chain
        )

        assertEquals(
            second,
            trace.currentKnowledge
        )

        assertTrue(
            trace.cycleDetected
        )
    }

    @Test
    fun bounded_history_query_uses_only_retained_lineage() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory(
                capacity = 2
            )

        val first =
            knowledge(
                "first",
                0.60,
                1L
            )

        val second =
            knowledge(
                "second",
                0.70,
                2L
            )

        val third =
            knowledge(
                "third",
                0.80,
                3L
            )

        val fourth =
            knowledge(
                "fourth",
                0.90,
                4L
            )

        history.record(first, second)
        history.record(second, third)
        history.record(third, fourth)

        val query =
            DefaultRuntimeKnowledgeSupersessionQuery(
                history
            )

        assertEquals(
            listOf(
                second,
                third,
                fourth
            ),
            query.trace(second).chain
        )

        assertEquals(
            listOf(first),
            query.trace(first).chain,
            "query must not invent lineage already evicted from bounded history"
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
            source =
                RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = createdAt
        )
}
