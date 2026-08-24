package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.holder.DefaultRuntimeKnowledgeLifecycleServiceHolder

class DefaultRuntimeKnowledgeLifecycleServiceHolderContractTest {

    @Test
    fun holder_returns_same_service_instance() {

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

        assertSame(
            service,
            holder.service()
        )

        assertSame(
            service,
            holder.service()
        )
    }

    @Test
    fun holder_reset_creates_new_service_instance() {

        var counter = 0

        val holder =
            DefaultRuntimeKnowledgeLifecycleServiceHolder {
                counter++

                object : RuntimeKnowledgeLifecycleService {
                    override fun processKnowledge(
                        knowledge: pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
                    ):
                        pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult {
                        error("not used")
                    }
                }
            }

        val first = holder.service()

        holder.reset()

        val second = holder.service()

        assertNotSame(
            first,
            second
        )
    }
}
