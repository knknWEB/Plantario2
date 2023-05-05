package com.example.plantario2.Activity

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.plantario2.NotificationReceiver
import com.example.plantario2.Repository.PlantRepository
import com.example.plantario2.R
import com.example.plantario2.model.Plant
import com.example.plantario2.model.Watered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import com.example.plantario2.dao.WateredDAO
import com.example.plantario2.database.PlantDatabase
import kotlinx.coroutines.GlobalScope
import java.io.File
import java.text.SimpleDateFormat

class PlantDetailsActivity : AppCompatActivity() {
    private lateinit var plantRepository: PlantRepository
    private lateinit var plant: Plant
    private lateinit var wateredDao: WateredDAO


    private lateinit var imgView: ImageView
    private lateinit var imageUri: Uri
    private val contract=registerForActivityResult(ActivityResultContracts.TakePicture()){
        imgView.setImageURI(null)
        imgView.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plant_details_activity)

        //przycisk
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val setReminderButton = findViewById<Button>(R.id.button_set_reminder)
        setReminderButton.setOnClickListener {
            showDateTimePicker()
        }



        // Inicjalizacja bazy danych
        val database = PlantDatabase.getDatabase(this)
        wateredDao = database.wateredDao()


        val plantId = intent.getIntExtra("plantId", -1)
        val plantName = intent.getStringExtra("plantName")
        val plantSpecies = intent.getStringExtra("species")
        val wateringInterval = intent.getIntExtra("wateringInterval", -1)

        // pobierz inne informacje o roślinie
        plantRepository = PlantRepository(application)





        // wyświetl informacje o roślinie
        val plantNameTextView = findViewById<TextView>(R.id.plant_name_textview)
        val plantSpeciesTextView = findViewById<TextView>(R.id.plant_species_textview)
        val wateringIntervalTextView = findViewById<TextView>(R.id.watering_interval_textview)
        val lastWateredTextView = findViewById<TextView>(R.id.lastWateredTextView)


        // znajdź inne widoki

        plantNameTextView.text = plantName
        wateringIntervalTextView.text = "$wateringInterval"
        plantSpeciesTextView.text = plantSpecies
        // ustaw inne informacje


        val historyButton = findViewById<Button>(R.id.historyButton)
        historyButton.setOnClickListener {
            val intent = Intent(this, WateringHistoryActivity::class.java)

            intent.putExtra("plantId", plant.id)
            startActivity(intent)
        }



        //imgView=findViewById(R.id.imageView)
        imgView = findViewById<ImageView>(R.id.imageView)


        imageUri=createImageUri()!!

        imgView.setImageURI(imageUri)


        val takePhotoButton = findViewById<Button>(R.id.button_take_photo)
        takePhotoButton.setOnClickListener {
            contract.launch(imageUri)
        }


        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                println(plantId)

                plant = plantRepository.getPlantByIdAsync(plantId).await() ?: throw Exception("Plant not found")

                // Pobierz datę ostatniego podlania rośliny
                val lastWateredDate = wateredDao.getLastWateredDate(plantId)

                if (lastWateredDate != null) {
                    val currentDate = Calendar.getInstance().time
                    val diff = currentDate.time - lastWateredDate?.wateredDate?.time!! ?: 0

                    val diffInMillis = currentDate.time + wateringInterval * 24 * 60 * 60 * 1000
                    val diffDate = Date(diffInMillis)
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                    val diffFormatted = dateFormat.format(diffDate)


                    val days = diff / (1000 * 60 * 60 * 24)
                    val plantToWater = findViewById<TextView>(R.id.plant_to_water)
                    val daysToWater=wateringInterval-days
                    if (daysToWater>0){
                        val text = resources.getString(R.string.days_to_water, daysToWater, diffFormatted)
                        plantToWater.text = text

                    }
                    else if(daysToWater<0){
                        val text = resources.getString(R.string.days_from_water, daysToWater, diffFormatted)
                        plantToWater.text = text
                    }
                    else{
                        val text = resources.getString(R.string.days_to_water_today)
                        plantToWater.text = text
                    }

                    if (days>0){
                        val text = resources.getString(R.string.days_last_watered,days)
                        lastWateredTextView.text = text
                    }
                    else if(days<1 && days>-1){
                        val text = resources.getString(R.string.days_last_watered_today)
                        lastWateredTextView.text = text
                    }
                    else{
                        val text = resources.getString(R.string.days_last_watered_not)
                        lastWateredTextView.text = text
                    }

                }



                // Zaktualizuj UI z datą ostatniego podlania rośliny


                }


//            val takePhotoButton = findViewById<Button>(R.id.button_take_photo)
//            takePhotoButton.setOnClickListener {
//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE)
//            }



        }


        // dodaj OnClickListener do przycisku "Podlej"
        val waterButton = findViewById<Button>(R.id.water_button)
        waterButton.setOnClickListener {
            val currentDate = Calendar.getInstance().time

            val watered = Watered(plantId = plantId, wateredDate = currentDate)
            GlobalScope.launch(Dispatchers.IO) {
                wateredDao.insert(watered)

            }
            finish()
            Toast.makeText(this, "Podlano $plantName dnia $currentDate", Toast.LENGTH_SHORT).show()

            //Wywołanie metody createAutoNotification
            createAutoNotification(plantId,wateringInterval)

        }
    }



    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            currentDate.set(Calendar.YEAR, year)
            currentDate.set(Calendar.MONTH, monthOfYear)
            currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                currentDate.set(Calendar.MINUTE, minute)
                currentDate.set(Calendar.SECOND, 0)
                createNotification(currentDate.timeInMillis)
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show()
        }
        DatePickerDialog(this, date, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun createNotification(notificationTime: Long) {
        val rnds = (0..1000).random()

        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        notificationIntent.putExtra("plantName", intent.getStringExtra("plantName"))
        notificationIntent.putExtra("rnds",rnds)

        val pendingIntent = PendingIntent.getBroadcast(this, rnds, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)

        Toast.makeText(this, getString(R.string.app_toast_notification_success), Toast.LENGTH_SHORT).show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.plant_details_menu, menu)
        return true
    }

    // Obsługuje kliknięcie przycisku wstecz na pasku nawigacji
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            }
            R.id.delete_plant -> {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        plantRepository.deletePlant(plant)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, getString(R.string.app_toast_delete), Toast.LENGTH_SHORT).show()                        }
                        //onBackPressed()
                        finish()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)


        }
        return super.onOptionsItemSelected(item)
    }



    private fun createImageUri(): Uri? {
        val plantName = intent.getStringExtra("plantName")

        val image = File(applicationContext.filesDir, "$plantName.jpg")
        return FileProvider.getUriForFile(applicationContext,"com.example.plantario2.fileprovider", image)
    }

    private fun createAutoNotification(plantId: Int, daysToWater: Int) {
        val rnds = (0..1000).random()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        calendar.add(Calendar.DATE, daysToWater)

        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        notificationIntent.putExtra("plantName", intent.getStringExtra("plantName"))
        notificationIntent.putExtra("rnds",rnds)

        val pendingIntent2 = PendingIntent.getBroadcast(this, rnds, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP,   calendar.timeInMillis, pendingIntent2)

        Toast.makeText(this, getString(R.string.app_toast_notification_success), Toast.LENGTH_SHORT).show()

    }








}




