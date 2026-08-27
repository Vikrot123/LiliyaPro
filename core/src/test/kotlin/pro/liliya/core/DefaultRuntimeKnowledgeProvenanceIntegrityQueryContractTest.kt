package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.DefaultRuntimeKnowledgeLifecycleHistoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.DefaultRuntimeKnowledgeProvenanceQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.integrity.DefaultRuntimeKnowledgeProvenanceIntegrityQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.DefaultRuntimeKnowledgeSupersessionHistory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity.DefaultRuntimeKnowledgeSupersessionIntegrityChecker
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query.DefaultRuntimeKnowledgeSupersessionQuery

class DefaultRuntimeKnowledgeProvenanceIntegrityQueryContractTest {

    @Test
    fun clean_selected_lineage_is_valid() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first =
            knowledge("first", 1L)

        val second =
            knowledge("second", 2L)

        history.record(first, second)

        val result =
            integrityQuery(history)
                .check(second)

        assertTrue(result.valid)
        assertEquals(0, result.issueCount)
    }

    @Test
    fun corruption_inside_selected_lineage_is_reported() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first =
            knowledge("first", 1L)

        val second =
            knowledge("second", 2L)

        history.record(first, second)
        history.record(first, second)

        val result =
            integrityQuery(history)
                .check(second)

        assertFalse(result.valid)
        assertTrue(result.issueCount > 0)
    }

    @Test
    fun unrelated_corrupt_lineage_does_not_taint_selected_provenance() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val selectedFirst =
            knowledge("selected first", 1L)

        val selectedCurrent =
            knowledge("selected current", 2L)

        history.record(
            selectedFirst,
            selectedCurrent
        )

        val unrelated =
            knowledge("unrelated", 3L)

        history.record(
            unrelated,
            unrelated
        )

        val result =
            integrityQuery(history)
                .check(selectedCurrent)

        assertTrue(
            result.valid,
            "unrelated lineage corruption must not invalidate selected provenance"
        )

        assertEquals(
            0,
            result.issueCount
        )
    }

    private fun integrityQuery(
        history:
            DefaultRuntimeKnowledgeSupersessionHistory
    ): DefaultRuntimeKnowledgeProvenanceIntegrityQuery {

        val supersessionQuery =
            DefaultRuntimeKnowledgeSupersessionQuery(
                history
            )

        val provenanceQuery =
            DefaultRuntimeKnowledgeProvenanceQuery(
                supersessionQuery =
                    supersessionQuery,
                lifecycleHistoryQuery =
                    DefaultRuntimeKnowledgeLifecycleHistoryQuery(
                        DefaultRuntimeKnowledgeLifecycleHistoryStore()
                    )
            )

        return DefaultRuntimeKnowledgeProvenanceIntegrityQuery(
            provenanceQuery =
                provenanceQuery,
            integrityChecker =
                DefaultRuntimeKnowledgeSupersessionIntegrityChecker(
                    history
                )
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
