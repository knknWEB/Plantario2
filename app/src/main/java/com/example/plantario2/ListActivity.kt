package com.example.plantario2
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantario2.*
import com.example.plantario2.dao.PlantDAO
import com.example.plantario2.database.PlantDatabase
import com.example.plantario2.model.Plant


class ListActivity : AppCompatActivity() {
    private lateinit var viewModel: PlantViewModel
    private lateinit var daoAdapter: DaoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listOfPlant: LiveData<List<Plant>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(PlantViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        listOfPlant = viewModel.getAllPlant()
        listOfPlant.observe(this, Observer {
            if(it.isNotEmpty()){
                daoAdapter=DaoAdapter(it)
                recyclerView.adapter=daoAdapter
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}