package pro.liliya.core.runtime.intelligence.context.cognitive.selection

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot

interface CognitiveContextSelector {

    fun select(
        snapshot: CognitiveContextSnapshot,
        criteria: CognitiveContextSelectionCriteria =
            CognitiveContextSelectionCriteria()
    ): CognitiveContextSelection
}
