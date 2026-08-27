package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.DefaultRuntimeKnowledgeLifecycleHistoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.DefaultRuntimeKnowledgeProvenanceQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.DefaultRuntimeKnowledgeSupersessionHistory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query.DefaultRuntimeKnowledgeSupersessionQuery

class DefaultRuntimeKnowledgeProvenanceQueryContractTest {

    @Test
    fun standalone_knowledge_preserves_primary_source() {
        val supersessionHistory =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val lifecycleHistory =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val query =
            DefaultRuntimeKnowledgeProvenanceQuery(
                supersessionQuery =
                    DefaultRuntimeKnowledgeSupersessionQuery(
                        supersessionHistory
                    ),
                lifecycleHistoryQuery =
                    DefaultRuntimeKnowledgeLifecycleHistoryQuery(
                        lifecycleHistory
                    )
            )

        val knowledge =
            knowledge(
                "standalone provenance knowledge",
                0.90,
                RuntimeKnowledgeSource.REFLECTION,
                1L
            )

        val result =
            query.provenance(
                knowledge
            )

        assertEquals(
            knowledge,
            result.startingKnowledge
        )

        assertEquals(
            knowledge,
            result.currentKnowledge
        )

        assertEquals(
            listOf(knowledge),
            result.chain
        )

        assertEquals(
            RuntimeKnowledgeSource.REFLECTION,
            result.steps.single().source
        )

        assertEquals(
            emptyList(),
            result.steps.single().lifecycleHistory
        )

        assertFalse(
            result.cycleDetected
        )
    }

    @Test
    fun multi_version_provenance_preserves_source_of_every_version() {
        val supersessionHistory =
            DefaultRuntimeKnowledgeSupersessionHistory()

        val lifecycleHistory =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val first =
            knowledge(
                "first",
                0.60,
                RuntimeKnowledgeSource.EXPERIENCE,
                1L
            )

        val second =
            knowledge(
                "second",
                0.80,
                RuntimeKnowledgeSource.REFLECTION,
                2L
            )

        val third =
            knowledge(
                "third",
                0.95,
                RuntimeKnowledgeSource.CONSOLIDATION,
                3L
            )

        supersessionHistory.record(
            first,
            second
        )

        supersessionHistory.record(
            second,
            third
        )

        val result =
            DefaultRuntimeKnowledgeProvenanceQuery(
                supersessionQuery =
                    DefaultRuntimeKnowledgeSupersessionQuery(
                        supersessionHistory
                    ),
                lifecycleHistoryQuery =
                    DefaultRuntimeKnowledgeLifecycleHistoryQuery(
                        lifecycleHistory
                    )
            ).provenance(
                first
            )

        assertEquals(
            listOf(
                first,
                second,
                third
            ),
            result.chain
        )

        assertEquals(
            listOf(
                RuntimeKnowledgeSource.EXPERIENCE,
                RuntimeKnowledgeSource.REFLECTION,
                RuntimeKnowledgeSource.CONSOLIDATION
            ),
            result.steps.map {
                it.source
            }
        )

        assertEquals(
            third,
            result.currentKnowledge
        )
    }

    private fun knowledge(
        statement: String,
        confidence: Double,
        source: RuntimeKnowledgeSource,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = source,
            createdAt = createdAt
        )
}
