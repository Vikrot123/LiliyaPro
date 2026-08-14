package pro.liliya.core.runtime.composition

object RuntimeCompositionFactory {

    fun create(): RuntimeComposition {
        return DefaultRuntimeComposition()
    }
}
