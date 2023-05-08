package com.example.plantario2.Activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.plantario2.ViewModel.PlantViewModel
import com.example.plantario2.R

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: PlantViewModel
    private val REQUEST_PERMISSIONS = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // sprawdzenie uprawnień
        val permissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.POST_NOTIFICATIONS)
        val notGrantedPermissions = permissions.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (notGrantedPermissions.isNotEmpty()) {
            requestPermissions(notGrantedPermissions.toTypedArray(), REQUEST_PERMISSIONS)
        }

        //Inicjalizacja przycisków Moje Plantario i Dodawania roslin
        val myPlantarioButton = findViewById<Button>(R.id.my_plantario_button)
        val addButton = findViewById<Button>(R.id.add_plant_button)

        //Utworzenie  nowego obiektu PlantViewModel i przypisanie go do zmiennej viewModel
        viewModel=ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(PlantViewModel::class.java)

        //Listener dla kliknięcia przycisku Moje Plantario
        myPlantarioButton.setOnClickListener {
            //Tworzona nowa aktywność ListActivity
            val intent = Intent(this, com.example.plantario2.Activity.ListActivity::class.java)
            startActivity(intent)   //uruchomienie
        }
        //Listener dla kliknięcia przycisku Dodaj Roślinę
        addButton.setOnClickListener {
            //Tworzona nowa aktywność AddActivity
            val intent2 = Intent(this, AddActivity::class.java)
            startActivity(intent2)  //uruchomienie
        }

    }




}