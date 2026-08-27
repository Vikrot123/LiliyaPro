package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.DefaultRuntimeKnowledgeSupersessionHistory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query.DefaultRuntimeKnowledgeSupersessionQuery

class RuntimeKnowledgeSupersessionReverseTraceContractTest {

    @Test
    fun current_winner_resolves_observable_ancestry() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first =
            knowledge("first", 0.60, 1L)

        val second =
            knowledge("second", 0.80, 2L)

        val third =
            knowledge("third", 0.95, 3L)

        history.record(first, second)
        history.record(second, third)

        val trace =
            DefaultRuntimeKnowledgeSupersessionQuery(
                history
            ).traceTo(
                third
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
            first,
            trace.startingKnowledge
        )

        assertEquals(
            third,
            trace.currentKnowledge
        )

        assertFalse(
            trace.cycleDetected
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
