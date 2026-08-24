package pro.liliya.core.runtime.intelligence.memory.composition

import pro.liliya.core.runtime.intelligence.memory.factory.RuntimeMemoryCompositionFactory

class DefaultRuntimeMemoryCompositionHolder(
    factory: RuntimeMemoryCompositionFactory
) : RuntimeMemoryCompositionHolder {

    private val memoryComposition =
        factory.create()

    override fun composition(): RuntimeMemoryComposition {
        return memoryComposition
    }
}
