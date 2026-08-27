package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceCommitter
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceMaterializer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceMaterialization
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceMaterializer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceNovelty
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning.RuntimeAutonomousExecutionExperienceLearningDecisionState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.postexecution.DefaultRuntimeAutonomousExecutionPostExecutionLearningPipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionRecorder
import pro.liliya.core.runtime.intelligence.experience.decision.DefaultRuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionPostExecutionLearningPipelineSemanticHardeningContractTest {

    @Test
    fun inconsistent_feedback_fails_closed_before_experience_materialization() {
        val composition =
            DefaultRuntimeComposition()

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence =
                                0.90
                        ),
                    source =
                        "v1.494-hardening-inconsistent",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.494-hardening-inconsistent",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val validFeedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val inconsistentFeedback =
            validFeedback.copy(
                state =
                    RuntimeAutonomousExecutionFeedbackState.NEGATIVE,
                command =
                    RuntimeCommand.RECOVER,
                message =
                    "foreign post-execution semantics"
            )

        val pipeline =
            DefaultRuntimeAutonomousExecutionPostExecutionLearningPipeline(
                feedbackDeriver =
                    object : RuntimeAutonomousExecutionFeedbackDeriver {

                        override fun derive(
                            assessment:
                                pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessment
                        ): RuntimeAutonomousExecutionFeedback {
                            return inconsistentFeedback
                        }
                    },
                reflectionAnalyzer =
                    DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                        reflectionAnalyzer =
                            composition.decisionReflectionAnalyzer()
                    ),
                reflectionRecorder =
                    DefaultRuntimeAutonomousExecutionReflectionRecorder(
                        history =
                            composition.decisionReflectionHistory()
                    ),
                noveltyDeriver =
                    DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver(),
                learningDecisionEngine =
                    DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine(),
                materializer =
                    DefaultRuntimeAutonomousExecutionExperienceMaterializer(),
                representationDeriver =
                    DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver(),
                commitDecisionEngine =
                    DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine(
                        experienceDecisionEngine =
                            DefaultRuntimeExperienceDecisionEngine()
                    ),
                committer =
                    DefaultRuntimeAutonomousExecutionExperienceCommitter(
                        experienceStore =
                            store
                    )
            )

        val before =
            store.experiences().size

        val result =
            pipeline.process(
                evaluation
            )

        assertEquals(
            false,
            result.analysis.evidence.consistent
        )

        assertEquals(
            RuntimeAutonomousExecutionExperienceLearningDecisionState.REJECTED,
            result.learningDecision.state
        )

        assertFalse(
            result.learningDecision.shouldProcessExperience
        )

        assertNull(
            result.materialization
        )

        assertNull(
            result.representation
        )

        assertNull(
            result.commitDecision
        )

        assertNull(
            result.commitResult
        )

        assertEquals(
            before,
            store.experiences().size
        )
    }

    @Test
    fun materializer_rejection_stops_representation_and_commit() {
        val composition =
            DefaultRuntimeComposition()

        val store =
            composition
                .experienceComposition()
                .experienceStore()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence =
                                0.90
                        ),
                    source =
                        "v1.494-hardening-materializer",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.494-hardening-materializer",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val pipeline =
            DefaultRuntimeAutonomousExecutionPostExecutionLearningPipeline(
                feedbackDeriver =
                    composition.autonomousExecutionFeedbackDeriver(),
                reflectionAnalyzer =
                    DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                        reflectionAnalyzer =
                            composition.decisionReflectionAnalyzer()
                    ),
                reflectionRecorder =
                    DefaultRuntimeAutonomousExecutionReflectionRecorder(
                        history =
                            composition.decisionReflectionHistory()
                    ),
                noveltyDeriver =
                    DefaultRuntimeAutonomousExecutionExperienceNoveltyDeriver(),
                learningDecisionEngine =
                    DefaultRuntimeAutonomousExecutionExperienceLearningDecisionEngine(),
                materializer =
                    object : RuntimeAutonomousExecutionExperienceMaterializer {

                        override fun materialize(
                            analysis:
                                pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult,
                            novelty:
                                RuntimeAutonomousExecutionExperienceNovelty
                        ): RuntimeAutonomousExecutionExperienceMaterialization? {
                            return null
                        }
                    },
                representationDeriver =
                    DefaultRuntimeAutonomousExecutionExperienceRepresentationDeriver(),
                commitDecisionEngine =
                    DefaultRuntimeAutonomousExecutionExperienceCommitDecisionEngine(
                        experienceDecisionEngine =
                            DefaultRuntimeExperienceDecisionEngine()
                    ),
                committer =
                    DefaultRuntimeAutonomousExecutionExperienceCommitter(
                        experienceStore =
                            store
                    )
            )

        val before =
            store.experiences().size

        val result =
            pipeline.process(
                evaluation
            )

        assertEquals(
            RuntimeAutonomousExecutionExperienceLearningDecisionState.PROCESS_NOVEL_EXPERIENCE,
            result.learningDecision.state
        )

        assertNull(
            result.materialization
        )

        assertNull(
            result.representation
        )

        assertNull(
            result.commitDecision
        )

        assertNull(
            result.commitResult
        )

        assertEquals(
            before,
            store.experiences().size
        )
    }
}
