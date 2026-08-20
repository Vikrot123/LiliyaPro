package pro.liliya.core.runtime.recovery

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeRecoveryEventBridge {

    private val listener: (RuntimeRecoveryEvent) -> Unit = { event ->
        when (event) {
            is RuntimeRecoveryEvent.Started -> {
                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceRecovered(
                        event.serviceName
                    )
                )
            }

            is RuntimeRecoveryEvent.Completed -> {
                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceRecovered(
                        event.serviceName
                    )
                )
            }

            is RuntimeRecoveryEvent.Failed -> {
                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceFailed(
                        serviceName = event.serviceName,
                        reason = "Runtime recovery failed"
                    )
                )
            }
        }
    }

    fun install() {
        RuntimeRecoveryEventBus.subscribe(listener)
    }

    fun uninstall() {
        RuntimeRecoveryEventBus.unsubscribe(listener)
    }
}
