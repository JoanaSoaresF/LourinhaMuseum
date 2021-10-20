package com.example.lourinhamuseum.data.domain

import com.example.lourinhamuseum.utils.RomanNumeralConverter

data class Score(
    val id: Long,
    val pos: Int,
    val username: String,
    val score: Int
) {
    val rankingPosition: String
        get() = RomanNumeralConverter().generate(pos)
}