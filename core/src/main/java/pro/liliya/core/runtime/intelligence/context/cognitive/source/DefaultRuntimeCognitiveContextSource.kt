package pro.liliya.core.runtime.intelligence.context.cognitive.source

import pro.liliya.core.runtime.intelligence.context.RuntimeContextProvider
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class DefaultRuntimeCognitiveContextSource(
    private val runtimeContextProvider: RuntimeContextProvider
) : RuntimeCognitiveContextSource {

    override fun snapshot(
        type: CognitiveContextType
    ): CognitiveContextSnapshot {
        val runtimeSnapshot = runtimeContextProvider
            .currentContext()
            .snapshot()

        return CognitiveContextSnapshot(
            type = type,
            values = mapOf(
                "runtimeState" to runtimeSnapshot.runtimeState,
                "activeServices" to runtimeSnapshot.activeServices,
                "moduleStates" to runtimeSnapshot.moduleStates,
                "failureReason" to runtimeSnapshot.failureReason,
                "runtimeTimestamp" to runtimeSnapshot.timestamp
            )
        )
    }
}
