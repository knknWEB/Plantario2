package com.example.plantario2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.plantario2.model.Plant
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking

class PlantViewModel(application: Application): AndroidViewModel(application) {
    private var plantRepository: PlantRepository= PlantRepository(application)
    private var allPlant: Deferred<LiveData<List<Plant>>> = plantRepository.getAllPlantAsync()

    fun getAllPlant():LiveData<List<Plant>> = runBlocking { allPlant.await() }
}