package pro.liliya.app

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pro.liliya.app.application.AppContainer


class MainActivity : Activity() {


    private lateinit var chatBox: TextView

    private lateinit var input: EditText

    private lateinit var scrollView: ScrollView


    /**
     * ChatController берётся из AppContainer.
     * Activity больше не создаёт Runtime-сервисы сама.
     */
    private val controller =
        AppContainer.chatController


    private val scope =
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.Main
        )


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        val root =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setBackgroundColor(
                    Color.WHITE
                )

                setPadding(
                    32,
                    48,
                    32,
                    32
                )
            }


        val title =
            TextView(this).apply {

                text =
                    "LiliyaPro"

                textSize =
                    30f

                setTypeface(
                    null,
                    Typeface.BOLD
                )

                gravity =
                    Gravity.CENTER

                setTextColor(
                    Color.BLACK
                )
            }


        val subtitle =
            TextView(this).apply {

                text =
                    "AI Companion Runtime"

                textSize =
                    16f

                gravity =
                    Gravity.CENTER

                setTextColor(
                    Color.DKGRAY
                )
            }


        chatBox =
            TextView(this).apply {

                textSize =
                    18f

                setTextColor(
                    Color.BLACK
                )

                text =
                    controller.welcomeMessage()

                setPadding(
                    16,
                    16,
                    16,
                    16
                )
            }


        scrollView =
            ScrollView(this).apply {

                addView(
                    chatBox,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }


        input =
            EditText(this).apply {

                hint =
                    "Введите сообщение..."

                textSize =
                    16f
            }


        val button =
            Button(this).apply {

                text =
                    "Отправить"
            }


        button.setOnClickListener {


            val message =
                input.text.toString().trim()


            if (message.isEmpty()) {
                return@setOnClickListener
            }


            input.text.clear()


            scope.launch {


                chatBox.append(
                    "\n\nТы:\n$message\n\nLiliya:\n"
                )


                controller
                    .sendMessage(message)
                    .collect { response ->


                        chatBox.append(
                            response + "\n"
                        )


                        scrollDown()
                    }
            }
        }


        val normalParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


        val chatParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
            ).apply {

                weight = 1f
            }


        root.addView(
            title,
            normalParams
        )


        root.addView(
            subtitle,
            normalParams
        )


        root.addView(
            scrollView,
            chatParams
        )


        root.addView(
            input,
            normalParams
        )


        root.addView(
            button,
            normalParams
        )


        setContentView(root)



        scope.launch {

            controller.start()


            chatBox.append(
                "\n\nRuntime: READY"
            )
        }
    }



    private fun scrollDown() {

        scrollView.post {

            scrollView.fullScroll(
                ScrollView.FOCUS_DOWN
            )
        }
    }



    override fun onDestroy() {


        scope.launch {

            controller.stop()
        }


        scope.cancel()


        super.onDestroy()
    }
}
