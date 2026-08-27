package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityAdvisoryContractTest {

    @Test
    fun composition_owns_stable_advisory_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityAdvisoryQuery(),
            composition.decisionQualityAdvisoryQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_advisory_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityAdvisoryQuery(),
            second.decisionQualityAdvisoryQuery()
        )
    }

    @Test
    fun empty_quality_state_produces_observe_advisory() {
        val composition =
            DefaultRuntimeComposition()

        val advisory =
            composition
                .decisionQualityAdvisoryQuery()
                .currentAdvisory()

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.OBSERVE,
            advisory.level
        )
    }

    @Test
    fun attention_quality_produces_review_advisory() {
        val composition =
            DefaultRuntimeComposition()

        repeat(2) {
            composition
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    attentionExplanation()
                )

            composition
                .decisionQualityRecorder()
                .recordCurrent()
        }

        val advisory =
            composition
                .decisionQualityAdvisoryQuery()
                .currentAdvisory()

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            advisory.level
        )
    }

    @Test
    fun prepare_runtime_resets_advisory_through_existing_read_models() {
        val composition =
            DefaultRuntimeComposition()

        val query =
            composition
                .decisionQualityAdvisoryQuery()

        repeat(2) {
            composition
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    attentionExplanation()
                )

            composition
                .decisionQualityRecorder()
                .recordCurrent()
        }

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            query.currentAdvisory().level
        )

        composition.prepareRuntime()

        assertSame(
            query,
            composition
                .decisionQualityAdvisoryQuery()
        )

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.OBSERVE,
            query.currentAdvisory().level
        )
    }

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "decision quality advisory attention",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
