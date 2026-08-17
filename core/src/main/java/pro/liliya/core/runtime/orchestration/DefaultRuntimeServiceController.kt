package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeRecoverySnapshot

class DefaultRuntimeServiceController(
    private val composition: RuntimeServiceComposition
) : RuntimeServiceController {

    override fun startRuntimeServices() {
        start()
    }

    override fun stopRuntimeServices() {
        stop()
    }

    override fun start() {
        composition.serviceBootstrap().start()
    }

    override fun stop() {
        composition.serviceBootstrap().stop()
    }

    override fun provider(): RuntimeServiceProvider {
        return composition.serviceProvider()
    }

    override fun providerHolder(): RuntimeServiceProviderHolder {
        return composition.runtimeServiceProviderHolder()
    }

    override fun registry(): RuntimeServiceRegistry {
        return composition.runtimeServiceRegistry()
    }

    override fun bootstrap(): RuntimeServiceBootstrap {
        return composition.serviceBootstrap()
    }

    override fun states(): Map<String, RuntimeServiceState> {
        return composition.runtimeServiceStates()
    }

    override fun failures(): List<RuntimeServiceFailure> {
        return composition.runtimeServiceFailures()
    }

    override fun health(): Map<String, RuntimeServiceHealth> {
        return composition.runtimeServiceHealth()
    }

    override fun recoverySnapshot(): RuntimeRecoverySnapshot? {
        return composition.runtimeServiceRecoverySnapshot()
    }

    override fun createBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap {
        return composition.createServiceBootstrap(
            provider = provider
        )
    }
}
