package pro.liliya.core.runtime.intelligence.memory.factory

import pro.liliya.core.runtime.intelligence.memory.composition.RuntimeMemoryComposition

interface RuntimeMemoryCompositionFactory {

    fun create(): RuntimeMemoryComposition
}
