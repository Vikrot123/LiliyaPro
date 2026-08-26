package pro.liliya.core

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.logging.LogInitializer

class RuntimeEventBusLoggingContractTest {

    @Test
    fun failing_listener_is_isolated_and_logged() {
        val logFile =
            File.createTempFile(
                "liliya-event-bus-",
                ".log"
            )

        logFile.writeText("")

        LogInitializer.initialize(logFile)

        RuntimeEventBus.clear()

        val failingListener:
            (RuntimeEvent) -> Unit = {
                throw IllegalStateException(
                    "listener-boom"
                )
            }

        RuntimeEventBus.subscribe(
            failingListener
        )

        try {
            RuntimeEventBus.publish(
                RuntimeEvent.SystemStart
            )
        } finally {
            RuntimeEventBus.unsubscribe(
                failingListener
            )
            RuntimeEventBus.clear()
        }

        val log = logFile.readText()

        assertTrue(
            log.contains(
                "Runtime event listener failed"
            )
        )

        assertTrue(
            log.contains(
                "listener-boom"
            )
        )
    }
}
