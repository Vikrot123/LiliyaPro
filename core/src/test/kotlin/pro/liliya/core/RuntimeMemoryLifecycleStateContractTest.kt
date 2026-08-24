package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.memory.lifecycle.DefaultRuntimeMemoryLifecycleController

class RuntimeMemoryLifecycleStateContractTest {

    @Test
    fun start_should_mark_controller_started() {

        val controller =
            DefaultRuntimeMemoryLifecycleController()

        controller.start()

        assertTrue(
            controller.isStarted()
        )
    }


    @Test
    fun stop_should_mark_controller_stopped() {

        val controller =
            DefaultRuntimeMemoryLifecycleController()

        controller.start()
        controller.stop()

        assertFalse(
            controller.isStarted()
        )
    }


    @Test
    fun reset_should_restore_initial_state() {

        val controller =
            DefaultRuntimeMemoryLifecycleController()

        controller.start()
        controller.reset()

        assertFalse(
            controller.isStarted()
        )
    }


    @Test
    fun repeated_lifecycle_operations_should_be_safe() {

        val controller =
            DefaultRuntimeMemoryLifecycleController()

        controller.start()
        controller.start()

        assertTrue(
            controller.isStarted()
        )

        controller.stop()
        controller.stop()

        assertFalse(
            controller.isStarted()
        )
    }
}
