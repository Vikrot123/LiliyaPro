package pro.liliya.core.runtime

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeRecoveryManager(
    private val supervisor: RuntimeSupervisor
) {

    private var installed = false

    private var lastRecoveredService: String? = null
    private var lastRecoverySuccessful: Boolean? = null

    fun install() {
        if (installed) {
            return
        }

        RuntimeEventBus.subscribe { event ->

            if (event is RuntimeEvent.RuntimeServiceFailed) {

                val recovered =
                    supervisor.recover(event.serviceName)

                lastRecoveredService =
                    event.serviceName

                lastRecoverySuccessful =
                    recovered
            }
        }

        installed = true
    }

    fun snapshot(): RuntimeRecoverySnapshot {

        return RuntimeRecoverySnapshot(
            restartCounts = supervisor.getRestartCounts(),
            lastRecoveredService = lastRecoveredService,
            lastRecoverySuccessful = lastRecoverySuccessful
        )
    }
}
