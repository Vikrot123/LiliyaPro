package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity

interface RuntimeKnowledgeSupersessionIntegrityChecker {

    fun check():
        RuntimeKnowledgeSupersessionIntegrityReport
}
