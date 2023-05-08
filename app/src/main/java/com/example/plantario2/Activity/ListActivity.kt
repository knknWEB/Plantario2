package com.example.plantario2.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantario2.*
import com.example.plantario2.Adapter.DaoAdapter
import com.example.plantario2.ViewModel.PlantViewModel
import com.example.plantario2.model.Plant

class ListActivity : AppCompatActivity() {
    //Deklaracja prywatnych pól aplikacji
    private lateinit var viewModel: PlantViewModel
    private lateinit var daoAdapter: DaoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listOfPlant: LiveData<List<Plant>>
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Ustawienie widoku i przycisku cofania w menu nagłówka.
        setContentView(R.layout.activity_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Inicjalizacja paska postępu
        progressBar = findViewById(R.id.progress_bar)

        //Inicjalizacja obiektu klasy PlantViewModel
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            PlantViewModel::class.java)

        //Inicjalizacja obiektu klasy RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        //Pobranie listy roślin z klasy PlantViewModel
        listOfPlant = viewModel.getAllPlant()

        //Przypisanie adaptera DaoAdapter do obiektu RecyclerView
        listOfPlant.observe(this, Observer { plants ->
            if (plants.isNotEmpty()) {  //jeżeli lista roślin jest niepusta
                daoAdapter = DaoAdapter(plants)
                recyclerView.adapter = daoAdapter
                progressBar.visibility = View.GONE  //Ukrycie paska postępu, jeśli lista roślin jest niepusta
            }
            else { //jeżeli lista roślin jest pusta
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.GONE
                findViewById<TextView>(R.id.app_list_nothing).visibility = View.VISIBLE //Pokazanie tekstu, jeżeli lista roślin jest pusta
            }
        })
    }

    //Funkcja, która obsługuje kliknięcie przycisku wstecz na pasku nawigacji
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
