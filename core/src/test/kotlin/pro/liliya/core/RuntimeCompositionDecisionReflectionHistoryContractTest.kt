package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class RuntimeCompositionDecisionReflectionHistoryContractTest {

    @Test
    fun composition_owns_stable_history_and_recorder() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition
                .decisionReflectionHistory(),
            composition
                .decisionReflectionHistory()
        )

        assertSame(
            composition
                .decisionReflectionRecorder(),
            composition
                .decisionReflectionRecorder()
        )
    }

    @Test
    fun separate_compositions_own_independent_histories() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionReflectionHistory(),
            second.decisionReflectionHistory()
        )
    }

    @Test
    fun provenance_aware_decision_reflection_can_be_recorded() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val weak =
            knowledge(
                "runtime historical reflection knowledge",
                0.60,
                1L
            )

        val strong =
            knowledge(
                "Runtime historical reflection knowledge.",
                0.95,
                2L
            )

        lifecycle.create(weak)
        lifecycle.create(strong)

        val decision =
            decisionFor(
                strong
            )

        val explanation =
            composition
                .decisionExplainer()
                .explain(decision)

        val record =
            composition
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    explanation
                )

        assertEquals(
            1,
            composition
                .decisionReflectionHistory()
                .records()
                .size
        )

        assertEquals(
            record,
            composition
                .decisionReflectionHistory()
                .records()
                .single()
        )

        assertTrue(
            record
                .insight
                .trustworthyKnowledgeBasis
        )

        assertEquals(
            2,
            record
                .insight
                .evidence
                .provenanceDepth
        )
    }

    @Test
    fun prepare_runtime_clears_decision_reflection_history() {
        val composition =
            DefaultRuntimeComposition()

        val explanation =
            composition
                .decisionExplainer()
                .explain(
                    RuntimeDecision(
                        command = null,
                        reason =
                            "prepare reflection reset",
                        confidence = 0.90
                    )
                )

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                explanation
            )

        assertEquals(
            1,
            composition
                .decisionReflectionHistory()
                .records()
                .size
        )

        composition.prepareRuntime()

        assertTrue(
            composition
                .decisionReflectionHistory()
                .records()
                .isEmpty()
        )
    }

    @Test
    fun prepare_runtime_preserves_history_and_recorder_owners() {
        val composition =
            DefaultRuntimeComposition()

        val history =
            composition
                .decisionReflectionHistory()

        val recorder =
            composition
                .decisionReflectionRecorder()

        composition.prepareRuntime()

        assertSame(
            history,
            composition
                .decisionReflectionHistory()
        )

        assertSame(
            recorder,
            composition
                .decisionReflectionRecorder()
        )
    }

    @Test
    fun previously_created_reflection_record_remains_historical_snapshot_after_prepare() {
        val composition =
            DefaultRuntimeComposition()

        val explanation =
            composition
                .decisionExplainer()
                .explain(
                    RuntimeDecision(
                        command = null,
                        reason =
                            "historical decision reflection",
                        confidence = 0.90
                    )
                )

        val record =
            composition
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    explanation
                )

        val snapshot =
            record.insight

        composition.prepareRuntime()

        assertEquals(
            "historical decision reflection",
            snapshot.evidence.decisionReason
        )

        assertEquals(
            0.90,
            snapshot.evidence.confidence
        )

        assertTrue(
            composition
                .decisionReflectionHistory()
                .records()
                .isEmpty()
        )
    }

    private fun decisionFor(
        knowledge: RuntimeKnowledge
    ) =
        RuntimeDecision(
            command =
                RuntimeCommand.HEALTH_CHECK,
            reason =
                "decision reflection history verification",
            confidence =
                knowledge.confidence,
            knowledgeSelection =
                RuntimeKnowledgeSelectionResult(
                    knowledge = knowledge,
                    relevantPoolUsed = true,
                    reason =
                        "selected current knowledge",
                    selectionReason =
                        RuntimeKnowledgeSelectionReason
                            .RELEVANT_POOL,
                    relevanceScore = 1.0
                )
        )

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
