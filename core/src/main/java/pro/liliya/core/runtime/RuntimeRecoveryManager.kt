package pro.liliya.core.runtime

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeRecoveryManager(
    private val supervisor: RuntimeSupervisor,
    private val registry: RuntimeServiceRegistry? = null,
    private val recoveryEventBus: RuntimeRecoveryEventBus = RuntimeRecoveryEventBus()
) {

    private var installed = false

    private var lastRecoveredService: String? = null
    private var lastRecoverySuccessful: Boolean? = null

    private companion object {
    val installedOwners = mutableSetOf<RuntimeServiceRegistry?>()
}

    private val eventListener: (RuntimeEvent) -> Unit = { event ->        if (event is RuntimeEvent.RuntimeServiceFailed) {
            val sourceRegistry = event.sourceRegistry

            if (
                sourceRegistry == null ||
                registry == null ||
                sourceRegistry === registry
            ) {
                val acquired = registry?.tryAcquireRecovery(event.serviceName) != false

                if (acquired) {
                    try {
                        val recovered = supervisor.recover(event.serviceName)

                        lastRecoveredService = event.serviceName
                        lastRecoverySuccessful = recovered
                    } catch (error: Throwable) {
                        lastRecoveredService = event.serviceName
                        lastRecoverySuccessful = false
                    } finally {
                        registry?.releaseRecovery(event.serviceName)
                    }
                }

                
            }
        }
    }


    fun install() {

        if (installed) {
            return
        }

        synchronized(installedOwners) {
            if (!installedOwners.add(registry)) {
                return
            }
        }

        RuntimeEventBus.subscribe(eventListener)

        installed = true
    }


    fun uninstall() {

        if (!installed) {
            return
        }

        RuntimeEventBus.unsubscribe(eventListener)

        synchronized(installedOwners) {
            installedOwners.remove(registry)
        }

        installed = false
    }


    fun recover(serviceName: String): Boolean {

        recoveryEventBus.publish(
            RuntimeRecoveryEvent.Started(serviceName)
        )

        return try {

            val recovered = supervisor.recover(serviceName)

            lastRecoveredService = serviceName
            lastRecoverySuccessful = recovered

            if (recovered) {
                recoveryEventBus.publish(
                    RuntimeRecoveryEvent.Completed(serviceName)
                )
            } else {
                recoveryEventBus.publish(
                    RuntimeRecoveryEvent.Failed(serviceName)
                )
            }

            recovered

        } catch (error: Throwable) {

            lastRecoveredService = serviceName
            lastRecoverySuccessful = false

            recoveryEventBus.publish(
                RuntimeRecoveryEvent.Failed(serviceName)
            )

            false
        }
    }


    fun reset() {
        uninstall()

        synchronized(installedOwners) {
            installedOwners.remove(registry)
        }

        lastRecoveredService = null
        lastRecoverySuccessful = null
    }


    fun snapshot(): RuntimeRecoverySnapshot {

        return RuntimeRecoverySnapshot(
            restartCounts = supervisor.getRestartCounts(),
            lastRecoveredService = lastRecoveredService,
            lastRecoverySuccessful = lastRecoverySuccessful
        )
    }
}
