package pro.liliya.core.runtime.intelligence.memory.lifecycle

interface RuntimeMemoryLifecycleController {

    fun start()

    fun stop()

    fun isStarted(): Boolean

    fun reset()
}
