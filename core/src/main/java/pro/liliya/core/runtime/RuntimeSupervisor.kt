package pro.liliya.core.runtime

private fun runtimeSupervisorLogger() =
    pro.liliya.core.logging.LoggerFactory.create(
        module = "CORE",
        component = "RuntimeSupervisor",
        method = "recover"
    )

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

        runtimeSupervisorLogger().debug(
            pro.liliya.core.logging.LoggerMarkers.METHOD_ENTER,
            "SUPERVISOR RECOVER REQUEST = $serviceName"
        )
        runtimeSupervisorLogger().debug(
            pro.liliya.core.logging.LoggerMarkers.DATA_RECEIVED,
            "SUPERVISOR BEFORE COUNTS = $restartCounters"
        )

        val currentRetries =
            restartCounters[serviceName] ?: 0

        if (currentRetries >= policy.maxRetries) {
            return false
        }

        restartCounters[serviceName] =
            currentRetries + 1

        return try {
            registry().restart(serviceName)
            runtimeSupervisorLogger().info(
                pro.liliya.core.logging.LoggerMarkers.STATE_CHANGED,
                "SUPERVISOR RECOVER SUCCESS = $serviceName"
            )
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
