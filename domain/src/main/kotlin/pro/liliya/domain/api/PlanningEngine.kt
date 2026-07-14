package pro.liliya.domain.api

import pro.liliya.domain.models.Plan
import pro.liliya.domain.models.ReasoningResult

/**
 * Движок планирования LiliyaPro.
 *
 * На основе результата рассуждения
 * строит план выполнения.
 */
interface PlanningEngine {

    suspend fun createPlan(
        reasoning: ReasoningResult
    ): Plan
}
