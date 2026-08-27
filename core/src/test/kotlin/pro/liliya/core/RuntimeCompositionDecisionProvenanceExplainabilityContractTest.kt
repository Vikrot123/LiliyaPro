package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class RuntimeCompositionDecisionProvenanceExplainabilityContractTest {

    @Test
    fun decision_explanation_exposes_full_provenance_of_current_knowledge() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val first =
            knowledge(
                "runtime decision provenance knowledge",
                0.60,
                RuntimeKnowledgeSource.EXPERIENCE,
                1L
            )

        val second =
            knowledge(
                "Runtime decision provenance knowledge.",
                0.80,
                RuntimeKnowledgeSource.REFLECTION,
                2L
            )

        val current =
            knowledge(
                "RUNTIME decision provenance knowledge!",
                0.95,
                RuntimeKnowledgeSource.CONSOLIDATION,
                3L
            )

        lifecycle.create(first)
        lifecycle.create(second)
        lifecycle.create(current)

        val decision =
            RuntimeDecision(
                command =
                    RuntimeCommand.HEALTH_CHECK,
                reason =
                    "diagnostic verification",
                confidence =
                    0.95,
                knowledgeSelection =
                    RuntimeKnowledgeSelectionResult(
                        knowledge = current,
                        relevantPoolUsed = true,
                        reason =
                            "selected current knowledge",
                        selectionReason =
                            RuntimeKnowledgeSelectionReason
                                .RELEVANT_POOL,
                        relevanceScore = 1.0
                    )
            )

        val explanation =
            composition
                .decisionExplainer()
                .explain(decision)

        val provenance =
            assertNotNull(
                explanation.knowledgeProvenance
            )

        assertEquals(
            listOf(
                first,
                second,
                current
            ),
            provenance.chain
        )

        assertEquals(
            current,
            provenance.currentKnowledge
        )

        assertEquals(
            listOf(
                RuntimeKnowledgeSource.EXPERIENCE,
                RuntimeKnowledgeSource.REFLECTION,
                RuntimeKnowledgeSource.CONSOLIDATION
            ),
            provenance.steps.map {
                it.source
            }
        )

        assertEquals(
            current.statement,
            explanation.knowledgeStatement
        )
    }

    @Test
    fun decision_without_selected_knowledge_has_no_provenance() {
        val composition =
            DefaultRuntimeComposition()

        val decision =
            RuntimeDecision(
                command = null,
                reason =
                    "Runtime is stable; no action required",
                confidence = 0.95,
                knowledgeSelection =
                    RuntimeKnowledgeSelectionResult(
                        knowledge = null,
                        relevantPoolUsed = false,
                        reason =
                            "no knowledge selected",
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
            explanation.knowledgeProvenance
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
