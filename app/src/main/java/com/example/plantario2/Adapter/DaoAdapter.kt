package com.example.plantario2.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.plantario2.model.Plant
import java.io.File
import com.example.plantario2.Activity.*
import com.example.plantario2.R
import com.squareup.picasso.Picasso

class DaoAdapter(private val listOfPlant: List<Plant>): RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val row = LayoutInflater.from(parent.context).inflate(R.layout.plant_row, parent, false)
        return MyViewHolder(row)
    }

    override fun getItemCount(): Int {
        return listOfPlant.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.plant_name_textview.text = listOfPlant[position].name
        holder.plant_species_textview.text = listOfPlant[position].species

        val nazwa = listOfPlant[position].name
        val imageFile = File(holder.itemView.context.filesDir, "$nazwa.jpg")
        if (imageFile.exists()) {
            Picasso.get().load(imageFile).into(holder.picon)
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlantDetailsActivity::class.java).apply {
                putExtra("plantId", listOfPlant[position].id)
                putExtra("plantName", listOfPlant[position].name)
                putExtra("species", listOfPlant[position].species)
                putExtra("wateringInterval", listOfPlant[position].wateringInterval)
                // dodaj inne informacje o roślinie
            }
            holder.itemView.context.startActivity(intent)
        }

    }

}

class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
    val plant_name_textview: TextView = view.findViewById(R.id.plant_name_textview)
    val plant_species_textview: TextView = view.findViewById(R.id.plant_species_textview)
    val picon: ImageView = view.findViewById(R.id.icon)
}





