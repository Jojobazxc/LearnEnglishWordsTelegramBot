import java.io.File

fun main() {

    val wordsFile = File("words.txt")

    wordsFile.createNewFile()

    val lines = wordsFile.readLines()

    val dictionary: MutableList<Word> = mutableListOf()
    for (line in lines) {
        val line = line.split("|")
        val word = Word(line[0], line[1], line[2].toInt())
        dictionary.add(word)
    }
    println(dictionary)

    val quantityOfCorrectAnswers = dictionary.filter { it.countOfCorrectAnswer >= 3 }
    while (true) {
        println("Меню: 1 – Учить слова, 2 – Статистика, 0 – Выход")
        val choice = readln().toIntOrNull() ?: continue
        when (choice) {
            1 -> {
                while (true) {
                    val listOfUnlearnedWords = dictionary.filter { it.countOfCorrectAnswer <= 3 }
                    val listOfLearnedWords = dictionary.filter { it.countOfCorrectAnswer > 3 }
                    if (listOfUnlearnedWords.isEmpty()) println("Вы выучили все слова")
                    else {
                        val answers: MutableList<Word> = listOfUnlearnedWords.shuffled().take(4).toMutableList()
                        if (answers.size < 4) {
                            answers.addAll(listOfLearnedWords.shuffled().take(4 - answers.size))
                        }
                        val wordForLearning = answers.random()
                        println(wordForLearning.translate)
                        answers.forEachIndexed { index, word -> println("${index + 1}. ${word.original}") }
                        println("0. Выход в меню")
                        val answerOfUser = readln().toInt()
                        when (answerOfUser) {
                            0 -> break
                            in 1..4 -> TODO("Add functionality")
                        }
                    }
                }
            }

            2 -> {
                val percentsOfCorrectAnswers =
                    ((quantityOfCorrectAnswers.size.toDouble() / dictionary.size) * 100).toInt()
                println("Выучено ${quantityOfCorrectAnswers.size} из ${dictionary.size} слов | ${percentsOfCorrectAnswers}%")
            }

            0 -> return
            else -> println("Введите существующий вариант ответа")
        }
    }

}

