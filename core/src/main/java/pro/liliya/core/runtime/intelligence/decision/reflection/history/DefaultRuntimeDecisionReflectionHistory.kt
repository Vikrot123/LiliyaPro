package pro.liliya.core.runtime.intelligence.decision.reflection.history

import java.util.ArrayDeque
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight

class DefaultRuntimeDecisionReflectionHistory(
    private val capacity: Int = DEFAULT_CAPACITY,
    private val clock: () -> Long =
        System::currentTimeMillis
) : RuntimeDecisionReflectionHistory {

    private val records =
        ArrayDeque<RuntimeDecisionReflectionRecord>()

    init {
        require(capacity > 0) {
            "Decision reflection history capacity must be positive"
        }
    }

    override fun record(
        insight: RuntimeDecisionReflectionInsight
    ): RuntimeDecisionReflectionRecord {

        return synchronized(records) {
            while (records.size >= capacity) {
                records.removeFirst()
            }

            val record =
                RuntimeDecisionReflectionRecord(
                    insight = insight,
                    recordedAt = clock()
                )

            records.addLast(record)

            record
        }
    }

    override fun records():
        List<RuntimeDecisionReflectionRecord> {

        return synchronized(records) {
            records.toList()
        }
    }

    override fun clear() {
        synchronized(records) {
            records.clear()
        }
    }

    companion object {
        const val DEFAULT_CAPACITY = 64
    }
}
