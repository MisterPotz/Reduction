package com.reducetechnologies.reduction.home_screen

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.reducetechnologies.reduction.R

class HomeActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var viewController: ViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        viewController = ViewController(navController, navView)
        viewController.initOnClickListeners()
        /**
         * The line below is commented out because its for setting up the action bar
         * But in our case, in home screen we have it turned off, so when the function below
         * Tries to get access to the actionsupportbar, it receives null - then it gives error.
         * @see AppTheme.NoActionBar in styles.xml
         */
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        //setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp() =
        findNavController(R.id.nav_host_fragment).navigateUp()

    class ViewController(
        private val navController: NavController,
        private val bottomBar: BottomNavigationView
    ) {
        fun initOnClickListeners() {
            bottomBar.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_calculation -> navController.navigate(R.id.action_global_navigation_calculation)
                    R.id.navigation_encyclopedia -> navController.navigate(R.id.action_global_navigation_encyclopedia)
                    R.id.navigation_favorites -> navController.navigate(R.id.action_global_navigation_favorites)
                }
                true
            }
        }
    }
}
