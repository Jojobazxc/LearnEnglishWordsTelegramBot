import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    val telegramBot = TelegramBotService()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBot.getUpdate(botToken, updateId)
        println(updates)

        val startUpdateId = updates.lastIndexOf("update_id")
        val endUpdateId = updates.lastIndexOf(",\n\"message\"")
        if (startUpdateId == -1 || endUpdateId == -1) continue
        val updateIdString = updates.substring(startUpdateId + 11, endUpdateId)

        updateId = updateIdString.toInt() + 1

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

        val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
        val matchResultId: MatchResult? = chatIdRegex.find(updates)
        val groupsId = matchResultId?.groups
        val id = groupsId?.get(1)?.value
        println(id)

        val message = telegramBot.sendMessage(botToken, id, text)
        println(message)
    }
    
}
