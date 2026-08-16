package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeRecoverySnapshot

interface RuntimeServiceComposition {

    fun serviceProvider(): RuntimeServiceProvider

    fun runtimeServiceProviderHolder(): RuntimeServiceProviderHolder

    fun runtimeServiceRegistry(): RuntimeServiceRegistry

    fun serviceBootstrap(): RuntimeServiceBootstrap

    fun runtimeServiceStates(): Map<String, RuntimeServiceState>

    fun runtimeServiceFailures(): List<RuntimeServiceFailure>

    fun runtimeServiceHealth(): Map<String, RuntimeServiceHealth>

    fun runtimeServiceRecoverySnapshot(): RuntimeRecoverySnapshot?

    fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap
}
