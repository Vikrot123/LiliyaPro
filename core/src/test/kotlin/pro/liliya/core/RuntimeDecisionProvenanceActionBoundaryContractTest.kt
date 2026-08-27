package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class RuntimeDecisionProvenanceActionBoundaryContractTest {

    @Test
    fun provenance_capable_decision_preserves_action_request_payload() {
        val authority =
            RuntimeActionAuthorityContext(
                source = "system",
                level =
                    RuntimeAuthorityLevel.SYSTEM
            )

        val knowledge =
            RuntimeKnowledge(
                statement =
                    "runtime provenance diagnostic knowledge",
                confidence = 0.95,
                source =
                    RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 1L
            )

        val decision =
            RuntimeDecision(
                command =
                    RuntimeCommand.RECOVER,
                reason =
                    "critical instability",
                confidence =
                    0.95,
                knowledgeSelection =
                    RuntimeKnowledgeSelectionResult(
                        knowledge = knowledge,
                        relevantPoolUsed = true,
                        reason =
                            "diagnostic selection",
                        selectionReason =
                            RuntimeKnowledgeSelectionReason
                                .RELEVANT_POOL,
                        relevanceScore = 1.0
                    )
            )

        val request =
            assertNotNull(
                DefaultRuntimeDecisionActionRequestFactory()
                    .create(
                        decision = decision,
                        source = "system",
                        authority = authority
                    )
            )

        assertEquals(
            RuntimeCommand.RECOVER,
            request.command
        )

        assertEquals(
            "critical instability",
            request.reason
        )

        assertEquals(
            "system",
            request.source
        )

        assertEquals(
            authority,
            request.authority
        )
    }
}
