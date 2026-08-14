package pro.liliya.core.runtime.lifecycle

class RuntimeLifecycleRecorderHolder(
    private val recorder: RuntimeLifecycleRecorder
) {

    fun get(): RuntimeLifecycleRecorder {
        return recorder
    }
}
