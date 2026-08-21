package pro.liliya.core.runtime.recovery

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeRecoveryEventBridge(
    private val recoveryEventBus: RuntimeRecoveryEventBus = RuntimeRecoveryEventBus()
) {

    private var installed = false

    private val listener: (RuntimeRecoveryEvent) -> Unit = { event ->
        when (event) {
            is RuntimeRecoveryEvent.Started -> {
                // Recovery has started; the service is not recovered yet.
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
        if (installed) {
            return
        }

        recoveryEventBus.subscribe(listener)
        installed = true
    }

    fun uninstall() {
        if (!installed) {
            return
        }

        recoveryEventBus.unsubscribe(listener)
        installed = false
    }
}
