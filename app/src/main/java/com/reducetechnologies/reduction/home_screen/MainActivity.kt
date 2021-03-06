package com.reducetechnologies.reduction.home_screen

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.setupWithNavController
import kotlinx.android.synthetic.main.apptoolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

object SingletoneContextCounter {
    var fragments: Int = 0
}

/**
 * Нужно что-то, чтобы хранило фрагменты и обеспечивало с ними безопасную работу.
 * [CoroutineScope] необходима, чтобы иметь высокоуровневые корутины на уровне активности
 */
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    val debugTree = Timber.DebugTree()
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar!!)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val debug = Timber.DebugTree()
        Timber.plant(debug)
        Timber.i("onSaveInstanceState called in activity")
        Timber.uproot(debug)
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.encyclopedia_global_navigation

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navigationIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.main_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
        })


        currentNavController = controller
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflating menu
//        menuInflater.inflate(R.menu.see,menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onStart() {
        Timber.i("in onStart")
        super.onStart()
    }

    override fun onResume() {
        if (Timber.treeCount() == 0) {
            Timber.plant(debugTree)
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Timber.i("Activity stopped, cutting down debug tree")
        // Uprooting the tree, so it doens't spawn multiplied messages across logs
        Timber.uprootAll()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Timber.i("onBackPressed")
    }
}
