package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.priority.DefaultRuntimeKnowledgePrioritizer
import pro.liliya.core.runtime.intelligence.knowledge.priority.RuntimeKnowledgePriorityLevel

class DefaultRuntimeKnowledgePrioritizerContractTest {

    private fun knowledge(
        confidence: Double
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "runtime knowledge",
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun high_confidence_knowledge_gets_critical_priority() {

        val result =
            DefaultRuntimeKnowledgePrioritizer()
                .prioritize(
                    knowledge(0.95)
                )

        assertEquals(
            RuntimeKnowledgePriorityLevel.CRITICAL,
            result.level
        )
    }

    @Test
    fun medium_confidence_knowledge_gets_medium_priority() {

        val result =
            DefaultRuntimeKnowledgePrioritizer()
                .prioritize(
                    knowledge(0.6)
                )

        assertEquals(
            RuntimeKnowledgePriorityLevel.MEDIUM,
            result.level
        )
    }

    @Test
    fun low_confidence_knowledge_gets_low_priority() {

        val result =
            DefaultRuntimeKnowledgePrioritizer()
                .prioritize(
                    knowledge(0.2)
                )

        assertEquals(
            RuntimeKnowledgePriorityLevel.LOW,
            result.level
        )
    }
}
