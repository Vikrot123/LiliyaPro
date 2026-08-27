package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.postexecution

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitDecision
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceLearningDecision
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceMaterialization
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceNovelty
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceRepresentation
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord

data class RuntimeAutonomousExecutionPostExecutionLearningPipelineResult(
    val evaluation:
        RuntimeAutonomousExecutionEvaluationResult,
    val feedback:
        RuntimeAutonomousExecutionFeedback,
    val analysis:
        RuntimeAutonomousExecutionReflectionAnalysisResult,
    val reflectionRecord:
        RuntimeDecisionReflectionRecord?,
    val novelty:
        RuntimeAutonomousExecutionExperienceNovelty,
    val learningDecision:
        RuntimeAutonomousExecutionExperienceLearningDecision,
    val materialization:
        RuntimeAutonomousExecutionExperienceMaterialization?,
    val representation:
        RuntimeAutonomousExecutionExperienceRepresentation?,
    val commitDecision:
        RuntimeAutonomousExecutionExperienceCommitDecision?,
    val commitResult:
        RuntimeAutonomousExecutionExperienceCommitResult?
)
