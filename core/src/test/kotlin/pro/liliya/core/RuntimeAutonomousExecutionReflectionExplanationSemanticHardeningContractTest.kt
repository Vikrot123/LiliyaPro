package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionExplanationDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionReflectionExplanationSemanticHardeningContractTest {

    @Test
    fun inconsistent_evidence_fails_closed_in_explanation() {
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
                    source = "reflection-explanation-hardening",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-explanation-hardening",
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

        val foreignFeedback =
            validFeedback.copy(
                state =
                    RuntimeAutonomousExecutionFeedbackState.NEGATIVE,
                command =
                    RuntimeCommand.RECOVER,
                message =
                    "foreign inconsistent feedback"
            )

        val evidence =
            DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver()
                .derive(
                    evaluation = evaluation,
                    feedback = foreignFeedback
                )

        assertEquals(
            false,
            evidence.consistent
        )

        val explanation =
            DefaultRuntimeAutonomousExecutionReflectionExplanationDeriver()
                .derive(evidence)

        assertNull(
            explanation.command,
            "inconsistent evidence must not expose an actionable command"
        )

        assertEquals(
            0.0,
            explanation.confidence,
            "inconsistent evidence must fail closed with zero confidence"
        )

        assertEquals(
            "Autonomous execution reflection evidence is inconsistent",
            explanation.decisionReason
        )

        assertNull(
            explanation.knowledgeStatement
        )

        assertNull(
            explanation.knowledgeSelectionReason
        )

        assertEquals(
            0.0,
            explanation.knowledgeRelevanceScore
        )

        assertNull(
            explanation.knowledgeProvenance
        )

        assertNull(
            explanation.knowledgeProvenanceIntegrity
        )
    }

    @Test
    fun consistent_evidence_preserves_original_decision_semantics() {
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
                    source = "reflection-explanation-valid",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-explanation-valid",
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

        val evidence =
            DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver()
                .derive(
                    evaluation = evaluation,
                    feedback = feedback
                )

        val explanation =
            DefaultRuntimeAutonomousExecutionReflectionExplanationDeriver()
                .derive(evidence)

        assertEquals(
            execution.execution.decision.command,
            explanation.command
        )

        assertEquals(
            execution.execution.decision.reason,
            explanation.decisionReason
        )

        assertEquals(
            execution.execution.decision.confidence,
            explanation.confidence
        )
    }
}
