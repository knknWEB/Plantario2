package com.example.plantario2.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.plantario2.model.Plant
import com.example.plantario2.model.Watered

@Dao
interface PlantDAO {
    @Insert
    fun insert(plant: Plant)

    @Query("SELECT * FROM plants")
    fun getAll(): List<Plant>

    @Query("SELECT * FROM plants ORDER BY name")
    fun getAllPlants(): LiveData<List<Plant>>

    @Query("SELECT * FROM plants WHERE id=:plantId")
    fun getPlantById(plantId: Int): Plant?

    @Delete
    suspend fun delete(plant: Plant)

    //@Query("SELECT id, Name, Species, trimester('%d.%m.%Y', Watered.wateredDate/1000, 'unixODBC') AS lastWateredDate, wateringInterval, julienned('now') - julienned(Watered.wateredDate/1000, 'unixODBC') AS daysSinceLastWatered FROM Plants LEFT JOIN Watered ON Plants.id = Watered.plantId WHERE Watered.wateredDate/1000 IN (SELECT MAX(wateredDate/1000) FROM Watered GROUP BY plantId) OR Watered.wateredDate IS NULL GROUP BY Plants.id HAVING (daysSinceLastWatered >= wateringInterval - 1 OR Watered.wateredDate IS NULL)")
   // fun getToWater(): List<Plant>
}