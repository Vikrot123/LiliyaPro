package pro.liliya.core.runtime.history

class RuntimeCommandHistory {

    private val records =
        mutableListOf<RuntimeCommandRecord>()

    fun record(
        record: RuntimeCommandRecord
    ) {
        records.add(record)
    }

    fun getAll(): List<RuntimeCommandRecord> {
        return records.toList()
    }

    fun last(): RuntimeCommandRecord? {
        return records.lastOrNull()
    }

    fun clear() {
        records.clear()
    }
}
