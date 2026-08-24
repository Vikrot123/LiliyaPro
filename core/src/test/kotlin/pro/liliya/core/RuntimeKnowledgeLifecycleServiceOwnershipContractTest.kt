package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.holder.DefaultRuntimeKnowledgeLifecycleServiceHolder

class RuntimeKnowledgeLifecycleServiceOwnershipContractTest {

    @Test
    fun holder_owns_single_lifecycle_service_instance() {

        val service =
            object : RuntimeKnowledgeLifecycleService {

                override fun processKnowledge(
                    knowledge: pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
                ):
                    pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult {
                    error("not used")
                }
            }

        val holder =
            DefaultRuntimeKnowledgeLifecycleServiceHolder {
                service
            }

        val first = holder.service()
        val second = holder.service()

        assertSame(
            first,
            second
        )
    }
}
