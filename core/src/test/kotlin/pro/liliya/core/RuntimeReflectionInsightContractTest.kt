package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.reflection.insight.RuntimeReflectionInsight

class RuntimeReflectionInsightContractTest {

    @Test
    fun insight_contains_semantic_reflection_result() {
        val insight = RuntimeReflectionInsight(
            message = "Runtime remains stable",
            confidence = 0.95,
            generatedAt = 1L
        )

        assertEquals(
            "Runtime remains stable",
            insight.message
        )

        assertEquals(
            0.95,
            insight.confidence
        )

        assertTrue(
            insight.generatedAt > 0
        )
    }
}
