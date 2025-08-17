package com.kamiapps.deep.deep.domain.data


data class DigitModel(val digitChar: Char, val fullNumber: Int, val place: Int) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is DigitModel -> digitChar == other.digitChar
            else -> super.equals(other)
        }
    }
}

operator fun DigitModel.compareTo(other: DigitModel): Int {
    return fullNumber.compareTo(other.fullNumber)
}
