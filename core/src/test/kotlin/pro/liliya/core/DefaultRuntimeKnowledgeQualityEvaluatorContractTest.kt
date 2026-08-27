package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.priority.RuntimeKnowledgePriorityLevel
import pro.liliya.core.runtime.intelligence.knowledge.quality.DefaultRuntimeKnowledgeQualityEvaluator

class DefaultRuntimeKnowledgeQualityEvaluatorContractTest {

    private val evaluator =
        DefaultRuntimeKnowledgeQualityEvaluator()

    @Test
    fun high_confidence_knowledge_is_accepted_and_high_priority() {
        val result =
            evaluator.evaluate(
                knowledge(0.80)
            )

        assertTrue(
            result.validation.accepted
        )

        assertEquals(
            RuntimeKnowledgePriorityLevel.HIGH,
            result.priority.level
        )
    }

    @Test
    fun critical_confidence_is_preserved_as_critical_quality() {
        val result =
            evaluator.evaluate(
                knowledge(0.95)
            )

        assertTrue(
            result.validation.accepted
        )

        assertEquals(
            RuntimeKnowledgePriorityLevel.CRITICAL,
            result.priority.level
        )
    }

    @Test
    fun low_confidence_knowledge_is_rejected() {
        val result =
            evaluator.evaluate(
                knowledge(0.30)
            )

        assertFalse(
            result.validation.accepted
        )

        assertEquals(
            RuntimeKnowledgePriorityLevel.LOW,
            result.priority.level
        )
    }

    private fun knowledge(
        confidence: Double
    ) =
        RuntimeKnowledge(
            statement = "quality knowledge",
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
}
