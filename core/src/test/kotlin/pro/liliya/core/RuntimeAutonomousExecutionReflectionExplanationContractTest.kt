package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.DefaultRuntimeAutonomousExecutionReflectionExplanationDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeAutonomousExecutionReflectionExplanationContractTest {

    @Test
    fun autonomous_reflection_evidence_maps_to_truthful_decision_explanation() {
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
                    source = "reflection-explanation-test",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-explanation-test",
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
            evidence.command,
            explanation.command
        )

        assertEquals(
            evidence.decisionReason,
            explanation.decisionReason
        )

        assertEquals(
            evidence.confidence,
            explanation.confidence
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
    fun explanation_preserves_exact_autonomous_decision_semantics() {
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
                    source = "reflection-explanation-identity",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-explanation-identity",
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

        assertSame(
            execution.execution.decision,
            evidence.decision
        )

        assertTrue(
            evidence.consistent
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

    @Test
    fun autonomous_explanation_does_not_invent_knowledge() {
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
                    source = "reflection-no-knowledge",
                    authority =
                        RuntimeActionAuthorityContext(
                            source = "reflection-no-knowledge",
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

        assertNull(
            evidence.decision.knowledgeSelection
        )

        assertNull(
            explanation.knowledgeStatement
        )

        assertNull(
            explanation.knowledgeProvenance
        )
    }
}
