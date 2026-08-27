package pro.liliya.core.runtime.intelligence.decision.reflection.history

import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight

interface RuntimeDecisionReflectionHistory {

    fun record(
        insight: RuntimeDecisionReflectionInsight
    ): RuntimeDecisionReflectionRecord

    fun records(): List<RuntimeDecisionReflectionRecord>

    fun clear()
}
