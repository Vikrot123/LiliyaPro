package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.revision.DefaultRuntimeKnowledgeRevisionManager
import pro.liliya.core.runtime.intelligence.knowledge.revision.RuntimeKnowledgeRevisionReason

class DefaultRuntimeKnowledgeRevisionManagerContractTest {

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
    fun revision_keeps_previous_and_updated_knowledge() {

        val previous = knowledge("old knowledge")
        val updated = knowledge("new knowledge")

        val revision =
            DefaultRuntimeKnowledgeRevisionManager()
                .revise(
                    previous = previous,
                    updated = updated,
                    reason = RuntimeKnowledgeRevisionReason.CORRECTION
                )

        assertEquals(
            previous,
            revision.previous
        )

        assertEquals(
            updated,
            revision.updated
        )

        assertEquals(
            RuntimeKnowledgeRevisionReason.CORRECTION,
            revision.reason
        )
    }
}
