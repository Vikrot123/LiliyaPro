package pro.liliya.core.runtime.service.composition

import pro.liliya.core.runtime.CoreRuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceBootstrapHolder
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeSupervisor
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.composition.RuntimeComposition
import pro.liliya.core.runtime.orchestration.RuntimeServiceComposition

class DefaultRuntimeServiceComposition(
    private val runtimeComposition: RuntimeComposition
) : RuntimeServiceComposition {

    private val serviceProvider =
        CoreRuntimeServiceProvider()

    private var runtimeServiceRegistry =
        RuntimeServiceRegistry()

    private val runtimeSupervisor =
        RuntimeSupervisor(
            registryProvider = { runtimeServiceRegistry }
        )

    private val runtimeRecoveryManager =
        RuntimeRecoveryManager(runtimeSupervisor)

    private val runtimeServiceProviderHolder =
        RuntimeServiceProviderHolder(
            serviceProvider
        )

    private val runtimeServiceBootstrapHolder =
        RuntimeServiceBootstrapHolder {
            createServiceBootstrap(
                runtimeServiceProvider()
            )
        }

    override fun serviceProvider(): RuntimeServiceProvider {
        return serviceProvider
    }

    override fun runtimeServiceProviderHolder(): RuntimeServiceProviderHolder {
        return runtimeServiceProviderHolder
    }

    override fun runtimeServiceRegistry(): RuntimeServiceRegistry {
        return runtimeServiceRegistry
    }

    override fun runtimeSupervisor(): RuntimeSupervisor {
        return runtimeSupervisor
    }

    override fun runtimeRecoveryManager(): RuntimeRecoveryManager {
        return runtimeRecoveryManager
    }

    override fun serviceBootstrap(): RuntimeServiceBootstrap {
        return runtimeServiceBootstrapHolder.get()
    }

    override fun runtimeServiceBootstrapHolder(): RuntimeServiceBootstrapHolder {
        return runtimeServiceBootstrapHolder
    }

    override fun runtimeServiceProvider(): RuntimeServiceProvider {
        return runtimeServiceProviderHolder.get()
            ?: error("RuntimeServiceProvider is not initialized")
    }

    override fun setRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    ) {
        runtimeServiceProviderHolder.set(provider)
    }

    override fun resetRuntimeServiceProvider() {
        runtimeServiceProviderHolder.reset()
    }

    override fun runtimeServiceBootstrap(): RuntimeServiceBootstrap {
        return runtimeServiceBootstrapHolder.get()
    }

    override fun setRuntimeServiceBootstrap(
        bootstrap: RuntimeServiceBootstrap
    ) {
        runtimeServiceBootstrapHolder.set(bootstrap)
    }

    override fun configureRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    ) {
        runtimeServiceBootstrapHolder
            .get()
            .stop()

        setRuntimeServiceProvider(provider)

        setRuntimeServiceBootstrap(
            createServiceBootstrap(
                runtimeServiceProvider()
            )
        )
    }

    override fun resetRuntimeServiceConfiguration() {
        val provider = runtimeServiceProvider()

        runtimeServiceBootstrapHolder
            .get()
            .stop()

        setRuntimeServiceBootstrap(
            createServiceBootstrap(provider)
        )
    }

    override fun runtimeServiceStates() =
        runtimeServiceBootstrapHolder.get().getStates()

    override fun runtimeServiceFailures() =
        runtimeServiceBootstrapHolder.get().getFailures()

    override fun runtimeServiceHealth() =
        runtimeServiceBootstrapHolder.get().getHealth()

    override fun runtimeServiceRecoverySnapshot() =
        runtimeServiceBootstrapHolder.get().getRecoverySnapshot()

    override fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap {

        return RuntimeServiceBootstrap(
            runtimeServiceProviderHolder,
            runtimeServiceRegistry,
            runtimeSupervisor,
            runtimeRecoveryManager
        )
    }
}
