package pro.liliya.core

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.logging.LogInitializer
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextFailureLoggingContractTest {

    private fun logFile(): File {
        return File.createTempFile(
            "liliya-cognitive-",
            ".log"
        ).also {
            it.writeText("")
            LogInitializer.initialize(it)
        }
    }

    @Test
    fun composition_source_failure_is_logged_and_isolated() {
        val file = logFile()

        val composition =
            DefaultCognitiveContextComposition()

        composition.service().registerSource(
            object : CognitiveContextSource {
                override fun snapshot(
                    type: CognitiveContextType
                ): CognitiveContextSnapshot {
                    throw IllegalStateException(
                        "composition-source-boom"
                    )
                }
            }
        )

        composition.service().registerSource(
            object : CognitiveContextSource {
                override fun snapshot(
                    type: CognitiveContextType
                ): CognitiveContextSnapshot {
                    return CognitiveContextSnapshot(
                        type = type,
                        values = mapOf(
                            "healthy" to "available"
                        )
                    )
                }
            }
        )

        val result =
            composition.context().snapshot(
                CognitiveContextType.WORKING
            )

        assertEquals(
            "available",
            result.values["healthy"]
        )

        val log = file.readText()

        assertTrue(
            log.contains(
                "Cognitive context source failed"
            )
        )

        assertTrue(
            log.contains(
                "composition-source-boom"
            )
        )
    }

    @Test
    fun builder_source_failure_is_logged_and_isolated() {
        val file = logFile()

        val builder =
            DefaultCognitiveContextBuilder()

        val result =
            builder.build(
                type = CognitiveContextType.TASK,
                sources = listOf(
                    object : CognitiveContextSource {
                        override fun snapshot(
                            type: CognitiveContextType
                        ): CognitiveContextSnapshot {
                            throw IllegalStateException(
                                "builder-source-boom"
                            )
                        }
                    },
                    object : CognitiveContextSource {
                        override fun snapshot(
                            type: CognitiveContextType
                        ): CognitiveContextSnapshot {
                            return CognitiveContextSnapshot(
                                type = type,
                                values = mapOf(
                                    "payload" to "healthy"
                                )
                            )
                        }
                    }
                )
            )

        assertEquals(
            listOf("source_1"),
            result.values.keys.toList()
        )

        val log = file.readText()

        assertTrue(
            log.contains(
                "Cognitive context builder source failed"
            )
        )

        assertTrue(
            log.contains(
                "builder-source-boom"
            )
        )
    }
}
