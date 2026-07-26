package pro.liliya.model.local


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pro.liliya.domain.api.ModelEngine
import pro.liliya.domain.models.ContextSnapshot


class QwenModelEngine(
    private val modelPath: String
) : ModelEngine {


    private var loaded = false


    override suspend fun loadModel(): Boolean {

        return try {

            LlamaCppJni.llama_init(
                modelPath,
                false,
                true
            )

            loaded = true

            true


        } catch (e: Exception) {

            loaded = false

            false
        }
    }



    override suspend fun unloadModel() {

        loaded = false
    }



    override fun isModelLoaded(): Boolean {

        return loaded
    }



    override fun streamInference(
        snapshot: ContextSnapshot
    ): Flow<String> = flow {


        if (!loaded) {

            emit(
                "Qwen model not loaded"
            )

            return@flow
        }


        emit(
            "Liliya local AI thinking..."
        )


        val result =
            LlamaCppJni.llama_infer(
                modelPath,
                snapshot.userInput,
                "You are Liliya, an AI assistant.",
                "",
                "",
                "",
                4,
                0.7f,
                0.9f,
                40f,
                1.1f,
                256,
                0
            )


        emit(result)

    }
}
