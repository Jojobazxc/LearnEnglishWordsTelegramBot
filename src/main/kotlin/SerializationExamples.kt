import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun main() {

    val json = Json{
        ignoreUnknownKeys = true
    }

    val responseString = """
        {
            "ok": true,
            "result": [
                {
                    "update_id": 326658405,
                    "message": {
                        "message_id": 304,
                        "from": {
                            "id": 6693068095,
                            "is_bot": false,
                            "first_name": "jojoba\ud83e\udd20",
                            "username": "jojobazxc",
                            "language_code": "ru"
                        },
                        "chat": {
                            "id": 6693068095,
                            "first_name": "jojoba\ud83e\udd20",
                            "username": "jojobazxc",
                            "type": "private"
                        },
                        "date": 1709290552,
                        "text": "/start",
                        "entities": [
                            {
                                "offset": 0,
                                "length": 6,
                                "type": "bot_command"
                            }
                        ]
                    }
                }
            ]
        }
    """.trimIndent()

   /* val word = Json.encodeToString(
        Word(
            original = "Hello",
            translate = "Привет",
            countOfCorrectAnswer = 0,
        )
    )
    println(word)

    val wordObject = Json.decodeFromString<Word>(
        """{"original":"Hello","translate":"Привет"}"""
    )
    println(wordObject)*/

    val response = json.decodeFromString<Response>(responseString)
    println(response)

}