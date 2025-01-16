package com.example.persistence

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref()
        roomDB()


    }

    //here using coroutines to handle the db operation and it is not blocking the main thread for UI
    private fun roomDB() {
        CoroutineScope(Dispatchers.IO).launch {
            val userDao = AppDatabase.getInstance(this@MainActivity).userDao()
            try {
                userDao.insertAll(User(firstName = "John", lastName = "Doe"))
                val users: List<User> = userDao.getAll()
                CoroutineScope(Dispatchers.Main).launch {
                    // Update UI with the users list
                    val usersTextView = findViewById<TextView>(R.id.users)
                    if (usersTextView != null) {
                        // Update UI with the users list
                        Log.i("MainActivity", "Users: $users")
                        usersTextView.text = users.toString()
                    } else {
                        Log.e("MainActivity", "TextView with id 'users' not found")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error: ${e.message}")
            }
        }
    }
//
//    private fun roomDB() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val userDao = AppDatabase.getInstance(this@MainActivity).userDao()
//            userDao.insertAll(User(1, "John","Doe"))
//            val users: List<User> = userDao.getAll()
//            Log.i("MainActivity", "Users: $users")
//            // Handle the result on the main thread if needed
//            CoroutineScope(Dispatchers.Main).launch {
//                // Update UI with the users list
//                val usersTextView = findViewById<TextView>(R.id.users)
//                if (usersTextView != null) {
//                    // Update UI with the users list
//                    Log.i("MainActivity", "Users: $users")
//                    usersTextView.text = users.toString()
//                } else {
//                    Log.e("MainActivity", "TextView with id 'users' not found")
//                }
//            }
//        }
//    }
    private fun sharedPref() {
        //READ
        val pref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val value = pref.getString("key", "default value")
        findViewById<TextView>(R.id.sharePref).text = value.toString()

        Log.i("test","returning shared pref $value")

        //WRITE
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("key", "sharepref value")
            apply()
        }
    }
}