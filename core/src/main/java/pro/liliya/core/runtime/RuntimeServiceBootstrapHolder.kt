package pro.liliya.core.runtime

class RuntimeServiceBootstrapHolder(
    private val defaultBootstrap: RuntimeServiceBootstrap
) {

    private var currentBootstrap: RuntimeServiceBootstrap = defaultBootstrap

    fun get(): RuntimeServiceBootstrap {
        return currentBootstrap
    }

    fun set(bootstrap: RuntimeServiceBootstrap) {
        currentBootstrap = bootstrap
    }

    fun reset() {
        currentBootstrap = defaultBootstrap
    }
}
