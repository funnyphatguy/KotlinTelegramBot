package org.example

const val MAXIMUM_VARIANTS = 4
const val WORLD_LEARNING_TARGET = 2
const val LEARNING_WORD_QUANTITY = 3

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Int,
)

data class Question(
    val questionWords: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    private var question: Question? = null
    private val dictionary = loadDictionary().toMutableList()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= LEARNING_WORD_QUANTITY }.run { size }
        val percent = (learnedCount.toDouble() / totalCount.toDouble() * 100).toInt()
        return Statistics(totalCount, learnedCount, percent)
    }

    fun getNextQuestion(): Question? {

        val notLearnedList = dictionary.filter { it.correctAnswersCount <= WORLD_LEARNING_TARGET }
        if (notLearnedList.isEmpty()) return null
        val questionWords = notLearnedList.shuffled().take(MAXIMUM_VARIANTS)
        val correctAnswer = notLearnedList.random()

        question = Question(
            questionWords = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int): Boolean {
        return question?.let { val userAnswerId = it.questionWords.indexOf(it.correctAnswer)
            if (userAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        val dictionary: MutableList<Word> = mutableListOf()
        wordsFile.forEachLine { line ->
            val cells = line.split("|")
            val word = Word(
                original = cells[0],
                translation = cells[1],
                correctAnswersCount = cells.getOrNull(2)?.toIntOrNull() ?: 0
            )
            dictionary.add(word)
        }
        return dictionary
    }

    private fun saveDictionary(dictionary: List<Word>) {
        wordsFile.printWriter().use { out ->
            dictionary.forEach { word ->
                out.write("${word.original}|${word.translation}|${word.correctAnswersCount}\n")
            }
        }
    }
}




