package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class RuntimeCompositionDecisionProvenanceIntegrityContractTest {

    @Test
    fun explanation_marks_real_selected_provenance_as_valid() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val first =
            knowledge(
                "runtime decision integrity knowledge",
                0.60,
                1L
            )

        val current =
            knowledge(
                "Runtime decision integrity knowledge.",
                0.95,
                2L
            )

        lifecycle.create(first)
        lifecycle.create(current)

        val explanation =
            composition
                .decisionExplainer()
                .explain(
                    decisionFor(current)
                )

        val integrity =
            assertNotNull(
                explanation
                    .knowledgeProvenanceIntegrity
            )

        assertTrue(integrity.valid)
        assertEquals(0, integrity.issueCount)

        assertEquals(
            listOf(
                first,
                current
            ),
            assertNotNull(
                explanation.knowledgeProvenance
            ).chain
        )
    }

    @Test
    fun decision_without_selected_knowledge_has_no_integrity_snapshot() {
        val composition =
            DefaultRuntimeComposition()

        val decision =
            RuntimeDecision(
                command = null,
                reason =
                    "Runtime stable",
                confidence = 0.95,
                knowledgeSelection =
                    RuntimeKnowledgeSelectionResult(
                        knowledge = null,
                        relevantPoolUsed = false,
                        reason =
                            "nothing selected",
                        selectionReason =
                            RuntimeKnowledgeSelectionReason.EMPTY,
                        relevanceScore = 0.0
                    )
            )

        val explanation =
            composition
                .decisionExplainer()
                .explain(decision)

        assertNull(
            explanation
                .knowledgeProvenanceIntegrity
        )
    }

    private fun decisionFor(
        knowledge: RuntimeKnowledge
    ) =
        RuntimeDecision(
            command =
                RuntimeCommand.HEALTH_CHECK,
            reason =
                "integrity-aware diagnostic verification",
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
