package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceProviderHolder
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeRecoverySnapshot

interface RuntimeServiceController {

    fun startRuntimeServices()

    fun stopRuntimeServices()

    fun start()

    fun stop()

    fun register(service: RuntimeService)

    fun provider(): RuntimeServiceProvider

    fun providerHolder(): RuntimeServiceProviderHolder

    fun registry(): RuntimeServiceRegistry

    fun bootstrap(): RuntimeServiceBootstrap

    fun states(): Map<String, RuntimeServiceState>

    fun failures(): List<RuntimeServiceFailure>

    fun health(): Map<String, RuntimeServiceHealth>

    fun recoverySnapshot(): RuntimeRecoverySnapshot?

    fun createBootstrap(
        provider: RuntimeServiceProvider
    ): RuntimeServiceBootstrap
}
