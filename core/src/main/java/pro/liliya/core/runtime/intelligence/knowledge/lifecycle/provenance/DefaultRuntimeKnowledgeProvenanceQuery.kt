package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.provenance

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.RuntimeKnowledgeLifecycleHistoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query.RuntimeKnowledgeSupersessionQuery

class DefaultRuntimeKnowledgeProvenanceQuery(
    private val supersessionQuery:
        RuntimeKnowledgeSupersessionQuery,
    private val lifecycleHistoryQuery:
        RuntimeKnowledgeLifecycleHistoryQuery
) : RuntimeKnowledgeProvenanceQuery {

    override fun provenance(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeProvenance {

        val trace =
            supersessionQuery.trace(
                knowledge
            )

        val steps =
            trace.chain.map { version ->
                RuntimeKnowledgeProvenanceStep(
                    knowledge = version,
                    source = version.source,
                    lifecycleHistory =
                        lifecycleHistoryQuery.history(
                            version
                        )
                )
            }

        return RuntimeKnowledgeProvenance(
            startingKnowledge =
                trace.startingKnowledge,
            currentKnowledge =
                trace.currentKnowledge,
            chain =
                trace.chain,
            steps =
                steps,
            cycleDetected =
                trace.cycleDetected
        )
    }
}
