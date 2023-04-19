package com.example.plantario2

import android.app.ListActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.plantario2.database.PlantDatabase
import com.example.plantario2.dao.PlantDAO
import com.example.plantario2.model.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

lateinit var plantDao: PlantDAO

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inicjalizacja bazy danych
        val database = PlantDatabase.getDatabase(this)
        plantDao = database.plantDao()

        val button_add = findViewById<Button>(R.id.button_dodaj)
        val editText_name = findViewById<TextView>(R.id.edit_text_nazwa)
        val editText_species = findViewById<TextView>(R.id.edit_text_gatunek)
        val editText_interval = findViewById<TextView>(R.id.edit_text_interwal)


        // Zapisanie rośliny do bazy danych po kliknięciu przycisku
                button_add.setOnClickListener {
            val name = editText_name.text.toString().trim()
            val species = editText_species.text.toString().trim()
            val interval = editText_interval.text.toString().toIntOrNull()

            // Sprawdzenie, czy użytkownik wprowadził poprawne dane
            if (name.isNotEmpty() && species.isNotEmpty() && interval != null) {
                val plant = Plant(name = name, species = species, wateringInterval = interval)
                GlobalScope.launch(Dispatchers.IO) {
                    plantDao.insert(plant)

                }
                finish()
                Toast.makeText(this, getString(R.string.app_add_plant_success), Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, getString(R.string.app_add_plant_error), Toast.LENGTH_SHORT).show()
            }
        }


    }
    // Obsługuje kliknięcie przycisku wstecz na pasku nawigacji
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