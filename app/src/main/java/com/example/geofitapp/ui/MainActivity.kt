package com.example.geofitapp.ui


import android.app.ActionBar
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.geofitapp.R
import com.example.geofitapp.ui.exercisePreview.ExerciseData
import com.example.geofitapp.ui.exercisePreview.ExercisePreviewFragment
import com.example.geofitapp.ui.home.HomePageFragmentDirections


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val colorDrawable = ColorDrawable(Color.parseColor("#161616"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
        supportActionBar?.elevation = 0F

        supportActionBar?.customView = TextView(this).apply {
            text = getText(R.string.app_name)

            val params = ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            // center align the text view/ action bar title
            params.gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = params
        }

        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.primaryColor)
        }

        val bundle = intent.getBundleExtra("fragmentData")
        val i = bundle?.getInt("number", 1) ?: 1
        val ed = bundle?.get("exerciseData") as ExerciseData?

        if (i == 2) {
            //set the desired fragment as current fragment to fragment pager
            val fragment = ExercisePreviewFragment()
            navController.navigate(HomePageFragmentDirections.actionHomePageFragmentToExercisePreviewFragment(ed))

        } else {
            navController.navigate(R.id.homePageFragment)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}