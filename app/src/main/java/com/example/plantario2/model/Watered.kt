package com.example.plantario2.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "watered",
    foreignKeys = [ForeignKey(entity = Plant::class,
        parentColumns = ["id"],
        childColumns = ["plantId"],
        onDelete = ForeignKey.CASCADE)])
data class Watered(
    val plantId: Int,
    val wateredDate: Date,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)