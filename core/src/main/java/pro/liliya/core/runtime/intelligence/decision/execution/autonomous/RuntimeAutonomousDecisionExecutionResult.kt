package pro.liliya.core.runtime.intelligence.decision.execution.autonomous

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal

data class RuntimeAutonomousDecisionExecutionResult(
    val proposal: RuntimeAutonomousDecisionProposal,
    val decision: RuntimeDecision,
    val request: RuntimeActionRequest?,
    val actionResult: RuntimeActionResult?
)
