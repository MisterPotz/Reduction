package com.reducetechnologies.reduction.home_screen

import android.os.Bundle
import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.NavController
import timber.log.Timber
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationFragment
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.EncyclopediaFragment
import com.reducetechnologies.reduction.home_screen.ui.favorites.FavoritesHostFragment
import com.reducetechnologies.reduction.home_screen.ui.favorites.WithOwnNavController
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.IllegalStateException

object SingletoneContextCounter {
    var fragments: Int = 0

    fun stackSize(navController: NavController) {
    }
}

/**
 * Нужно что-то, чтобы хранило фрагменты и обеспечивало с ними безопасную работу.
 */
class HomeActivity : FragmentActivity() {
    /**
     * [TAG] - how we can cal a fragment by const string
     * [menuItemIdRes] - bottommenu item id for thig fragment
     */
    data class FragmentWrapped(
        val type: TabType
    ) {
        var fragment: WithOwnNavController? = null
    }

    enum class TabType(val TAG: String, @IdRes val menuItemIdRes: Int) {
        CALCULATION("CALCULATION", R.id.navigation_calculation),
        ENCYCLOPEDIA("ENCYCLOPEDIA", R.id.navigation_encyclopedia),
        FAVORITES("FAVORITES", R.id.navigation_favorites)
    }

    val singletoneContextCounter = SingletoneContextCounter

    lateinit var bottomFragmentController: BottomFragmentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_home)
        initFragments()
        Timber.v("Home Activity created")
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

    /*override fun onNavigateUp(): Boolean {
        // Нужно делать так, чтобы закрывалось не все приложение, а поднимался по иерарзии вызовов
        // текущий navhostmanager
        return bottomFragmentController.onNavigateUp()
    }*/

    override fun onBackPressed() {
        bottomFragmentController.onBackPressed()
    }

    private fun initFragments() {
        bottomFragmentController = BottomFragmentController(
            supportFragmentManager, R.id.main_container,
            nav_view
        )
    }

    class BottomFragmentController(
        val fm: FragmentManager,
        @IdRes val fragmentsContainer: Int,
        val bottomNavigationView: BottomNavigationView
/*
        val fragmentsList: List<FragmentWrapped>
*/
    ) {
        val fragmentsList: List<FragmentWrapped> = listOf<FragmentWrapped>(
            FragmentWrapped(TabType.CALCULATION),
            FragmentWrapped(TabType.ENCYCLOPEDIA),
            FragmentWrapped(TabType.FAVORITES)
        )

        var active: Int = 1
            set(value) {
                if (value >= fragmentsList.size || value < 0) {
                    Timber.w("Value was out of fragmentList.Size")
                    return
                } else {
                    field = value
                }
            }

        init {
            for (i in fragmentsList) {
                when (i.type) {
                    TabType.CALCULATION -> i.fragment = CalculationFragment()
                    TabType.ENCYCLOPEDIA -> i.fragment = EncyclopediaFragment()
                    TabType.FAVORITES -> i.fragment = FavoritesHostFragment()
                }
            }
            onFirstLaunch()
        }

        fun onFirstLaunch() {
            fragmentsList.forEachIndexed { index, fragment ->
                if (index != active) {
                    fm.beginTransaction().add(fragment).hide(fragment).commit()
                } else {
                    fm.beginTransaction().add(fragment).show(fragment).commit()
                }
            }
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                active = fragmentsList.indexOfFirst {
                    item.itemId == it.type.menuItemIdRes
                }
                showActive()
                true
            }
        }

        fun showActive() {
            fragmentsList.forEachIndexed { index, fragment ->
                if (index != active) {
                    fm.beginTransaction().hide(fragment).commit()
                } else {
                    fm.beginTransaction().show(fragment).commit()
                }
            }
        }

        fun installPrimaryNavHostFragment() {
        }

//        fun onNavigateUp(): Boolean {
//            return fragmentsList[active].fragment!!.navController().navigateUp()
//        }

        fun onBackPressed() {
            fragmentsList[active].fragment!!.getNavController().popBackStack()
        }

        // Classes for comfortable working with fragment transaction
        fun FragmentTransaction.add(fragmentWrapped: FragmentWrapped): FragmentTransaction {
            this.add(fragmentsContainer, fragmentWrapped.fragment!!, fragmentWrapped.type.TAG)
            return this
        }

        fun FragmentTransaction.hide(fragmentWrapped: FragmentWrapped): FragmentTransaction {
            this.hide(fragmentWrapped.fragment!!)
            return this
        }

        fun FragmentTransaction.show(fragmentWrapped: FragmentWrapped): FragmentTransaction {
            this.show(fragmentWrapped.fragment!!)
            return this
        }
    }
}
