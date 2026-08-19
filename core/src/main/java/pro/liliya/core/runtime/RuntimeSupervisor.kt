package pro.liliya.core.runtime

class RuntimeSupervisor(
    private val registryProvider: () -> RuntimeServiceRegistry,
    private val policy: RuntimeRecoveryPolicy = RuntimeRecoveryPolicy()
) {

    private val restartCounters = mutableMapOf<String, Int>()

    private fun registry(): RuntimeServiceRegistry {
        return registryProvider()
    }

    fun start() {
        registry().startAll()
    }

    fun stop() {
        registry().stopAll()
    }

    fun recover(serviceName: String): Boolean {

        println("SUPERVISOR RECOVER REQUEST = $serviceName")
        println("SUPERVISOR BEFORE COUNTS = $restartCounters")

        val currentRetries =
            restartCounters[serviceName] ?: 0

        if (currentRetries >= policy.maxRetries) {
            return false
        }

        restartCounters[serviceName] =
            currentRetries + 1

        return try {
            registry().restart(serviceName)
            println("SUPERVISOR RECOVER SUCCESS = $serviceName")
            true
        } catch (error: Exception) {
            throw IllegalStateException(
                "RECOVERY FAILED FOR $serviceName: ${error.message}",
                error
            )
        }
    }

    fun getRestartCount(
        serviceName: String
    ): Int {
        return restartCounters[serviceName] ?: 0
    }

    fun getRestartCounts(): Map<String, Int> {
        return restartCounters.toMap()
    }

    fun reset() {
        restartCounters.clear()
    }
}
