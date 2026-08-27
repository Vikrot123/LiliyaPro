package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.postexecution

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceCommitter
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceLearningDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceMaterializer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceRepresentationDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionRecorder

class DefaultRuntimeAutonomousExecutionPostExecutionLearningPipeline(
    private val feedbackDeriver:
        RuntimeAutonomousExecutionFeedbackDeriver,
    private val reflectionAnalyzer:
        RuntimeAutonomousExecutionReflectionAnalyzer,
    private val reflectionRecorder:
        RuntimeAutonomousExecutionReflectionRecorder,
    private val noveltyDeriver:
        RuntimeAutonomousExecutionExperienceNoveltyDeriver,
    private val learningDecisionEngine:
        RuntimeAutonomousExecutionExperienceLearningDecisionEngine,
    private val materializer:
        RuntimeAutonomousExecutionExperienceMaterializer,
    private val representationDeriver:
        RuntimeAutonomousExecutionExperienceRepresentationDeriver,
    private val commitDecisionEngine:
        RuntimeAutonomousExecutionExperienceCommitDecisionEngine,
    private val committer:
        RuntimeAutonomousExecutionExperienceCommitter
) : RuntimeAutonomousExecutionPostExecutionLearningPipeline {

    override fun process(
        evaluation: RuntimeAutonomousExecutionEvaluationResult
    ): RuntimeAutonomousExecutionPostExecutionLearningPipelineResult {

        val feedback =
            feedbackDeriver.derive(
                evaluation.assessment
            )

        val analysis =
            reflectionAnalyzer.analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val reflectionRecord =
            reflectionRecorder.record(
                analysis
            )

        val novelty =
            noveltyDeriver.derive(
                feedback
            )

        val learningDecision =
            learningDecisionEngine.decide(
                analysis = analysis,
                novelty = novelty
            )

        if (!learningDecision.shouldProcessExperience) {
            return RuntimeAutonomousExecutionPostExecutionLearningPipelineResult(
                evaluation =
                    evaluation,
                feedback =
                    feedback,
                analysis =
                    analysis,
                reflectionRecord =
                    reflectionRecord,
                novelty =
                    novelty,
                learningDecision =
                    learningDecision,
                materialization =
                    null,
                representation =
                    null,
                commitDecision =
                    null,
                commitResult =
                    null
            )
        }

        val materialization =
            materializer.materialize(
                analysis = analysis,
                novelty = novelty
            )
                ?: return RuntimeAutonomousExecutionPostExecutionLearningPipelineResult(
                    evaluation =
                        evaluation,
                    feedback =
                        feedback,
                    analysis =
                        analysis,
                    reflectionRecord =
                        reflectionRecord,
                    novelty =
                        novelty,
                    learningDecision =
                        learningDecision,
                    materialization =
                        null,
                    representation =
                        null,
                    commitDecision =
                        null,
                    commitResult =
                        null
                )

        val representation =
            representationDeriver.derive(
                materialization
            )

        val commitDecision =
            commitDecisionEngine.decide(
                learningDecision =
                    learningDecision,
                representation =
                    representation
            )

        val commitResult =
            committer.commit(
                commitDecision
            )

        return RuntimeAutonomousExecutionPostExecutionLearningPipelineResult(
            evaluation =
                evaluation,
            feedback =
                feedback,
            analysis =
                analysis,
            reflectionRecord =
                reflectionRecord,
            novelty =
                novelty,
            learningDecision =
                learningDecision,
            materialization =
                materialization,
            representation =
                representation,
            commitDecision =
                commitDecision,
            commitResult =
                commitResult
        )
    }
}
