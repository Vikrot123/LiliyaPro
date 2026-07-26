package pro.liliya.model.local


object LlamaCppJni {


    init {
        System.loadLibrary("llama-jni")
    }


    external fun llama_init(
        modelPath: String,
        useGpu: Boolean,
        useMmap: Boolean
    )


    external fun llama_infer(
        modelPath: String,
        prompt: String,
        systemPrompt: String,
        chatTemplate: String,
        stop1: String,
        stop2: String,
        nThreads: Int,
        temperature: Float,
        topP: Float,
        topK: Float,
        repeatPenalty: Float,
        maxTokens: Int,
        seed: Int
    ): String

}
