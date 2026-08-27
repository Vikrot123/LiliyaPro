package pro.liliya.core.runtime.intelligence.strategy

import pro.liliya.core.runtime.intelligence.intent.RuntimeIntent

interface RuntimeStrategyDeriver {

    fun derive(
        intent: RuntimeIntent
    ): RuntimeStrategy
}
