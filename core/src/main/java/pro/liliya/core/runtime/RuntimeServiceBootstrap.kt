package pro.liliya.core.runtime

import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeServiceBootstrap(
    private val providerHolder: RuntimeServiceProviderHolder,
    private val registry: RuntimeServiceRegistry,
    private val supervisor: RuntimeSupervisor,
    private val recoveryManager: RuntimeRecoveryManager
) {

    constructor(
        provider: RuntimeServiceProvider,
        registry: RuntimeServiceRegistry,
        supervisor: RuntimeSupervisor,
        recoveryManager: RuntimeRecoveryManager
    ) : this(
        RuntimeServiceProviderHolder(provider),
        registry,
        supervisor,
        recoveryManager
    )

    constructor(
        provider: RuntimeServiceProvider,
        registry: RuntimeServiceRegistry
    ) : this(
        createLegacyDependencies(provider, registry)
    )

    private constructor(
        dependencies: LegacyDependencies
    ) : this(
        dependencies.providerHolder,
        dependencies.registry,
        dependencies.supervisor,
        dependencies.recoveryManager
    )

    private data class LegacyDependencies(
        val providerHolder: RuntimeServiceProviderHolder,
        val registry: RuntimeServiceRegistry,
        val supervisor: RuntimeSupervisor,
        val recoveryManager: RuntimeRecoveryManager
    )

    private companion object {
        private fun createLegacyDependencies(
            provider: RuntimeServiceProvider,
            registry: RuntimeServiceRegistry
        ): LegacyDependencies {
            val supervisor = RuntimeSupervisor(
                registryProvider = { registry }
            )

            val recoveryEventBus = RuntimeRecoveryEventBus()

            return LegacyDependencies(
                providerHolder = RuntimeServiceProviderHolder(provider),
                registry = registry,
                supervisor = supervisor,
                recoveryManager = RuntimeRecoveryManager(supervisor, registry, recoveryEventBus)
            )
        }
    }

    private val registeredServices = mutableMapOf<String, RuntimeService>()

    private var started = false

    fun register(service: RuntimeService) {
        registeredServices[service.name] = service
    }

    fun start() {

        println("BOOTSTRAP START CALLED")
        println("BOOTSTRAP REGISTERED BEFORE START = ${registeredServices.keys}")

        if (started) {
            return
        }

        providerHolder
            .get()
            .provideServices()
            .forEach { service ->
                registeredServices[service.name] = service
            }

        registeredServices.values.forEach { service ->
            println("BOOTSTRAP REGISTERING SERVICE = ${service.name}")
            registry.register(service)
        }

        println("BOOTSTRAP STATES AFTER REGISTER = ${registry.getStates().keys}")

        recoveryManager.install()

        supervisor.start()

        started = true
    }

    fun stop() {

        if (!started) {
            return
        }

        recoveryManager.reset()

        supervisor.stop()
        supervisor.reset()

        registry.reset()
        registeredServices.clear()

        started = false
    }
    
    fun getStates(): Map<String, RuntimeServiceState> {
        return registry.getStates()
    }

    fun getFailures(): List<RuntimeServiceFailure> {
        return registry.getFailures()
    }

    fun getHealth(): Map<String, RuntimeServiceHealth> {
        return registry.getHealth()
    }


    fun recover(serviceName: String): Boolean {
        return supervisor.recover(serviceName)
    }

    fun getRestartCount(serviceName: String): Int {
        return supervisor.getRestartCount(serviceName)
    }

    fun getRecoverySnapshot(): RuntimeRecoverySnapshot {
        return recoveryManager.snapshot()
    }

}
