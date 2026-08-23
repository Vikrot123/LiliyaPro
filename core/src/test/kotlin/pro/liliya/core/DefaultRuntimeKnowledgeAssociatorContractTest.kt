package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.association.DefaultRuntimeKnowledgeAssociator
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType

class DefaultRuntimeKnowledgeAssociatorContractTest {

    private fun knowledge(
        statement: String
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = statement,
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun association_keeps_source_target_and_type() {

        val source = knowledge("runtime state")
        val target = knowledge("runtime recovery")

        val association =
            DefaultRuntimeKnowledgeAssociator()
                .associate(
                    source = source,
                    target = target,
                    type = RuntimeKnowledgeAssociationType.RELATED
                )

        assertEquals(
            source,
            association.source
        )

        assertEquals(
            target,
            association.target
        )

        assertEquals(
            RuntimeKnowledgeAssociationType.RELATED,
            association.type
        )
    }
}
