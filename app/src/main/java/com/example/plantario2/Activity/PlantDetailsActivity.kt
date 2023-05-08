package com.example.plantario2.Activity

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
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
    //Deklaracja prywatnych pól aplikacji
    private val REQUEST_PERMISSIONS = 123
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
        //Ustawienie widoku i przycisku cofania w menu nagłówka.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plant_details_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Inicjalizacja bazy danych
        val database = PlantDatabase.getDatabase(this)
        wateredDao = database.wateredDao()

        //Pobranie wartości z Intentu
        val plantId = intent.getIntExtra("plantId", -1)
        val plantName = intent.getStringExtra("plantName")
        val plantSpecies = intent.getStringExtra("species")
        val wateringInterval = intent.getIntExtra("wateringInterval", -1)
        plantRepository = PlantRepository(application)

        //Wyświetlenie informacji o roślinie
        val plantNameTextView = findViewById<TextView>(R.id.plant_name_textview)
        val plantSpeciesTextView = findViewById<TextView>(R.id.plant_species_textview)
        val wateringIntervalTextView = findViewById<TextView>(R.id.watering_interval_textview)
        val lastWateredTextView = findViewById<TextView>(R.id.lastWateredTextView)

        //Inicjalizacja przycisku podlej i obsługa zdarzenia kliknięcia
        val waterButton = findViewById<Button>(R.id.water_button)
        waterButton.setOnClickListener {
            val currentDate = Calendar.getInstance().time   //pobranie bieżącej daty
            val watered = Watered(plantId = plantId, wateredDate = currentDate)
            GlobalScope.launch(Dispatchers.IO) {
                wateredDao.insert(watered)  //wpisanie wartości do tabeli watered
            }
            finish()
            Toast.makeText(this, "Podlano $plantName dnia $currentDate", Toast.LENGTH_SHORT).show()
            //Wywołanie metody createAutoNotification
            createAutoNotification(plantId,wateringInterval)
        }

        //Inicjalizacja przycisku ustawienia przypomnień i obsługa zdarzenia kliknięcia
        val setReminderButton = findViewById<Button>(R.id.button_set_reminder)
        setReminderButton.setOnClickListener {
            showDateTimePicker()
        }

        //Inicjalizacja przycisku historii podlewania i obsługa zdarzenia kliknięcia
        val historyButton = findViewById<Button>(R.id.historyButton)
        historyButton.setOnClickListener {
            val intent = Intent(this, WateringHistoryActivity::class.java)
            intent.putExtra("plantId", plant.id)
            startActivity(intent)
        }

        // sprawdzenie uprawnień
        val permissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.POST_NOTIFICATIONS)
        val notGrantedPermissions = permissions.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (notGrantedPermissions.isNotEmpty()) {
            requestPermissions(notGrantedPermissions.toTypedArray(), REQUEST_PERMISSIONS)
        }


        //Inicjalizacja przycisku zrób zdjęcie i obsługa zdarzenia kliknięcia
        val takePhotoButton = findViewById<Button>(R.id.button_take_photo)
        takePhotoButton.setOnClickListener {
            contract.launch(imageUri)
        }


        //Wyświetlenie zdjęcia rośliny, jeżeli istnieje
        imgView = findViewById<ImageView>(R.id.imageView)
        imageUri=createImageUri()!!
        imgView.setImageURI(imageUri)

        //Wyświetlenie informacji na temat dat i interwałów podlewania roślinu - uruchomienie asynchroniczne w tle, aby uniknąć blokowania interfejsu użytkownika
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                println(plantId)

                plant = plantRepository.getPlantByIdAsync(plantId).await() ?: throw Exception("Plant not found")
                plantNameTextView.text = plant.name
                plantSpeciesTextView.text = plant.species
                wateringIntervalTextView.text = plant.wateringInterval.toString()

               val wateringInterval=plant.wateringInterval

                //Pobranie daty ostatniego podlania
                val lastWateredDate = wateredDao.getLastWateredDate(plantId)

                //Wykona się jeżeli wartość lastWateredDate jest różna od null
                if (lastWateredDate != null) {
                    val currentDate = Calendar.getInstance().time
                    val diff = currentDate.time - lastWateredDate?.wateredDate?.time!! ?: 0 //różnica między bieżącą datą a datą ostatniego podlania rośliny
                    val diffInMillis = currentDate.time + wateringInterval * 24 * 60 * 60 * 1000 //różnica między bieżącą datą a interwałem podlewania
                    val diffDate = Date(diffInMillis) //konwersja na format Date
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                    val diffFormatted = dateFormat.format(diffDate)
                    val days = diff / (1000 * 60 * 60 * 24)
                    val plantToWater = findViewById<TextView>(R.id.plant_to_water)
                    val daysToWater=wateringInterval-days

                    //Ustawienie odpowiednich wartośi w zależności od warunków
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
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.plant_details_menu, menu)
        return true
    }

    // Obsługuje kliknięcie przycisku wstecz i usuń roślinę na pasku nawigacji
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
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
    //Funkcja umożliwia wybór daty i godziny, a następnie wywołuje funkcję "createNotification" z wybraną datą i godziną
    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        val plantId = intent.getIntExtra("plantId", 1)
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            currentDate.set(Calendar.YEAR, year)
            currentDate.set(Calendar.MONTH, monthOfYear)
            currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                currentDate.set(Calendar.MINUTE, minute)
                currentDate.set(Calendar.SECOND, 0)
                createNotification(plantId, currentDate.timeInMillis)
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show()
        }
        DatePickerDialog(this, date, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
    }
    //Funkcja tworzy powiadomienie dla rośliny na podstawie przekazanego czasu, w którym ma się pojawić
    private fun createNotification(plantId: Int, notificationTime: Long) {
        val rnds = (0..1000).random()
        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        notificationIntent.putExtra("plantName", intent.getStringExtra("plantName"))
        notificationIntent.putExtra("plantId", intent.getIntExtra("plantId",1))
        notificationIntent.putExtra("rnds",rnds)
        val pendingIntent = PendingIntent.getBroadcast(this, rnds, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)

        Toast.makeText(this, getString(R.string.app_toast_notification_success), Toast.LENGTH_SHORT).show()
    }
    //Funkcja tworzy menu akcji na pasku aplikacji dla widoku szczegółów rośliny.

    private fun createImageUri(): Uri? {
        val plantName = intent.getStringExtra("plantName")

        val image = File(applicationContext.filesDir, "$plantName.jpg")
        return FileProvider.getUriForFile(applicationContext,"com.example.plantario2.fileprovider", image)
    }

    //Funkcja tworzy automatyczne powiadomienie dla wybranej rośliny, z przekazaniem
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
        notificationIntent.putExtra("plantId", intent.getIntExtra("plantId",1))

        notificationIntent.putExtra("rnds",rnds)

        val pendingIntent2 = PendingIntent.getBroadcast(this, rnds, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP,   calendar.timeInMillis, pendingIntent2)

        Toast.makeText(this, getString(R.string.app_toast_notification_success), Toast.LENGTH_SHORT).show()

    }

}




