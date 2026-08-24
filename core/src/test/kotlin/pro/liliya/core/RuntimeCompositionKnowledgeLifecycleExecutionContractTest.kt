package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleProcessingStatus

class RuntimeCompositionKnowledgeLifecycleExecutionContractTest {

    @Test
    fun composition_knowledge_service_processes_knowledge() {
        val composition =
            DefaultRuntimeComposition()

        val knowledge =
            RuntimeKnowledge(
                statement = "test knowledge",
                confidence = 0.8,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = System.currentTimeMillis()
            )

        val result =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleService()
                .processKnowledge(knowledge)

        assertNotNull(result)
        assertNotEquals(
            RuntimeKnowledgeLifecycleProcessingStatus.FAILED,
            result.status
        )
    }
}
