package pro.liliya.core.runtime.intelligence.decision.quality.history

import java.util.ArrayDeque
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment

class DefaultRuntimeDecisionQualityHistory(
    private val capacity: Int = DEFAULT_CAPACITY,
    private val clock: () -> Long =
        System::currentTimeMillis
) : RuntimeDecisionQualityHistory {

    private val records =
        ArrayDeque<RuntimeDecisionQualityRecord>()

    init {
        require(capacity > 0) {
            "Decision quality history capacity must be positive"
        }
    }

    override fun record(
        assessment: RuntimeDecisionQualityAssessment
    ): RuntimeDecisionQualityRecord {

        return synchronized(records) {
            while (records.size >= capacity) {
                records.removeFirst()
            }

            val record =
                RuntimeDecisionQualityRecord(
                    assessment = assessment,
                    recordedAt = clock()
                )

            records.addLast(record)

            record
        }
    }

    override fun records():
        List<RuntimeDecisionQualityRecord> {

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
