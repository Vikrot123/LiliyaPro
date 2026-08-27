package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession

import java.util.ArrayDeque
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeSupersessionHistory(
    private val capacity: Int = DEFAULT_CAPACITY,
    private val clock: () -> Long =
        System::currentTimeMillis
) : RuntimeKnowledgeSupersessionHistory {

    private val records =
        ArrayDeque<RuntimeKnowledgeSupersessionRecord>()

    init {
        require(capacity > 0) {
            "Knowledge supersession history capacity must be positive"
        }
    }

    override fun record(
        previousKnowledge: RuntimeKnowledge,
        replacementKnowledge: RuntimeKnowledge
    ): RuntimeKnowledgeSupersessionRecord {

        return synchronized(records) {
            while (records.size >= capacity) {
                records.removeFirst()
            }

            val record =
                RuntimeKnowledgeSupersessionRecord(
                    previousKnowledge =
                        previousKnowledge,
                    replacementKnowledge =
                        replacementKnowledge,
                    recordedAt = clock()
                )

            records.addLast(record)

            record
        }
    }

    override fun records():
        List<RuntimeKnowledgeSupersessionRecord> {

        return synchronized(records) {
            records.toList()
        }
    }

    override fun replacementsOf(
        knowledge: RuntimeKnowledge
    ): List<RuntimeKnowledgeSupersessionRecord> {

        return synchronized(records) {
            records.filter {
                it.previousKnowledge == knowledge
            }
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
