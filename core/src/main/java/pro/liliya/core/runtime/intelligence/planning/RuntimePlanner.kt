package pro.liliya.core.runtime.intelligence.planning

import pro.liliya.core.runtime.intelligence.goal.RuntimeGoal

interface RuntimePlanner {

    fun plan(
        goal: RuntimeGoal
    ): RuntimePlan
}
