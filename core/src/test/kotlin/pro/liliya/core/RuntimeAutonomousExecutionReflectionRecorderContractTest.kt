package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionRecorder
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionReflectionRecorderContractTest {

    @Test
    fun consistent_autonomous_analysis_records_exact_existing_insight() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "v1.486-record",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.486-record",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val feedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val history =
            composition.decisionReflectionHistory()

        val recorder =
            DefaultRuntimeAutonomousExecutionReflectionRecorder(
                history = history
            )

        val before =
            history.records().size

        val record =
            recorder.record(
                analysis
            )

        val records =
            history.records()

        assertEquals(
            before + 1,
            records.size
        )

        assertSame(
            analysis.insight,
            record?.insight
        )

        assertSame(
            record,
            records.last()
        )
    }

    @Test
    fun recorder_does_not_reanalyze_autonomous_reflection() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.STABLE,
                            confidence = 0.90
                        ),
                    source = "v1.486-no-reanalysis",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.486-no-reanalysis",
                            level = RuntimeAuthorityLevel.SYSTEM
                        )
                )

        val evaluation =
            composition
                .autonomousExecutionEvaluationPipeline()
                .evaluate(execution)

        val feedback =
            composition
                .autonomousExecutionFeedbackDeriver()
                .derive(
                    evaluation.assessment
                )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        val record =
            DefaultRuntimeAutonomousExecutionReflectionRecorder(
                history =
                    composition.decisionReflectionHistory()
            ).record(
                analysis
            )

        assertSame(
            analysis.insight,
            record?.insight,
            "autonomous insight must be recorded without re-analysis"
        )
    }

    @Test
    fun inconsistent_autonomous_analysis_is_not_recorded() {
        val composition =
            DefaultRuntimeComposition()

        val execution =
            composition
                .autonomousExecutionPipeline()
                .process(
                    intelligence =
                        RuntimeIntelligenceFixture.result(
                            significance =
                                RuntimeMeaningSignificance.WARNING,
                            confidence = 0.80
                        ),
                    source = "v1.486-inconsistent",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.486-inconsistent",
                            level = RuntimeAuthorityLevel.SYSTEM
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
                    "foreign reflection semantics"
            )

        val analysis =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = inconsistentFeedback
            )

        assertEquals(
            false,
            analysis.evidence.consistent
        )

        val history =
            composition.decisionReflectionHistory()

        val before =
            history.records().size

        val result =
            DefaultRuntimeAutonomousExecutionReflectionRecorder(
                history = history
            ).record(
                analysis
            )

        assertNull(
            result
        )

        assertEquals(
            before,
            history.records().size
        )
    }
}
