package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipelineResult

class DefaultRuntimeAutonomousExecutionOutcomeDeriver :
    RuntimeAutonomousExecutionOutcomeDeriver {

    override fun derive(
        result: RuntimeAutonomousExecutionPipelineResult
    ): RuntimeAutonomousExecutionOutcome {

        val execution =
            result.execution

        val actionResult =
            execution.actionResult

        if (actionResult == null) {
            return RuntimeAutonomousExecutionOutcome(
                state =
                    RuntimeAutonomousExecutionOutcomeState.NO_ACTION,
                proposalState =
                    result.proposal.state,
                command =
                    execution.decision.command,
                actionAttempted =
                    false,
                actionSucceeded =
                    null,
                previousRuntimeState =
                    null,
                currentRuntimeState =
                    null,
                message =
                    null
            )
        }

        val controlResult =
            actionResult.controlResult

        return RuntimeAutonomousExecutionOutcome(
            state =
                if (actionResult.success) {
                    RuntimeAutonomousExecutionOutcomeState.SUCCEEDED
                } else {
                    RuntimeAutonomousExecutionOutcomeState.FAILED
                },
            proposalState =
                result.proposal.state,
            command =
                execution.decision.command,
            actionAttempted =
                true,
            actionSucceeded =
                actionResult.success,
            previousRuntimeState =
                controlResult.previousState,
            currentRuntimeState =
                controlResult.currentState,
            message =
                controlResult.message
        )
    }
}
