package pro.liliya.core

import kotlinx.coroutines.runBlocking
import pro.liliya.core.orchestration.ExecutiveControllerImpl
import pro.liliya.model.mock.MockModelEngine
import kotlin.test.Test
import kotlin.test.assertTrue

class LiliyaProTest {

    @Test
    fun `liliya pro should respond through model engine`() = runBlocking {

        val modelEngine = MockModelEngine()

        modelEngine.loadModel()

        val controller = ExecutiveControllerImpl(
            modelEngine = modelEngine
        )

        val responses = mutableListOf<String>()

        controller.processInput(
            "Привет, LiliyaPro"
        ).collect { response ->
            responses.add(response)
        }

        assertTrue(
            responses.isNotEmpty()
        )

        println("LiliyaPro response:")
        responses.forEach {
            println(it)
        }
    }
}
