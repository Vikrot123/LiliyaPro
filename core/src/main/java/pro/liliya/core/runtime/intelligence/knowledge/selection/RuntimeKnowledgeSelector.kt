package pro.liliya.core.runtime.intelligence.knowledge.selection

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeSelector {

    fun select(
        knowledge: List<RuntimeKnowledge>,
        interpretation: String
    ): RuntimeKnowledge?

    fun selectResult(
        knowledge: List<RuntimeKnowledge>,
        interpretation: String
    ): RuntimeKnowledgeSelectionResult {
        return RuntimeKnowledgeSelectionResult(
            knowledge = select(
                knowledge,
                interpretation
            ),
            relevantPoolUsed = false,
            reason = "Compatibility selection result"
        )
    }
}
