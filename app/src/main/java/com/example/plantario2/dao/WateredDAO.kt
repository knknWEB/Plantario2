package com.example.plantario2.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.plantario2.model.Watered

@Dao
interface WateredDAO {
    @Insert
    fun insert(watered: Watered)

    @Query("SELECT * FROM watered WHERE plantId=:plantId ORDER BY id DESC LIMIT 1 ")
    fun getLastWateredDate(plantId: Int): Watered?

    @Query("SELECT * FROM watered WHERE plantId = :plantId ORDER BY wateredDate DESC")
    fun getWateringHistory(plantId: Int): List<Watered>


}