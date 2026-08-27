package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState

data class RuntimeAutonomousExecutionOutcome(
    val state: RuntimeAutonomousExecutionOutcomeState,
    val proposalState: RuntimeAutonomousDecisionProposalState,
    val command: RuntimeCommand?,
    val actionAttempted: Boolean,
    val actionSucceeded: Boolean?,
    val previousRuntimeState: CoreRuntimeState?,
    val currentRuntimeState: CoreRuntimeState?,
    val message: String?
)
