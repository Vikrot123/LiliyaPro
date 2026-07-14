package pro.liliya.domain.api

import pro.liliya.domain.models.Goal
import pro.liliya.domain.models.GoalStatus

interface GoalManager {

    suspend fun addGoal(goal: Goal)

    suspend fun updateGoal(goal: Goal)

    suspend fun removeGoal(id: String)

    suspend fun getGoal(id: String): Goal?

    suspend fun getGoals(): List<Goal>

    suspend fun getGoalsByStatus(
        status: GoalStatus
    ): List<Goal>
}
