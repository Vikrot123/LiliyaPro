package pro.liliya.core.runtime.intelligence.intent

import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningResult

data class RuntimeIntent(
    val state: RuntimeIntentState,
    val objective: String,
    val reasoningResult: RuntimeAutonomousReasoningResult,
    val coherent: Boolean,
    val actionable: Boolean,
    val confidence: Double,
    val reason: String
)
