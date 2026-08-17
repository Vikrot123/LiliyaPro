package pro.liliya.core.runtime

class RuntimeServiceBootstrapHolder(
    private val factory: () -> RuntimeServiceBootstrap
) {

    private var currentBootstrap: RuntimeServiceBootstrap =
        factory()

    fun get(): RuntimeServiceBootstrap {
        return currentBootstrap
    }

    fun set(bootstrap: RuntimeServiceBootstrap) {
        currentBootstrap = bootstrap
    }

    fun reset() {
        currentBootstrap = factory()
    }
}
