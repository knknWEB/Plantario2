package com.example.plantario2.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.plantario2.ViewModel.PlantViewModel
import com.example.plantario2.R

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PlantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //znajdujemy przycisk Moje Plantario
        val myPlantarioButton = findViewById<Button>(R.id.my_plantario_button)
        val addButton = findViewById<Button>(R.id.add_plant_button)

        viewModel=ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            PlantViewModel::class.java)

        //tworzymy listener dla kliknięcia przycisku Moje Plantario
        myPlantarioButton.setOnClickListener {
            //tworzymy nową aktywność ListActivity
            val intent = Intent(this, com.example.plantario2.Activity.ListActivity::class.java)
            //uruchamiamy aktywność
            startActivity(intent)


        }
        //tworzymy listener dla kliknięcia przycisku Dodaj Roślinę
        addButton.setOnClickListener {
            //tworzymy nową aktywność ListActivity
            val intent2 = Intent(this, AddActivity::class.java)
            //uruchamiamy aktywność
            startActivity(intent2)
        }
    }

}