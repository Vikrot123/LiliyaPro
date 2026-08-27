package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.explanation.DefaultRuntimeDecisionExplainer
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class DefaultRuntimeDecisionExplainerContractTest {

    private val explainer =
        DefaultRuntimeDecisionExplainer()

    @Test
    fun decision_without_knowledge_exposes_decision_explanation() {
        val decision =
            RuntimeDecision(
                command = RuntimeCommand.HEALTH_CHECK,
                reason = "health verification required",
                confidence = 0.75
            )

        val result =
            explainer.explain(decision)

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            result.command
        )

        assertEquals(
            "health verification required",
            result.decisionReason
        )

        assertEquals(
            0.75,
            result.confidence
        )

        assertNull(
            result.knowledgeStatement
        )

        assertNull(
            result.knowledgeSelectionReason
        )

        assertEquals(
            0.0,
            result.knowledgeRelevanceScore
        )
    }

    @Test
    fun selected_knowledge_diagnostics_are_exposed_in_explanation() {
        val knowledge =
            RuntimeKnowledge(
                statement =
                    "runtime operational state remained stable",
                confidence = 0.90,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        val selection =
            RuntimeKnowledgeSelectionResult(
                knowledge = knowledge,
                relevantPoolUsed = true,
                reason = "selected from relevant pool",
                selectionReason =
                    RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
                relevanceScore = 0.75
            )

        val decision =
            RuntimeDecision(
                command = null,
                reason = "Runtime is stable; no action required",
                confidence = 0.95,
                knowledgeSelection = selection
            )

        val result =
            explainer.explain(decision)

        assertNull(
            result.command
        )

        assertEquals(
            decision.reason,
            result.decisionReason
        )

        assertEquals(
            decision.confidence,
            result.confidence
        )

        assertEquals(
            knowledge.statement,
            result.knowledgeStatement
        )

        assertEquals(
            RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
            result.knowledgeSelectionReason
        )

        assertEquals(
            0.75,
            result.knowledgeRelevanceScore
        )
    }

    @Test
    fun explanation_must_not_change_decision_policy_data() {
        val decision =
            RuntimeDecision(
                command = RuntimeCommand.RECOVER,
                reason = "Runtime instability requires recovery",
                confidence = 0.85
            )

        val result =
            explainer.explain(decision)

        assertEquals(
            decision.command,
            result.command
        )

        assertEquals(
            decision.reason,
            result.decisionReason
        )

        assertEquals(
            decision.confidence,
            result.confidence
        )
    }
}
