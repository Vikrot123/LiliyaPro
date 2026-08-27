package pro.liliya.core.runtime.intelligence.decision.execution.autonomous

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal

interface RuntimeAutonomousDecisionExecutor {

    fun execute(
        proposal: RuntimeAutonomousDecisionProposal,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousDecisionExecutionResult
}
