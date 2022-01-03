package com.example.lourinhamuseum.utils

import android.text.format.DateUtils

/**
 * Converts a time in milliseconds to a string to be displayed
 */
fun convertTime(time: Int): String {
    return DateUtils.formatElapsedTime((time / 1000).toLong())
}

/**
 * Converts an arabic number to a roman number
 */
class RomanNumeralConverter {

    private companion object {
        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 4999
        private val RN_M = arrayOf("", "M", "MM", "MMM", "MMMM")
        private val RN_C =
            arrayOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
        private val RN_X =
            arrayOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
        private val RN_I =
            arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")
    }

    fun generate(number: Int): String {
        if (number < MIN_VALUE || number > MAX_VALUE) {
            return "?"
        }
        return StringBuilder()
            .append(RN_M[number / 1000])
            .append(RN_C[number % 1000 / 100])
            .append(RN_X[number % 100 / 10])
            .append(RN_I[number % 10])
            .toString()
    }
}