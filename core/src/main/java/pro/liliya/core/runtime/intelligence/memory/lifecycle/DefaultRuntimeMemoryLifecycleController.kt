package pro.liliya.core.runtime.intelligence.memory.lifecycle

class DefaultRuntimeMemoryLifecycleController :
    RuntimeMemoryLifecycleController {

    private var started = false

    override fun start() {
        started = true
    }

    override fun stop() {
        started = false
    }

    override fun isStarted(): Boolean {
        return started
    }

    override fun reset() {
        started = false
    }
}
