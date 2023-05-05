package com.example.plantario2.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.plantario2.R
import com.example.plantario2.database.PlantDatabase
import com.example.plantario2.dao.PlantDAO
import com.example.plantario2.model.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Ustawienie widoku oraz ustawienie wyświetlania pasku nawigacyjnego
        setContentView(R.layout.activity_add)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Inicjalizacja bazy danych
        lateinit var plantDao: PlantDAO
        val database = PlantDatabase.getDatabase(this)
        plantDao = database.plantDao()

        //Inicjalizacja zmiennych widoku
        val buttonAdd = findViewById<Button>(R.id.button_dodaj)
        val edittextName = findViewById<TextView>(R.id.edit_text_nazwa)
        val edittextSpecies = findViewById<TextView>(R.id.edit_text_gatunek)
        val edittextInterval = findViewById<TextView>(R.id.edit_text_interwal)

        // Zapisanie rośliny do bazy danych po kliknięciu przycisku button_dodaj
        buttonAdd.setOnClickListener {
            val name = edittextName.text.toString().trim()
            val species = edittextSpecies.text.toString().trim()
            val interval = edittextInterval.text.toString().toIntOrNull()

            // Sprawdzenie, czy użytkownik wprowadził poprawne dane
            if (name.isNotEmpty() && species.isNotEmpty() && interval != null) {
                val plant = Plant(name = name, species = species, wateringInterval = interval)
                GlobalScope.launch(Dispatchers.IO) {
                    plantDao.insert(plant)
                }
                finish()
                Toast.makeText(this, getString(R.string.app_add_plant_success), Toast.LENGTH_SHORT).show()

            }
            else {
                Toast.makeText(this, getString(R.string.app_add_plant_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Funkcja, która obsługuje kliknięcie przycisku wstecz na pasku nawigacji
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}