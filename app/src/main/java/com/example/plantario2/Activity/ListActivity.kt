package com.example.plantario2.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantario2.*
import com.example.plantario2.Adapter.DaoAdapter
import com.example.plantario2.ViewModel.PlantViewModel
import com.example.plantario2.model.Plant


class ListActivity : AppCompatActivity() {
    private lateinit var viewModel: PlantViewModel
    private lateinit var daoAdapter: DaoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listOfPlant: LiveData<List<Plant>>
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progress_bar)

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            PlantViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        listOfPlant = viewModel.getAllPlant()
        listOfPlant.observe(this, Observer { plants ->
            if (plants.isNotEmpty()) {
                daoAdapter = DaoAdapter(plants)
                recyclerView.adapter = daoAdapter
                progressBar.visibility = View.GONE
            }
        })

    }
}
