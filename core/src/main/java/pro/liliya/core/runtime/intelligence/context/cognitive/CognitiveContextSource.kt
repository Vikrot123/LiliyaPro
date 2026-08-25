package pro.liliya.core.runtime.intelligence.context.cognitive

interface CognitiveContextSource {

    fun snapshot(
        type: CognitiveContextType
    ): CognitiveContextSnapshot?
}
