package pro.liliya.core.runtime.lifecycle

class DefaultRuntimeLifecycleRecorder : RuntimeLifecycleRecorder {

    private val history = mutableListOf<RuntimeLifecycleRecord>()

    override fun record(
        event: RuntimeLifecycleEvent,
        reason: String?
    ) {
        history += RuntimeLifecycleRecord(
            event = event,
            reason = reason
        )
    }

    override fun records(): List<RuntimeLifecycleRecord> {
        return history.toList()
    }

    override fun last(): RuntimeLifecycleRecord? {
        return history.lastOrNull()
    }
}
