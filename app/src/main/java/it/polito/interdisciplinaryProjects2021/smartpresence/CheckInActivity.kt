package it.polito.interdisciplinaryProjects2021.smartpresence

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.joda.time.DateTime

class CheckInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_check_in)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val addressConfigurationFinished = sharedPreferences.getString("addressConfigurationFinished", "false").toBoolean()
        val maxOccupancyConfigurationFinished = sharedPreferences.getString("maxOccupancyConfigurationFinished", "false").toBoolean()

        if (addressConfigurationFinished && maxOccupancyConfigurationFinished) {
            val db = Firebase.firestore

            val address = sharedPreferences.getString("address", "nothing")
            val user = sharedPreferences.getString("keyCurrentAccount", "noEmail")

            val now = DateTime.now()

            val input = hashMapOf(now.toString() to "IN")

            address?.let { db.collection(it).document("$user+MANUAL").set(input, SetOptions.merge()) }

            Toast.makeText(applicationContext, getString(R.string.shortcutCheckInMessage), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, getString(R.string.configurationNotFinishedMessage), Toast.LENGTH_LONG).show()
        }

        finish()
    }
}