package com.robuxe.robuxtracker.freerobux.Quiz

data class RC_Quiz_Model(
    val question: String,
    val options: Array<String>,
    val correctIndex: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RC_Quiz_Model

        if (question != other.question) return false
        if (!options.contentEquals(other.options)) return false
        if (correctIndex != other.correctIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + options.contentHashCode()
        result = 31 * result + correctIndex
        return result
    }
}
