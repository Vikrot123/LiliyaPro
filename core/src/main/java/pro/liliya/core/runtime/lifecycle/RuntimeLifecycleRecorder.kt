package pro.liliya.core.runtime.lifecycle

interface RuntimeLifecycleRecorder {

    fun record(
        event: RuntimeLifecycleEvent,
        reason: String? = null
    )

    fun records(): List<RuntimeLifecycleRecord>

    fun last(): RuntimeLifecycleRecord?
}
