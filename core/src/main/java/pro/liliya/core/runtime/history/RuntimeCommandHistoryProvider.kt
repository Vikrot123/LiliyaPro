package pro.liliya.core.runtime.history

class RuntimeCommandHistoryProvider {

    private val history =
        RuntimeCommandHistory()

    fun record(
        record: RuntimeCommandRecord
    ) {
        history.record(record)
    }

    fun snapshot(): List<RuntimeCommandRecord> {
        return history.getAll()
    }

    fun last(): RuntimeCommandRecord? {
        return history.last()
    }

    fun clear() {
        history.clear()
    }
}
