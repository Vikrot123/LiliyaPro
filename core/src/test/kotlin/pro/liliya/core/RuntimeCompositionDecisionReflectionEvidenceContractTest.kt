package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class RuntimeCompositionDecisionReflectionEvidenceContractTest {

    @Test
    fun composition_owns_stable_decision_reflection_analyzer() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition
                .decisionReflectionAnalyzer(),
            composition
                .decisionReflectionAnalyzer()
        )
    }

    @Test
    fun provenance_aware_explanation_can_be_reflected_without_mutating_decision() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val weak =
            knowledge(
                "runtime post decision reflection knowledge",
                0.60,
                RuntimeKnowledgeSource.EXPERIENCE,
                1L
            )

        val current =
            knowledge(
                "Runtime post decision reflection knowledge.",
                0.95,
                RuntimeKnowledgeSource.CONSOLIDATION,
                2L
            )

        lifecycle.create(weak)
        lifecycle.create(current)

        val decision =
            RuntimeDecision(
                command =
                    RuntimeCommand.HEALTH_CHECK,
                reason =
                    "post-decision reflection verification",
                confidence =
                    0.95,
                knowledgeSelection =
                    RuntimeKnowledgeSelectionResult(
                        knowledge = current,
                        relevantPoolUsed = true,
                        reason =
                            "selected current knowledge",
                        selectionReason =
                            RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
                        relevanceScore = 1.0
                    )
            )

        val explanation =
            composition
                .decisionExplainer()
                .explain(decision)

        val insight =
            composition
                .decisionReflectionAnalyzer()
                .analyze(explanation)

        assertTrue(
            insight.trustworthyKnowledgeBasis
        )

        assertEquals(
            2,
            insight.evidence.provenanceDepth
        )

        assertEquals(
            decision.command,
            insight.evidence.command
        )

        assertEquals(
            decision.reason,
            insight.evidence.decisionReason
        )

        assertEquals(
            decision.confidence,
            insight.evidence.confidence
        )

        assertNotNull(
            explanation.knowledgeProvenance
        )

        assertNotNull(
            explanation.knowledgeProvenanceIntegrity
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
