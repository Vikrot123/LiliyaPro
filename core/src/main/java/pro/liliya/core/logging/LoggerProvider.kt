package pro.liliya.core.logging


object LoggerProvider {


    @Volatile
    private var writer: FileLogWriter? = null


    fun initialize(

        fileWriter: FileLogWriter

    ) {

        writer = fileWriter

    }


    fun get(

        context: LoggerContext

    ): Logger {


        val activeWriter =

            writer

                ?: throw IllegalStateException(

                    "LiliyaLogger is not initialized"

                )


        return LiliyaLogger(

            context,

            activeWriter

        )

    }

}
