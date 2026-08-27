package pro.liliya.core.runtime.intelligence.decision.quality.governance.history

import java.util.ArrayDeque
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceAssessment

class DefaultRuntimeDecisionQualityGovernanceHistory(
    private val capacity: Int = DEFAULT_CAPACITY,
    private val clock: () -> Long =
        System::currentTimeMillis
) : RuntimeDecisionQualityGovernanceHistory {

    private val records =
        ArrayDeque<RuntimeDecisionQualityGovernanceRecord>()

    init {
        require(capacity > 0) {
            "Decision quality governance history capacity must be positive"
        }
    }

    override fun record(
        assessment: RuntimeDecisionQualityGovernanceAssessment
    ): RuntimeDecisionQualityGovernanceRecord {

        return synchronized(records) {
            while (records.size >= capacity) {
                records.removeFirst()
            }

            val record =
                RuntimeDecisionQualityGovernanceRecord(
                    assessment = assessment,
                    recordedAt = clock()
                )

            records.addLast(record)

            record
        }
    }

    override fun records():
        List<RuntimeDecisionQualityGovernanceRecord> {

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
