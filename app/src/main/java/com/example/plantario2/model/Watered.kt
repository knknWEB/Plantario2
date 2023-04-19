package com.example.plantario2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "watered")
data class Watered(
    val plantId: Int,
    val wateredDate: Date,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)