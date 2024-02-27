import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

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
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}


const val TELEGRAM_BOT_API_BASE_URL = "https://api.telegram.org/bot"