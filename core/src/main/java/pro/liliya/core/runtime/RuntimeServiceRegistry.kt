package pro.liliya.core.runtime

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeServiceRegistry {

    private val services = mutableMapOf<String, RuntimeService>()

    private val failures = mutableListOf<RuntimeServiceFailure>()

    private val serviceFailures =
        mutableMapOf<String, RuntimeServiceFailure>()

    private var terminated = false


    fun register(service: RuntimeService) {
        if (services.containsKey(service.name)) {
            throw IllegalStateException(
                "Runtime service already registered: ${service.name}"
            )
        }

        services[service.name] = service
    }


    fun startAll() {
        if (terminated) {
            throw IllegalStateException(
                "Runtime service registry is terminated"
            )
        }

        services.values.forEach { service ->

            try {
                service.start()

            } catch (error: Exception) {

                val failure = RuntimeServiceFailure(
                    serviceName = service.name,
                    reason = error.message ?: "unknown"
                )

                failures.add(failure)
                serviceFailures[service.name] = failure

                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceFailed(
                        serviceName = service.name,
                        reason = error.message ?: "unknown"
                    )
                )
            }
        }
    }


    fun stopAll() {

        if (terminated) {
            return
        }

        services.values.forEach { service ->

            try {
                service.stop()

            } catch (error: Exception) {

                val failure = RuntimeServiceFailure(
                    serviceName = service.name,
                    reason = error.message ?: "unknown"
                )

                failures.add(failure)
                serviceFailures[service.name] = failure

                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceFailed(
                        serviceName = service.name,
                        reason = error.message ?: "unknown"
                    )
                )
            }
        }

        terminated = true
    }


    fun getStates(): Map<String, RuntimeServiceState> {

        return services.mapValues { (_, service) ->
            service.state
        }
    }


    fun getFailures(): List<RuntimeServiceFailure> {

        return failures.toList()
    }

    fun getHealth(): Map<String, RuntimeServiceHealth> {

        return services.mapValues { (_, service) ->

            RuntimeServiceHealth(
                name = service.name,
                state = service.state,
                healthy = service.state == RuntimeServiceState.RUNNING &&
                    !serviceFailures.containsKey(service.name),
                lastFailure = serviceFailures[service.name]
            )

        }

    }


    fun reset() {
        services.clear()
        failures.clear()
        serviceFailures.clear()
        terminated = false
    }

    fun restart(serviceName: String) {
        println("RECOVERY RESTART REQUEST: $serviceName")
        println("RECOVERY REGISTERED SERVICES: ${services.keys}")


        val service = services[serviceName]
            ?: throw IllegalArgumentException(
                "Runtime service not found: $serviceName, available=${services.keys}"
            )

        service.stop()

        try {
            service.start()
        } catch (error: Exception) {

            val failure = RuntimeServiceFailure(
                serviceName = service.name,
                reason = error.message ?: "unknown"
            )

            failures.add(failure)
            serviceFailures[service.name] = failure

            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeServiceFailed(
                    serviceName = service.name,
                    reason = error.message ?: "unknown"
                )
            )

            throw error
        }
    }

}
