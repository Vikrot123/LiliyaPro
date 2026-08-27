package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class RuntimeDecisionProvenanceIntegritySnapshotContractTest {

    @Test
    fun recorded_integrity_result_remains_historical_snapshot() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycleComposition =
            composition
                .knowledgeLifecycleComposition()

        val lifecycle =
            lifecycleComposition
                .lifecycleMemory()

        val first =
            knowledge(
                "runtime integrity snapshot knowledge",
                0.60,
                1L
            )

        val current =
            knowledge(
                "Runtime integrity snapshot knowledge.",
                0.95,
                2L
            )

        lifecycle.create(first)
        lifecycle.create(current)

        val record =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(
                    decisionFor(current)
                )

        val snapshot =
            assertNotNull(
                record
                    .explanation
                    .knowledgeProvenanceIntegrity
            )

        assertTrue(snapshot.valid)
        assertEquals(0, snapshot.issueCount)

        lifecycleComposition.reset()

        assertTrue(snapshot.valid)
        assertEquals(
            0,
            snapshot.issueCount,
            "recorded integrity result must remain a historical snapshot"
        )
    }

    private fun decisionFor(
        knowledge: RuntimeKnowledge
    ) =
        RuntimeDecision(
            command =
                RuntimeCommand.HEALTH_CHECK,
            reason =
                "snapshot integrity verification",
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
