package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.governance.DefaultRuntimeKnowledgeValidator

class DefaultRuntimeKnowledgeValidatorContractTest {

    private fun knowledge(
        confidence: Double
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "runtime pattern",
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun high_confidence_knowledge_is_accepted() {

        val result =
            DefaultRuntimeKnowledgeValidator()
                .validate(
                    knowledge(0.8)
                )

        assertTrue(
            result.accepted
        )
    }

    @Test
    fun low_confidence_knowledge_is_rejected() {

        val result =
            DefaultRuntimeKnowledgeValidator()
                .validate(
                    knowledge(0.2)
                )

        assertFalse(
            result.accepted
        )
    }
}
