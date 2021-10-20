package com.example.lourinhamuseum.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lourinhamuseum.data.database.entities.DatabaseMuseum.Companion.MUSEUM_TABLE

@Entity(tableName = MUSEUM_TABLE)
data class DatabaseMuseum(
    @PrimaryKey
    val id: Int,
    val title: String,
    var score: Int = 0
) {
    companion object {
        const val MUSEUM_TABLE = "museum_table"
    }
}



