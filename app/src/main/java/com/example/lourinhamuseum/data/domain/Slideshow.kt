package com.example.lourinhamuseum.data.domain

data class Slideshow(
    val id: Int,
    val name: String,
    val url: String,
    val time: Int,
    val caption: String
)
fun List<Slideshow>.filesToDownload():List<String>{
    return map {
        it.url
    }
}

