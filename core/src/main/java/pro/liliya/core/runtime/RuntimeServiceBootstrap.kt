package pro.liliya.core.runtime

class RuntimeServiceBootstrap(
    private val provider: RuntimeServiceProvider,
    private var registry: RuntimeServiceRegistry = RuntimeServiceRegistry()
) {

    private val registeredServices = mutableMapOf<String, RuntimeService>()

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

        registry.startAll()

        started = true
    }

    fun stop() {

        if (!started) {
            return
        }

        registry.stopAll()

        registry = RuntimeServiceRegistry()

        started = false
    }
    
    fun getStates(): Map<String, RuntimeServiceState> {
        return registry.getStates()
    }

}
