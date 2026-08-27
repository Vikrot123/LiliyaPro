package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.cognition.RuntimeAutonomousCognitionPipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.RuntimeAutonomousDecisionExecutor
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalDeriver
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

class DefaultRuntimeAutonomousExecutionPipeline(
    private val cognitionPipeline:
        RuntimeAutonomousCognitionPipeline,
    private val proposalDeriver:
        RuntimeAutonomousDecisionProposalDeriver,
    private val decisionExecutor:
        RuntimeAutonomousDecisionExecutor
) : RuntimeAutonomousExecutionPipeline {

    override fun process(
        intelligence: RuntimeIntelligenceOrchestrationResult,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousExecutionPipelineResult {

        val cognition =
            cognitionPipeline.process(
                intelligence
            )

        val proposal =
            proposalDeriver.derive(
                cognition
            )

        val execution =
            decisionExecutor.execute(
                proposal = proposal,
                source = source,
                authority = authority
            )

        return RuntimeAutonomousExecutionPipelineResult(
            intelligence = intelligence,
            cognition = cognition,
            proposal = proposal,
            execution = execution
        )
    }
}
