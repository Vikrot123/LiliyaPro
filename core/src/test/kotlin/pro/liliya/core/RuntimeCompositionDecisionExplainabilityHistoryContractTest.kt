package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionDecisionExplainabilityHistoryContractTest {

    @Test
    fun composition_owns_stable_history_and_recorder() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionExplanationHistory(),
            composition.decisionExplanationHistory()
        )

        assertSame(
            composition.decisionExplanationRecorder(),
            composition.decisionExplanationRecorder()
        )
    }

    @Test
    fun real_decisions_can_be_explained_and_recorded_across_cycles() {
        val composition =
            DefaultRuntimeComposition()

        val firstIntelligence =
            composition
                .intelligenceOrchestrator()
                .process()

        val firstDecision =
            composition
                .decisionEngine()
                .decide(firstIntelligence)

        val firstRecord =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(firstDecision)

        val secondIntelligence =
            composition
                .intelligenceOrchestrator()
                .process()

        val secondDecision =
            composition
                .decisionEngine()
                .decide(secondIntelligence)

        val secondRecord =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(secondDecision)

        val records =
            composition
                .decisionExplanationHistory()
                .records()

        assertEquals(
            2,
            records.size
        )

        assertEquals(
            firstRecord,
            records[0]
        )

        assertEquals(
            secondRecord,
            records[1]
        )

        val secondSelection =
            assertNotNull(
                secondDecision.knowledgeSelection
            )

        assertEquals(
            secondDecision.reason,
            secondRecord.explanation.decisionReason
        )

        assertEquals(
            secondDecision.confidence,
            secondRecord.explanation.confidence
        )

        assertEquals(
            secondSelection.knowledge?.statement,
            secondRecord.explanation.knowledgeStatement
        )

        assertEquals(
            secondSelection.selectionReason,
            secondRecord.explanation.knowledgeSelectionReason
        )

        assertEquals(
            secondSelection.relevanceScore,
            secondRecord.explanation.knowledgeRelevanceScore
        )
    }
}
