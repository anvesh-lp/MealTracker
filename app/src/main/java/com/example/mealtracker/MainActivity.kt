package com.example.mealtracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mealtracker.databinding.ActivityMainBinding
import com.example.mealtracker.fragments.HomeFragment
import com.example.mealtracker.fragments.InputFragment
import com.example.mealtracker.fragments.MonthFragment
import com.example.mealtracker.fragments.WeekFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    private lateinit var USER_ID: String
    private lateinit var bottomnavbar: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var authenticaion: FirebaseAuth
    var PREFS_KEY = "prefs"
    var EMAIL_KEY = "email"
    var email = ""
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticaion = FirebaseAuth.getInstance()
        USER_ID = authenticaion.currentUser?.uid.toString()

//        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main)
        USER_ID = intent.getStringExtra("USER_ID").toString()
        Log.d("onCreate: ", USER_ID)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val homeFragment = HomeFragment()
        val inputFragment = InputFragment()
        val weekFragment = WeekFragment()
        val monthFragment = MonthFragment()
//        bottomnavbar = binding.bottomNav
        bottomnavbar = findViewById<BottomNavigationView>(R.id.bottomNav)
        setTheFragment(homeFragment)
//Navigation between fragments from bottom navigation bar
        bottomnavbar.setOnItemSelectedListener {
            Log.d("Inside the Today", "Main Activity*********************************************")
            when (it.itemId) {
                R.id.today -> {
                    setTheFragment(homeFragment)
                }
                R.id.weekly -> {
                    setTheFragment(weekFragment)
                }
                R.id.monthly -> {
                    setTheFragment(monthFragment)
                }
                R.id.add -> {
                    setTheFragment(inputFragment)
                }
            }
            true
        }

    }

    private fun setTheFragment(fragment: Fragment) {

        val mBundle = Bundle()
        mBundle.putString("UserId", USER_ID)
        fragment.arguments = mBundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment).commit()
        }
    }

    //    Layout inflator for creating bottom navigation bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout, menu)
        return true
//        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // on below line we are clearing our editor.
                editor.clear()
                // on below line we are applying changes which are cleared.
                editor.apply()
                val i = Intent(this@MainActivity, LoginActivity::class.java)

                // on below line we are simply starting
                // our activity to start main activity
                startActivity(i)

                // on below line we are calling
                // finish to close our main activity 2.
                finish()
                //logout code
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

}

