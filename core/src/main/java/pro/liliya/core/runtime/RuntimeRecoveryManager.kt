package pro.liliya.core.runtime

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

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
        val recovered = supervisor.recover(serviceName)

        lastRecoveredService = serviceName
        lastRecoverySuccessful = recovered

        return recovered
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
