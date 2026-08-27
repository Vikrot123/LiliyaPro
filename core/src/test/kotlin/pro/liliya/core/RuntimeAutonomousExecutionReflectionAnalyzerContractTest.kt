package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionReflectionAnalyzerContractTest {

    @Test
    fun autonomous_execution_reuses_existing_reflection_semantics() {
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
                    source = "v1.485",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.485",
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
                .derive(evaluation.assessment)

        val result =
            DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
                reflectionAnalyzer =
                    composition.decisionReflectionAnalyzer()
            ).analyze(
                evaluation = evaluation,
                feedback = feedback
            )

        assertSame(
            execution.execution.decision,
            result.evidence.decision
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            result.insight.evidence.command
        )

        assertEquals(
            execution.execution.decision.reason,
            result.insight.evidence.decisionReason
        )

        assertEquals(
            execution.execution.decision.confidence,
            result.insight.evidence.confidence
        )

        assertFalse(
            result.insight.evidence.knowledgeUsed
        )

        assertFalse(
            result.insight.requiresAttention
        )

        assertFalse(
            result.insight.trustworthyKnowledgeBasis
        )
    }

    @Test
    fun autonomous_reflection_analysis_does_not_record_history() {
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
                    source = "v1.485-read-only",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "v1.485-read-only",
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
                .derive(evaluation.assessment)

        val before =
            composition
                .decisionReflectionHistory()
                .records()
                .size

        DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
            reflectionAnalyzer =
                composition.decisionReflectionAnalyzer()
        ).analyze(
            evaluation = evaluation,
            feedback = feedback
        )

        val after =
            composition
                .decisionReflectionHistory()
                .records()
                .size

        assertEquals(
            before,
            after
        )
    }
}
