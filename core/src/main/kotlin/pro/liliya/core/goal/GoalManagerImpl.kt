package pro.liliya.core.goal

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pro.liliya.domain.api.GoalManager
import pro.liliya.domain.models.Goal
import pro.liliya.domain.models.GoalStatus

class GoalManagerImpl : GoalManager {

    private val mutex = Mutex()

    private val goals = mutableListOf<Goal>()

    override suspend fun addGoal(goal: Goal) {
        mutex.withLock {
            goals.add(goal)
        }
    }

    override suspend fun updateGoal(goal: Goal) {
        mutex.withLock {
            val index = goals.indexOfFirst { it.id == goal.id }

            if (index >= 0) {
                goals[index] = goal
            }
        }
    }

    override suspend fun removeGoal(id: String) {
        mutex.withLock {
            goals.removeAll {
                it.id == id
            }
        }
    }

    override suspend fun getGoal(id: String): Goal? {
        return mutex.withLock {
            goals.firstOrNull {
                it.id == id
            }
        }
    }

    override suspend fun getGoals(): List<Goal> {
        return mutex.withLock {
            goals.toList()
        }
    }

    override suspend fun getGoalsByStatus(
        status: GoalStatus
    ): List<Goal> {
        return mutex.withLock {
            goals.filter {
                it.status == status
            }
        }
    }
}
