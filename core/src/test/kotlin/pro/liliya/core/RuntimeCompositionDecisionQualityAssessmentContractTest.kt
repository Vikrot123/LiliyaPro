package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityAssessmentContractTest {

    @Test
    fun empty_reflection_history_has_no_current_quality_assessment() {
        val composition =
            DefaultRuntimeComposition()

        assertNull(
            composition
                .decisionQualityQuery()
                .currentAssessment()
        )
    }

    @Test
    fun composition_owns_stable_quality_assessor_and_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityAssessor(),
            composition.decisionQualityAssessor()
        )

        assertSame(
            composition.decisionQualityQuery(),
            composition.decisionQualityQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_quality_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityQuery(),
            second.decisionQualityQuery()
        )
    }

    @Test
    fun current_assessment_is_derived_from_latest_reflection_and_current_trend() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val knowledge =
            RuntimeKnowledge(
                statement =
                    "runtime trustworthy decision quality knowledge",
                confidence = 0.90,
                source =
                    RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 1L
            )

        lifecycle.create(
            knowledge
        )

        val explanation =
            composition
                .decisionExplainer()
                .explain(
                    RuntimeDecision(
                        command = null,
                        reason =
                            "trustworthy decision quality verification",
                        confidence = 0.90,
                        knowledgeSelection =
                            RuntimeKnowledgeSelectionResult(
                                knowledge = knowledge,
                                relevantPoolUsed = true,
                                reason =
                                    "selected lifecycle knowledge",
                                selectionReason =
                                    RuntimeKnowledgeSelectionReason
                                        .RELEVANT_POOL,
                                relevanceScore = 1.0
                            )
                    )
                )

        repeat(3) {
            composition
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    explanation
                )
        }

        val assessment =
            composition
                .decisionQualityQuery()
                .currentAssessment()
                ?: error(
                    "quality assessment expected"
                )

        assertEquals(
            RuntimeDecisionQualityState.TRUSTWORTHY,
            assessment.state
        )

        assertEquals(
            3,
            assessment.trend.sampleCount
        )

        assertTrue(
            assessment.trustworthyKnowledgeBasis
        )
    }

    @Test
    fun oscillating_reflection_history_is_reported_as_unstable() {
        val composition =
            DefaultRuntimeComposition()

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                attentionExplanation(
                    0.60
                )
            )

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                neutralExplanation(
                    0.90
                )
            )

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                attentionExplanation(
                    0.60
                )
            )

        val assessment =
            composition
                .decisionQualityQuery()
                .currentAssessment()
                ?: error(
                    "quality assessment expected"
                )

        /*
         * Current attention is stronger than temporal
         * instability and therefore remains authoritative.
         */
        assertEquals(
            RuntimeDecisionQualityState.REQUIRES_ATTENTION,
            assessment.state
        )

        assertTrue(
            assessment.trend.oscillating
        )
    }

    @Test
    fun prepare_runtime_clears_quality_read_model_without_replacing_owner() {
        val composition =
            DefaultRuntimeComposition()

        val assessor =
            composition
                .decisionQualityAssessor()

        val query =
            composition
                .decisionQualityQuery()

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                trustworthyExplanation(
                    0.90
                )
            )

        assertTrue(
            query.currentAssessment() != null
        )

        composition.prepareRuntime()

        assertSame(
            assessor,
            composition.decisionQualityAssessor()
        )

        assertSame(
            query,
            composition.decisionQualityQuery()
        )

        assertNull(
            query.currentAssessment()
        )
    }

    private fun trustworthyExplanation(
        confidence: Double
    ) =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "trustworthy decision",
            confidence = confidence,
            knowledgeStatement =
                "trusted knowledge",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0,
            knowledgeProvenance =
                null,
            knowledgeProvenanceIntegrity =
                null
        )

    /*
     * The existing analyzer treats knowledge without
     * provenance as attention-required.
     */
    private fun attentionExplanation(
        confidence: Double
    ) =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "attention decision",
            confidence = confidence,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )

    /*
     * No selected knowledge remains neutral in the
     * current reflection analyzer.
     */
    private fun neutralExplanation(
        confidence: Double
    ) =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "neutral decision",
            confidence = confidence,
            knowledgeStatement = null,
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason.EMPTY,
            knowledgeRelevanceScore = 0.0
        )
}
