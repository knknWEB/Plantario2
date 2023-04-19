package com.example.plantario2.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    val name: String,
    val species: String,
    val wateringInterval: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)