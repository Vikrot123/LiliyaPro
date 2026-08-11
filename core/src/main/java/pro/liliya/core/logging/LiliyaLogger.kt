package pro.liliya.core.logging


class LiliyaLogger(

    private val context: LoggerContext,

    private val writer: FileLogWriter

) : Logger {


    override fun debug(

        marker: String,

        message: String

    ) {

        write(

            LogLevel.DEBUG,

            marker,

            message

        )

    }


    override fun info(

        marker: String,

        message: String

    ) {

        write(

            LogLevel.INFO,

            marker,

            message

        )

    }


    override fun warn(

        marker: String,

        message: String

    ) {

        write(

            LogLevel.WARN,

            marker,

            message

        )

    }


    override fun error(

        marker: String,

        message: String,

        throwable: Throwable?

    ) {

        write(

            LogLevel.ERROR,

            marker,

            message +
                (throwable?.message ?: "")

        )

    }


    private fun write(

        level: LogLevel,

        marker: String,

        message: String

    ) {

        writer.write(

            LogEvent(

                level = level,

                tag =
                    "${context.module}:" +
                    "${context.component}:" +
                    "${context.method}",

                marker = marker,

                message = message

            )

        )

    }

}
