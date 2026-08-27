package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.relevance.DefaultRuntimeKnowledgeRelevanceEvaluator

class DefaultRuntimeKnowledgeRelevanceEvaluatorContractTest {

    private val evaluator =
        DefaultRuntimeKnowledgeRelevanceEvaluator()

    @Test
    fun exact_match_is_fully_relevant() {
        val result =
            evaluator.evaluate(
                statement =
                    "Runtime maintains stable operational state",
                interpretation =
                    "Runtime maintains stable operational state"
            )

        assertTrue(result.relevant)
        assertEquals(1.0, result.score)
    }

    @Test
    fun related_wording_is_relevant_without_exact_substring() {
        val result =
            evaluator.evaluate(
                statement =
                    "runtime operational state remained stable",
                interpretation =
                    "Runtime maintains stable operational state"
            )

        assertTrue(result.relevant)
        assertEquals(0.8, result.score)
    }

    @Test
    fun one_shared_generic_token_is_not_relevant() {
        val result =
            evaluator.evaluate(
                statement = "runtime backup completed",
                interpretation =
                    "Runtime maintains stable operational state"
            )

        assertFalse(result.relevant)
    }

    @Test
    fun threshold_boundary_remains_fifty_percent() {
        val result =
            evaluator.evaluate(
                statement =
                    "runtime maintains unrelated knowledge",
                interpretation =
                    "runtime maintains stable operational"
            )

        assertTrue(result.relevant)
        assertEquals(0.5, result.score)
    }
}
