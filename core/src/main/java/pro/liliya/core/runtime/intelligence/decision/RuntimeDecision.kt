package pro.liliya.core.runtime.intelligence.decision

import pro.liliya.core.runtime.control.RuntimeCommand

data class RuntimeDecision(
    val command: RuntimeCommand?,
    val reason: String,
    val confidence: Double
)
