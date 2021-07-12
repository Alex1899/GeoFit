package com.example.geofitapp.ui


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.geofitapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val colorDrawable = ColorDrawable(Color.parseColor("#342c3a"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
        supportActionBar?.elevation = 0F
    }
}