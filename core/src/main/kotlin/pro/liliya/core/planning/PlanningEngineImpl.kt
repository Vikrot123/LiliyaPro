package pro.liliya.core.planning

import pro.liliya.domain.api.PlanningEngine
import pro.liliya.domain.models.Plan
import pro.liliya.domain.models.ReasoningResult

/**
 * Первая реализация Planning Engine.
 *
 * Пока строит простой линейный план.
 */
class PlanningEngineImpl : PlanningEngine {

    override suspend fun createPlan(
        reasoning: ReasoningResult
    ): Plan {

        return Plan(
            steps = listOf(
                "Analyze reasoning",
                "Prepare execution",
                "Execute response"
            ),
            priority = 0,
            goalId = null
        )
    }
}
