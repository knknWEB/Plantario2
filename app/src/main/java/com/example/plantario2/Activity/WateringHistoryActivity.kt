package com.example.plantario2.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantario2.R
import com.example.plantario2.Adapter.WateringHistoryAdapter
import com.example.plantario2.database.PlantDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WateringHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WateringHistoryAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watering_history)



        supportActionBar?.setDisplayHomeAsUpEnabled(true) // dodanie przycisku "wstecz"

        recyclerView = findViewById(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayout.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        val plantId = intent.getIntExtra("plantId", -1)

        CoroutineScope(Dispatchers.IO).launch {
            val wateringHistory = PlantDatabase.getInstance(this@WateringHistoryActivity).wateredDao().getWateringHistory(plantId)
            withContext(Dispatchers.Main) {
                adapter = WateringHistoryAdapter(wateringHistory)
                recyclerView.adapter = adapter
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
