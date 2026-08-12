package pro.liliya.core.runtime

class RuntimeServiceRegistry {

    private val services = mutableMapOf<String, RuntimeService>()

    fun register(service: RuntimeService) {

        if (services.containsKey(service.name)) {
            throw IllegalStateException(
                "Runtime service already registered: ${service.name}"
            )
        }

        services[service.name] = service
    }

    private var terminated = false

    fun startAll() {

        if (terminated) {
            throw IllegalStateException(
                "Runtime service registry is terminated"
            )
        }

        services.values.forEach { service ->
            service.start()
        }
    }

    fun stopAll() {

        if (terminated) {
            return
        }

        services.values.forEach { service ->
            service.stop()
        }

        terminated = true
    }

    fun getStates(): Map<String, RuntimeServiceState> {

        return services.mapValues { (_, service) ->
            service.state
        }
    }
}
