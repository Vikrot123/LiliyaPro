package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance.DefaultRuntimeKnowledgeMaintenanceTrigger
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance.RuntimeKnowledgeMaintenanceReport
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance.RuntimeKnowledgeMaintenanceService

class DefaultRuntimeKnowledgeMaintenanceTriggerContractTest {

    @Test
    fun cycle_without_new_knowledge_does_not_run_maintenance() {
        val service =
            RecordingMaintenanceService()

        val trigger =
            DefaultRuntimeKnowledgeMaintenanceTrigger(
                service
            )

        val result =
            trigger.afterKnowledgeProduction(
                produced = false
            )

        assertNull(result)
        assertEquals(0, service.calls)
    }

    @Test
    fun knowledge_production_runs_exactly_one_maintenance_pass() {
        val service =
            RecordingMaintenanceService()

        val trigger =
            DefaultRuntimeKnowledgeMaintenanceTrigger(
                service
            )

        val result =
            trigger.afterKnowledgeProduction(
                produced = true
            )

        assertSame(
            service.report,
            result
        )

        assertEquals(
            1,
            service.calls
        )
    }

    private class RecordingMaintenanceService :
        RuntimeKnowledgeMaintenanceService {

        var calls = 0

        val report =
            RuntimeKnowledgeMaintenanceReport(
                outcomes = emptyList()
            )

        override fun maintain():
            RuntimeKnowledgeMaintenanceReport {

            calls += 1

            return report
        }
    }
}
