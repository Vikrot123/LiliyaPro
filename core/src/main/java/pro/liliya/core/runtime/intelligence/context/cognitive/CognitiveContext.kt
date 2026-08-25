package pro.liliya.core.runtime.intelligence.context.cognitive

interface CognitiveContext {

    fun snapshot(
        type: CognitiveContextType
    ): CognitiveContextSnapshot

    fun sources(): List<CognitiveContextSource>
}
