package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline

import pro.liliya.core.runtime.intelligence.cognition.RuntimeAutonomousCognitionResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.RuntimeAutonomousDecisionExecutionResult
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

data class RuntimeAutonomousExecutionPipelineResult(
    val intelligence: RuntimeIntelligenceOrchestrationResult,
    val cognition: RuntimeAutonomousCognitionResult,
    val proposal: RuntimeAutonomousDecisionProposal,
    val execution: RuntimeAutonomousDecisionExecutionResult
)
