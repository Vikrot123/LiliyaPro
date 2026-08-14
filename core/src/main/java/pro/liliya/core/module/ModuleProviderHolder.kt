package pro.liliya.core.module

class ModuleProviderHolder(
    private val defaultProvider: ModuleProvider
) {

    private var currentProvider: ModuleProvider = defaultProvider

    fun get(): ModuleProvider {
        return currentProvider
    }

    fun set(provider: ModuleProvider) {
        currentProvider = provider
    }

    fun reset() {
        currentProvider = defaultProvider
    }
}
