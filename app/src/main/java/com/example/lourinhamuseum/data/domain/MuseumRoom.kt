package com.example.fetchfromserver.domain

import com.example.lourinhamuseum.data.domain.Point

data class MuseumRoom(

    val id: Int,
    val name: String,
    val title: String,
    val order: Int,
    val points: List<Point>
)