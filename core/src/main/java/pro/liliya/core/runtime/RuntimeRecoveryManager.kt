package pro.liliya.core.runtime

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeRecoveryManager(
    private val supervisor: RuntimeSupervisor
) {

    private var installed = false

    private var lastRecoveredService: String? = null
    private var lastRecoverySuccessful: Boolean? = null

    private val eventListener: (RuntimeEvent) -> Unit = { event ->

        if (event is RuntimeEvent.RuntimeServiceFailed) {

            val recovered =
                supervisor.recover(event.serviceName)

            lastRecoveredService =
                event.serviceName

            lastRecoverySuccessful =
                recovered
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

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Started(serviceName)
        )

        return try {

            val recovered = supervisor.recover(serviceName)

            lastRecoveredService = serviceName
            lastRecoverySuccessful = recovered

            if (recovered) {
                RuntimeRecoveryEventBus.publish(
                    RuntimeRecoveryEvent.Completed(serviceName)
                )
            } else {
                RuntimeRecoveryEventBus.publish(
                    RuntimeRecoveryEvent.Failed(serviceName)
                )
            }

            recovered

        } catch (error: Throwable) {

            lastRecoveredService = serviceName
            lastRecoverySuccessful = false

            RuntimeRecoveryEventBus.publish(
                RuntimeRecoveryEvent.Failed(serviceName)
            )

            false
        }
    }


    fun reset() {

        uninstall()

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
