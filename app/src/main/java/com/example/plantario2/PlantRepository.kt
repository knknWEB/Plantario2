package com.example.plantario2

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.plantario2.dao.PlantDAO
import com.example.plantario2.database.PlantDatabase
import com.example.plantario2.model.Plant
import kotlinx.coroutines.*

class PlantRepository (application: Application) {
    private var plantDao: PlantDAO

    init {
        val database: PlantDatabase? = PlantDatabase.getInstance(application.applicationContext)
        plantDao = database!!.plantDao()
    }

    fun getAllPlantAsync(): Deferred<LiveData<List<Plant>>> =
        CoroutineScope(Dispatchers.IO).async { plantDao.getAllPlants() }

    fun getPlantByIdAsync(id: Int): Deferred<Plant?> =
        CoroutineScope(Dispatchers.IO).async { plantDao.getPlantById(id) }


    suspend fun deletePlant(plant: Plant) {
        plantDao.delete(plant)
    }
}

