package pro.liliya.core.runtime.intelligence.decision.quality.advisory.history

import java.util.ArrayDeque
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory

class DefaultRuntimeDecisionQualityAdvisoryHistory(
    private val capacity: Int = DEFAULT_CAPACITY,
    private val clock: () -> Long =
        System::currentTimeMillis
) : RuntimeDecisionQualityAdvisoryHistory {

    private val records =
        ArrayDeque<RuntimeDecisionQualityAdvisoryRecord>()

    init {
        require(capacity > 0) {
            "Decision quality advisory history capacity must be positive"
        }
    }

    override fun record(
        advisory: RuntimeDecisionQualityAdvisory
    ): RuntimeDecisionQualityAdvisoryRecord {

        return synchronized(records) {
            while (records.size >= capacity) {
                records.removeFirst()
            }

            val record =
                RuntimeDecisionQualityAdvisoryRecord(
                    advisory = advisory,
                    recordedAt = clock()
                )

            records.addLast(record)

            record
        }
    }

    override fun records():
        List<RuntimeDecisionQualityAdvisoryRecord> {

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
