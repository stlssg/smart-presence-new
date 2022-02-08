package it.polito.interdisciplinaryProjects2021.smartpresence

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.joda.time.DateTime

class CheckOutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_check_out)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val addressConfigurationFinished = sharedPreferences.getString("addressConfigurationFinished", "false").toBoolean()
        val maxOccupancyConfigurationFinished = sharedPreferences.getString("maxOccupancyConfigurationFinished", "false").toBoolean()

        if (addressConfigurationFinished && maxOccupancyConfigurationFinished) {
            val db = Firebase.firestore

            val address = sharedPreferences.getString("address", "nothing")
            val user = sharedPreferences.getString("keyCurrentAccount", "noEmail")

            val now = DateTime.now()

            val input = hashMapOf("presence" to "OUT", "timestamp" to now.toString())

            db.collection(address!!).document(user!!).set(hashMapOf("UserName" to user), SetOptions.merge())
            db.collection(address)
                .document(user)
                .collection("MANUAL")
                .document(now.toString())
                .set(input, SetOptions.merge())

            Toast.makeText(applicationContext, getString(R.string.shortcutCheckOutMessage), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, getString(R.string.shortcutNoConfiguration), Toast.LENGTH_LONG).show()

            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("fromCheckInOrOut", "yes")
            startActivity(intent)
        }

        finish()
    }
}