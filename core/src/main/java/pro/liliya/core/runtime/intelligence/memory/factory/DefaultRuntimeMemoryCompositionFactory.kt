package pro.liliya.core.runtime.intelligence.memory.factory

import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryComposition
import pro.liliya.core.runtime.intelligence.memory.composition.RuntimeMemoryComposition

class DefaultRuntimeMemoryCompositionFactory :
    RuntimeMemoryCompositionFactory {

    override fun create(): RuntimeMemoryComposition {
        return DefaultRuntimeMemoryComposition()
    }
}
