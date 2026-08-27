package pro.liliya.core.runtime.intelligence.strategy

import pro.liliya.core.runtime.intelligence.intent.RuntimeIntent

data class RuntimeStrategy(
    val state: RuntimeStrategyState,
    val objective: String,
    val intent: RuntimeIntent,
    val coherent: Boolean,
    val actionable: Boolean,
    val confidence: Double,
    val reason: String
)
