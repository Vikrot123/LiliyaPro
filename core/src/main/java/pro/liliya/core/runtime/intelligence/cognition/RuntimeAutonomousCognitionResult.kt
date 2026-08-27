package pro.liliya.core.runtime.intelligence.cognition

import pro.liliya.core.runtime.intelligence.intent.RuntimeIntent
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningResult
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategy

data class RuntimeAutonomousCognitionResult(
    val reasoningResult: RuntimeAutonomousReasoningResult,
    val intent: RuntimeIntent,
    val strategy: RuntimeStrategy,
    val coherent: Boolean,
    val actionable: Boolean,
    val confidence: Double
)
