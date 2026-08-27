package pro.liliya.core.runtime.intelligence.intent

import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningResult

interface RuntimeIntentDeriver {

    fun derive(
        reasoningResult: RuntimeAutonomousReasoningResult
    ): RuntimeIntent
}
