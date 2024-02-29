import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdate(updateId: Int): String {
        val urlGetUpdates = "$TELEGRAM_BOT_API_BASE_URL$botToken/getUpdates?offset=$updateId"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: String?, textMessage: String?): String {
        val encodedText = URLEncoder.encode(textMessage, Charsets.UTF_8)
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage?chat_id=$chatId&text=$encodedText"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(chatId: String?): String {
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "$CALLBACK_DATA_LEARN_BUTTON"
                                
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$CALLBACK_DATA_STATISTICS_BUTTON"                                
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    private fun sendQuestion(chatId: String?, question: Question): String? {
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage"
        val answers =
            question.answers.mapIndexed { index, word -> """[{ "text": "${word.translate}", "callback_data": "$CALLBACK_DATA_ANSWER_PREFIX$index" }]""" }
                .joinToString(",")
        println(question)
        println(answers)
        val sendQuestion = """
            {
                "chat_id": $chatId,
                "text": "${question.wordForLearning.original}",
                "reply_markup": {
                    "inline_keyboard": [ $answers ]
                }
            }        
        """.trimIndent()

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestion))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, chatId: String?) {
        trainer.getNextQuestion()?.let { sendQuestion(chatId, it) } ?: sendMessage(chatId, "Вы выучили все слова!")
    }
}


