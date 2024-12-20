package org.example

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Int,
)

data class Question(
    val questionWords: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val learningAnswerCount: Int = 3,
    private val countOfQuestionWords: Int = 4,
    private val worldLearningTarget: Int = 2,
) {

    private var question: Question? = null
    private val dictionary = loadDictionary().toMutableList()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= learningAnswerCount }.run { size }
        val percent = (learnedCount.toDouble() / totalCount.toDouble() * 100).toInt()
        return Statistics(totalCount, learnedCount, percent)
    }

    fun getNextQuestion(): Question? {

        val notLearnedList = dictionary.filter { it.correctAnswersCount <= worldLearningTarget }
        if (notLearnedList.isEmpty()) return null
        val questionWords = if (notLearnedList.size < countOfQuestionWords) {
            val learnedList = dictionary.filter { it.correctAnswersCount >= worldLearningTarget }.shuffled()
            notLearnedList.shuffled()
                .take(countOfQuestionWords) + learnedList.take(countOfQuestionWords - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(countOfQuestionWords)
        }.shuffled()

        val correctAnswer = notLearnedList.random()

        question = Question(
            questionWords = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int): Boolean {
        return question?.let {
            val userAnswerId = it.questionWords.indexOf(it.correctAnswer)
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
        try {
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
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("некорректный файл")
        }
    }

    private fun saveDictionary(dictionary: List<Word>) {
        wordsFile.printWriter().use { out ->
            dictionary.forEach { word ->
                out.write("${word.original}|${word.translation}|${word.correctAnswersCount}\n")
            }
        }
    }
}