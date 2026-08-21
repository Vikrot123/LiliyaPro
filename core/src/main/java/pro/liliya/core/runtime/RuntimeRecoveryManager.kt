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

    private val recoveringServices = mutableSetOf<String>()

    private val eventListener: (RuntimeEvent) -> Unit = { event ->        if (event is RuntimeEvent.RuntimeServiceFailed) {
            val sourceRegistry = event.sourceRegistry

            if (
                sourceRegistry == null ||
                registry == null ||
                sourceRegistry === registry
            ) {
                try {
                    if (recoveringServices.add(event.serviceName)) {
                    try {
                        val recovered = supervisor.recover(event.serviceName)

                        lastRecoveredService = event.serviceName
                        lastRecoverySuccessful = recovered
                    } finally {
                        recoveringServices.remove(event.serviceName)
                    }
                }

                } catch (error: Throwable) {

                    lastRecoveredService =
                        event.serviceName

                    lastRecoverySuccessful =
                        false
                }
            }
        }
    }


    fun install() {

        if (installed) {
            return
        }

        RuntimeEventBus.subscribe(eventListener)

        installed = true
    }


    fun uninstall() {

        if (!installed) {
            return
        }

        RuntimeEventBus.unsubscribe(eventListener)

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

        recoveringServices.clear()

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
