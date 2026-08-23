package pro.liliya.core.runtime.intelligence.reflection.trend

import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot

interface RuntimeReflectionTrendAnalyzer {

    fun analyze(
        history: List<RuntimeReflectionSnapshot>
    ): RuntimeReflectionTrend
}
