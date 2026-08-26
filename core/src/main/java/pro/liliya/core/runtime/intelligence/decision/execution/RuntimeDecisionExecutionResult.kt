package pro.liliya.core.runtime.intelligence.decision.execution

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision

data class RuntimeDecisionExecutionResult(
    val decision: RuntimeDecision,
    val request: RuntimeActionRequest?,
    val actionResult: RuntimeActionResult?
)
