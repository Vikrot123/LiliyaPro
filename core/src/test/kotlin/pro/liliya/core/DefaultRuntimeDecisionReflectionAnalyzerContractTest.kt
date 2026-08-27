package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.reflection.DefaultRuntimeDecisionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.RuntimeKnowledgeProvenance
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.RuntimeKnowledgeProvenanceStep
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance.integrity.RuntimeKnowledgeProvenanceIntegrity
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class DefaultRuntimeDecisionReflectionAnalyzerContractTest {

    private val analyzer =
        DefaultRuntimeDecisionReflectionAnalyzer()

    @Test
    fun decision_without_knowledge_remains_neutral_reflection_evidence() {
        val insight =
            analyzer.analyze(
                RuntimeDecisionExplanation(
                    command = null,
                    decisionReason =
                        "no action required",
                    confidence = 0.95,
                    knowledgeStatement = null,
                    knowledgeSelectionReason =
                        RuntimeKnowledgeSelectionReason.EMPTY,
                    knowledgeRelevanceScore = 0.0
                )
            )

        assertFalse(
            insight.evidence.knowledgeUsed
        )

        assertFalse(
            insight.evidence.provenanceAvailable
        )

        assertNull(
            insight.evidence.provenanceValid
        )

        assertEquals(
            0,
            insight.evidence.provenanceDepth
        )

        assertFalse(
            insight.trustworthyKnowledgeBasis
        )

        assertFalse(
            insight.requiresAttention
        )
    }

    @Test
    fun valid_provenance_becomes_trustworthy_reflection_evidence() {
        val knowledge =
            knowledge()

        val provenance =
            RuntimeKnowledgeProvenance(
                startingKnowledge = knowledge,
                currentKnowledge = knowledge,
                chain = listOf(knowledge),
                steps =
                    listOf(
                        RuntimeKnowledgeProvenanceStep(
                            knowledge = knowledge,
                            source = knowledge.source,
                            lifecycleHistory =
                                emptyList()
                        )
                    ),
                cycleDetected = false
            )

        val insight =
            analyzer.analyze(
                RuntimeDecisionExplanation(
                    command = null,
                    decisionReason =
                        "knowledge-backed decision",
                    confidence = 0.90,
                    knowledgeStatement =
                        knowledge.statement,
                    knowledgeSelectionReason =
                        RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
                    knowledgeRelevanceScore = 1.0,
                    knowledgeProvenance =
                        provenance,
                    knowledgeProvenanceIntegrity =
                        RuntimeKnowledgeProvenanceIntegrity(
                            issues = emptyList()
                        )
                )
            )

        assertTrue(
            insight.evidence.knowledgeUsed
        )

        assertTrue(
            insight.evidence.provenanceAvailable
        )

        assertEquals(
            true,
            insight.evidence.provenanceValid
        )

        assertEquals(
            1,
            insight.evidence.provenanceDepth
        )

        assertTrue(
            insight.trustworthyKnowledgeBasis
        )

        assertFalse(
            insight.requiresAttention
        )
    }

    @Test
    fun knowledge_without_provenance_requires_attention() {
        val insight =
            analyzer.analyze(
                RuntimeDecisionExplanation(
                    command = null,
                    decisionReason =
                        "knowledge diagnostics",
                    confidence = 0.70,
                    knowledgeStatement =
                        "runtime knowledge",
                    knowledgeSelectionReason =
                        RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
                    knowledgeRelevanceScore = 0.8
                )
            )

        assertTrue(
            insight.evidence.knowledgeUsed
        )

        assertFalse(
            insight.evidence.provenanceAvailable
        )

        assertTrue(
            insight.requiresAttention
        )

        assertFalse(
            insight.trustworthyKnowledgeBasis
        )
    }

    private fun knowledge() =
        RuntimeKnowledge(
            statement =
                "runtime reflection knowledge",
            confidence = 0.90,
            source =
                RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
}
