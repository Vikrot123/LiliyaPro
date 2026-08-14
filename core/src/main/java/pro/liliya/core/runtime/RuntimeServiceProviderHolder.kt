package pro.liliya.core.runtime

class RuntimeServiceProviderHolder(
    private val defaultProvider: RuntimeServiceProvider
) {

    private var currentProvider: RuntimeServiceProvider = defaultProvider

    fun get(): RuntimeServiceProvider {
        return currentProvider
    }

    fun set(provider: RuntimeServiceProvider) {
        currentProvider = provider
    }

    fun reset() {
        currentProvider = defaultProvider
    }
}
