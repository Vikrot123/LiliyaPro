package pro.liliya.core.runtime

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeRecoveryManager(
    private val supervisor: RuntimeSupervisor
) {

    private var installed = false

    fun install() {
        if (installed) {
            return
        }

        RuntimeEventBus.subscribe { event ->

            if (event is RuntimeEvent.RuntimeServiceFailed) {

                supervisor.recover(
                    event.serviceName
                )
            }
        }

        installed = true
    }
}
