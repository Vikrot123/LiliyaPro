package pro.liliya.core.runtime.intelligence.context.cognitive.selection

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot

class DefaultCognitiveContextSelector :
    CognitiveContextSelector {

    override fun select(
        snapshot: CognitiveContextSnapshot,
        criteria: CognitiveContextSelectionCriteria
    ): CognitiveContextSelection {

        /*
         * The foundation does not impose a scoring algorithm yet.
         *
         * Selection metadata will be introduced by concrete context
         * sources without coupling this selector to Memory, Knowledge,
         * Conversation, or any particular model.
         */
        return CognitiveContextSelection(
            values = snapshot.values,
            selectedCount = snapshot.values.size,
            rejectedCount = 0
        )
    }
}
