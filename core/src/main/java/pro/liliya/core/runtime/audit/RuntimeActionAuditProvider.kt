package pro.liliya.core.runtime.audit

class RuntimeActionAuditProvider {

    private val records =
        mutableListOf<RuntimeActionAuditRecord>()

    fun record(
        record: RuntimeActionAuditRecord
    ) {
        records.add(record)
    }

    fun snapshot(): List<RuntimeActionAuditRecord> {
        return records.toList()
    }

    fun clear() {
        records.clear()
    }
}
