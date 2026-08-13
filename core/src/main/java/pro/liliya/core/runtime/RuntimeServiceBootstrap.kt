package pro.liliya.core.runtime

class RuntimeServiceBootstrap(
    private val provider: RuntimeServiceProvider,
    private var registry: RuntimeServiceRegistry = RuntimeServiceRegistry()
) {

    private val registeredServices = mutableMapOf<String, RuntimeService>()

    private val supervisor =
        RuntimeSupervisor(
        registryProvider = { registry }
    )

    private val recoveryManager =
        RuntimeRecoveryManager(supervisor)

    private var started = false

    fun register(service: RuntimeService) {
        registeredServices[service.name] = service
    }

    fun start() {

        if (started) {
            return
        }

        provider
            .provideServices()
            .forEach { service ->
                registeredServices[service.name] = service
            }

        registeredServices.values.forEach { service ->
            registry.register(service)
        }

        recoveryManager.install()

        supervisor.start()

        started = true
    }

    fun stop() {

        if (!started) {
            return
        }

        supervisor.stop()

        registry = RuntimeServiceRegistry()

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
