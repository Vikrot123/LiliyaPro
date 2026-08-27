package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import java.util.Collections
import java.util.IdentityHashMap
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipelineResult
import pro.liliya.core.runtime.intelligence.experience.store.RuntimeExperienceStore

class DefaultRuntimeAutonomousExecutionExperienceCommitter(
    private val experienceStore: RuntimeExperienceStore
) : RuntimeAutonomousExecutionExperienceCommitter {

    private val committedExecutions =
        Collections.newSetFromMap(
            IdentityHashMap<
                RuntimeAutonomousExecutionPipelineResult,
                Boolean
            >()
        )

    override fun commit(
        decision: RuntimeAutonomousExecutionExperienceCommitDecision
    ): RuntimeAutonomousExecutionExperienceCommitResult {

        val representation =
            decision.representation

        val experience =
            representation.experience

        if (
            decision.state ==
                RuntimeAutonomousExecutionExperienceCommitDecisionState.REJECTED
        ) {
            return RuntimeAutonomousExecutionExperienceCommitResult(
                state =
                    RuntimeAutonomousExecutionExperienceCommitState.REJECTED,
                committed =
                    false,
                experience =
                    experience,
                decision =
                    decision,
                reason =
                    "Autonomous experience commit decision was rejected"
            )
        }

        if (
            decision.state !=
                RuntimeAutonomousExecutionExperienceCommitDecisionState.COMMIT ||
            !decision.shouldCommit
        ) {
            return RuntimeAutonomousExecutionExperienceCommitResult(
                state =
                    RuntimeAutonomousExecutionExperienceCommitState.SKIPPED,
                committed =
                    false,
                experience =
                    experience,
                decision =
                    decision,
                reason =
                    "Autonomous experience commit was not approved"
            )
        }

        val executionResult =
            representation
                .materialization
                .analysis
                .evidence
                .evaluation
                .executionResult

        synchronized(committedExecutions) {
            if (
                committedExecutions.contains(
                    executionResult
                )
            ) {
                return RuntimeAutonomousExecutionExperienceCommitResult(
                    state =
                        RuntimeAutonomousExecutionExperienceCommitState.ALREADY_COMMITTED,
                    committed =
                        false,
                    experience =
                        experience,
                    decision =
                        decision,
                    reason =
                        "Autonomous execution experience was already committed"
                )
            }

            experienceStore.append(
                experience
            )

            committedExecutions.add(
                executionResult
            )
        }

        return RuntimeAutonomousExecutionExperienceCommitResult(
            state =
                RuntimeAutonomousExecutionExperienceCommitState.COMMITTED,
            committed =
                true,
            experience =
                experience,
            decision =
                decision,
            reason =
                "Autonomous post-execution experience committed"
        )
    }
}
