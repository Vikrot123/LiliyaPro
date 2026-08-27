package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.reflection.DefaultRuntimeDecisionReflectionAnalyzer
import pro.liliya.core.runtime.intelligence.decision.reflection.DefaultRuntimeDecisionReflectionRecorder
import pro.liliya.core.runtime.intelligence.decision.reflection.history.DefaultRuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class DefaultRuntimeDecisionReflectionRecorderContractTest {

    @Test
    fun recorder_analyzes_and_records_exactly_one_insight() {
        val analyzer =
            DefaultRuntimeDecisionReflectionAnalyzer()

        val history =
            DefaultRuntimeDecisionReflectionHistory()

        val recorder =
            DefaultRuntimeDecisionReflectionRecorder(
                analyzer = analyzer,
                history = history
            )

        val explanation =
            RuntimeDecisionExplanation(
                command = null,
                decisionReason =
                    "runtime decision reflection record",
                confidence = 0.90,
                knowledgeStatement = null,
                knowledgeSelectionReason =
                    RuntimeKnowledgeSelectionReason.EMPTY,
                knowledgeRelevanceScore = 0.0
            )

        val record =
            recorder.analyzeAndRecord(
                explanation
            )

        assertEquals(
            1,
            history.records().size
        )

        assertSame(
            record,
            history.records().single()
        )

        assertEquals(
            explanation.decisionReason,
            record.insight.evidence.decisionReason
        )
    }
}
