package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.explanation.DefaultRuntimeDecisionExplainer
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState

class DefaultRuntimeDecisionGovernanceExplainabilityCompatibilityContractTest {

    @Test
    fun standalone_explainer_without_governance_dependency_remains_compatible() {
        val explanation =
            DefaultRuntimeDecisionExplainer()
                .explain(
                    decision()
                )

        assertNull(
            explanation
                .decisionQualityGovernanceAssessment
        )
    }

    @Test
    fun explicitly_supplied_governance_query_is_snapshotted() {
        val composition =
            DefaultRuntimeComposition()

        val explanation =
            DefaultRuntimeDecisionExplainer(
                governanceQuery =
                    composition
                        .decisionQualityGovernanceQuery()
            )
                .explain(
                    decision()
                )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            explanation
                .decisionQualityGovernanceAssessment
                ?.state
        )
    }

    private fun decision() =
        RuntimeDecision(
            command = null,
            reason =
                "standalone governance explainability compatibility",
            confidence = 0.90,
            knowledgeSelection = null
        )
}
