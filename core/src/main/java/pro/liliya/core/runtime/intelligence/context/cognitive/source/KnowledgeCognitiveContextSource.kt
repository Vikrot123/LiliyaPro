package pro.liliya.core.runtime.intelligence.context.cognitive.source

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory

class KnowledgeCognitiveContextSource(
    private val knowledgeMemory: RuntimeKnowledgeMemory
) : CognitiveContextSource {

    override fun snapshot(
        type: CognitiveContextType
    ): CognitiveContextSnapshot {
        return CognitiveContextSnapshot(
            type = type,
            values = mapOf(
                AVAILABLE_KNOWLEDGE_KEY to
                    knowledgeMemory.availableKnowledge()
            )
        )
    }

    companion object {
        const val AVAILABLE_KNOWLEDGE_KEY =
            "availableKnowledge"
    }
}
