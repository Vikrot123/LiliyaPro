package pro.liliya.core.runtime

class RuntimeServiceBootstrapHolder(
    private val factory: () -> RuntimeServiceBootstrap
) {

    private var currentBootstrap: RuntimeServiceBootstrap =
        factory()

    fun get(): RuntimeServiceBootstrap {
        return currentBootstrap
    }

    fun replace(
        bootstrap: RuntimeServiceBootstrap
    ) {
        currentBootstrap.stop()
        currentBootstrap = bootstrap
    }

    fun reset() {
        currentBootstrap.stop()
        currentBootstrap = factory()
    }
}
