package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.DefaultRuntimeKnowledgeSupersessionHistory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity.DefaultRuntimeKnowledgeSupersessionIntegrityChecker
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity.RuntimeKnowledgeSupersessionIntegrityIssue

class DefaultRuntimeKnowledgeSupersessionIntegrityCheckerContractTest {

    @Test
    fun linear_lineage_is_valid() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first = knowledge("first", 1L)
        val second = knowledge("second", 2L)
        val third = knowledge("third", 3L)

        history.record(first, second)
        history.record(second, third)

        val report =
            DefaultRuntimeKnowledgeSupersessionIntegrityChecker(
                history
            ).check()

        assertTrue(report.valid)
        assertEquals(0, report.issueCount)
    }

    @Test
    fun self_supersession_is_detected() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val knowledge =
            knowledge("self", 1L)

        history.record(
            knowledge,
            knowledge
        )

        val report =
            DefaultRuntimeKnowledgeSupersessionIntegrityChecker(
                history
            ).check()

        assertFalse(report.valid)

        assertTrue(
            report.issues.any {
                it is
                    RuntimeKnowledgeSupersessionIntegrityIssue
                        .SelfSupersession
            }
        )
    }

    @Test
    fun duplicate_edge_is_detected_once() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first = knowledge("first", 1L)
        val second = knowledge("second", 2L)

        history.record(first, second)
        history.record(first, second)

        val report =
            DefaultRuntimeKnowledgeSupersessionIntegrityChecker(
                history
            ).check()

        assertEquals(
            1,
            report.issues.count {
                it is
                    RuntimeKnowledgeSupersessionIntegrityIssue
                        .DuplicateEdge
            }
        )
    }

    @Test
    fun branching_lineage_is_detected() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first = knowledge("first", 1L)
        val second = knowledge("second", 2L)
        val third = knowledge("third", 3L)

        history.record(first, second)
        history.record(first, third)

        val report =
            DefaultRuntimeKnowledgeSupersessionIntegrityChecker(
                history
            ).check()

        val branching =
            report.issues
                .filterIsInstance<
                    RuntimeKnowledgeSupersessionIntegrityIssue
                        .Branching
                >()
                .single()

        assertEquals(
            first,
            branching.previousKnowledge
        )

        assertEquals(
            setOf(second, third),
            branching.replacements.toSet()
        )
    }

    @Test
    fun cycle_is_detected_without_hanging() {
        val history =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val first = knowledge("first", 1L)
        val second = knowledge("second", 2L)
        val third = knowledge("third", 3L)

        history.record(first, second)
        history.record(second, third)
        history.record(third, first)

        val report =
            DefaultRuntimeKnowledgeSupersessionIntegrityChecker(
                history
            ).check()

        assertFalse(report.valid)

        assertEquals(
            1,
            report.issues.count {
                it is
                    RuntimeKnowledgeSupersessionIntegrityIssue
                        .Cycle
            }
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
