package pro.liliya.core.runtime.orchestration

interface RuntimeBridgeController {
    fun startRuntimeBridges()
    fun stopRuntimeBridges()

    fun install()

    fun uninstall()

    fun stop()
}
