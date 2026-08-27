package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.DefaultRuntimeKnowledgeSupersessionHistory

class DefaultRuntimeKnowledgeSupersessionHistoryContractTest {

    @Test
    fun history_is_bounded_and_preserves_newest_lineage() {
        var now = 0L

        val history =
            DefaultRuntimeKnowledgeSupersessionHistory(
                capacity = 2,
                clock = {
                    now += 1L
                    now
                }
            )

        val first = knowledge("first", 1L)
        val second = knowledge("second", 2L)
        val third = knowledge("third", 3L)
        val fourth = knowledge("fourth", 4L)

        history.record(first, second)
        history.record(second, third)
        history.record(third, fourth)

        val records =
            history.records()

        assertEquals(
            2,
            records.size
        )

        assertEquals(
            listOf(second, third),
            records.map {
                it.previousKnowledge
            }
        )

        assertEquals(
            listOf(third, fourth),
            records.map {
                it.replacementKnowledge
            }
        )

        assertEquals(
            listOf(2L, 3L),
            records.map {
                it.recordedAt
            }
        )
    }

    @Test
    fun replacements_can_be_queried_by_previous_knowledge() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first = knowledge("first", 1L)
        val second = knowledge("second", 2L)

        history.record(
            first,
            second
        )

        assertEquals(
            second,
            history
                .replacementsOf(first)
                .single()
                .replacementKnowledge
        )
    }

    @Test
    fun clear_removes_lineage() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        history.record(
            knowledge("first", 1L),
            knowledge("second", 2L)
        )

        history.clear()

        assertEquals(
            emptyList(),
            history.records()
        )
    }

    private fun knowledge(
        statement: String,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = 0.9,
            source =
                RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = createdAt
        )
}
