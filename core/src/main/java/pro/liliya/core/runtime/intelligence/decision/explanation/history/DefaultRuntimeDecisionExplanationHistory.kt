package pro.liliya.core.runtime.intelligence.decision.explanation.history

import java.util.ArrayDeque
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation

class DefaultRuntimeDecisionExplanationHistory(
    private val capacity: Int = DEFAULT_CAPACITY,
    private val clock: () -> Long = System::currentTimeMillis
) : RuntimeDecisionExplanationHistory {

    private val records =
        ArrayDeque<RuntimeDecisionExplanationRecord>()

    init {
        require(capacity > 0) {
            "Decision explanation history capacity must be positive"
        }
    }

    override fun record(
        explanation: RuntimeDecisionExplanation
    ): RuntimeDecisionExplanationRecord {

        return synchronized(records) {
            while (records.size >= capacity) {
                records.removeFirst()
            }

            val record =
                RuntimeDecisionExplanationRecord(
                    explanation = explanation,
                    recordedAt = clock()
                )

            records.addLast(record)

            record
        }
    }

    override fun records():
        List<RuntimeDecisionExplanationRecord> {

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
