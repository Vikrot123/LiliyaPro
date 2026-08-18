package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceBootstrapHolder
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.RuntimeSupervisor
import pro.liliya.core.runtime.RuntimeRecoveryManager

interface RuntimeServiceComposition {

    fun serviceProvider(): RuntimeServiceProvider

    fun runtimeServiceProviderHolder(): RuntimeServiceProviderHolder

    fun runtimeServiceProvider(): RuntimeServiceProvider

    fun setRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    )

    fun resetRuntimeServiceProvider()

    fun replaceRuntimeServiceBootstrap(
        bootstrap: RuntimeServiceBootstrap
    )

    fun configureRuntimeServiceProvider(
        provider: RuntimeServiceProvider
    )

    fun resetRuntimeServiceConfiguration()

    fun runtimeServiceRegistry(): RuntimeServiceRegistry

    fun runtimeSupervisor(): RuntimeSupervisor

    fun runtimeRecoveryManager(): RuntimeRecoveryManager

    fun serviceBootstrap(): RuntimeServiceBootstrap

    fun runtimeServiceBootstrapHolder(): RuntimeServiceBootstrapHolder


    fun runtimeServiceStates(): Map<String, RuntimeServiceState>

    fun runtimeServiceFailures(): List<RuntimeServiceFailure>

    fun runtimeServiceHealth(): Map<String, RuntimeServiceHealth>

    fun runtimeServiceRecoverySnapshot(): RuntimeRecoverySnapshot?

    fun createServiceBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap
}
