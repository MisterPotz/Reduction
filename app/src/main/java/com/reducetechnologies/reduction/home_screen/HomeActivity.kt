package com.reducetechnologies.reduction.home_screen

import android.os.Bundle
import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.FragmentStateHelper
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationFragment
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.EncyclopediaNavHost
import com.reducetechnologies.reduction.home_screen.ui.favorites.FavoritesHostFragment
import com.reducetechnologies.reduction.home_screen.ui.favorites.WithOwnNavController
import kotlinx.android.synthetic.main.activity_home.*

object SingletoneContextCounter {
    var fragments: Int = 0

}

/**
 * Нужно что-то, чтобы хранило фрагменты и обеспечивало с ними безопасную работу.
 */
class HomeActivity : FragmentActivity() {
    // Mutable debugging data
    var debugInt = 0

    val debugTree = Timber.DebugTree()
    private var fragmentStateHelper: FragmentStateHelper? = null

    companion object {
        // TAG for fragment state helper
        //private const val STATE_HELPER = "HomeActivity_STATE_HELPER"
    }

    init {
        Timber.i("Activity constructor, debugInt: $debugInt")
    }

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
        Timber.plant(debugTree)

        if (savedInstanceState != null) {
            // We saved some data before. We stored fragmentStateHelper in there
            fragmentStateHelper?.restoreHelperState(savedInstanceState)
        }

        setContentView(R.layout.activity_home)
        Timber.i("Home Activity created: $this")
        Timber.i("Activity supportFragmentManager: $supportFragmentManager")
        // initFragments должен обязательно быть только в onCreate - если выполнять его позже
        // по Lifecycle (в onStart) - будут спауниться лишние фрагменты и порождать дичайшие галюны
        initFragments(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        bottomFragmentController.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        Timber.i("in onStart")
        super.onStart()
    }

    override fun onResume() {
        bottomFragmentController.recheckOnResume()
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

    /*override fun onNavigateUp(): Boolean {
        // Нужно делать так, чтобы закрывалось не все приложение, а поднимался по иерарзии вызовов
        // текущий navhostmanager
        return bottomFragmentController.onNavigateUp()
    }*/

    override fun onBackPressed() {
        Timber.i("onBackPressed")
        bottomFragmentController.onBackPressed()
    }

    private fun initFragments(bundle: Bundle?) {
        bottomFragmentController = BottomFragmentController(
            fragmentStateHelper,
            supportFragmentManager, R.id.main_container,
            nav_view
        )
//        if (bundle != null) {
//            bottomFragmentController.onRestoreInstanceState(bundle)
//        }
    }

    class BottomFragmentController(
        val fSH: FragmentStateHelper?,
        val fm: FragmentManager,
        @IdRes val fragmentsContainer: Int,
        val bottomNavigationView: BottomNavigationView
    ) {
        companion object {
            const val CURRENT_ACTIVE = "CURRENT_ACTIVE"
            const val NAVIGATION_FRAGMENT_STATE = "NAVIGATION_FRAGMENT_STATE"
            const val HELPER_STATE = "BottomFragmentController_HELPER_STATE"
        }

        val fragmentsList: List<FragmentWrapped> = listOf<FragmentWrapped>(
            /*FragmentWrapped(TabType.CALCULATION),*/
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
            Timber.i("instantiating bottom controller")
            onCreated()
        }

        private fun navigationStateTag(int: Int): String {
            return NAVIGATION_FRAGMENT_STATE + int.toString()
        }

        fun pickFragment(fragmentWrapped: FragmentWrapped) {
            when (fragmentWrapped.type) {
                TabType.CALCULATION -> fragmentWrapped.fragment = CalculationFragment()
                TabType.ENCYCLOPEDIA -> fragmentWrapped.fragment =
                    EncyclopediaNavHost()
                TabType.FAVORITES -> fragmentWrapped.fragment = FavoritesHostFragment()
            }
        }

        fun saveNavigationState(fragmentWrapped: FragmentWrapped, int: Int, bundle: Bundle) {
//            val savedState = fragmentWrapped.fragment!!.getNavController().saveState()
////            bundle.putBundle(navigationStateTag(int), savedState)
            // Saving to Fragment State Helper
            fSH?.saveState(fragmentWrapped.fragment!!, fragmentWrapped.type.menuItemIdRes)
                ?: Timber.w("State was not saved due to FragmentStateHelper being null")
        }

        fun onSaveInstanceState(bundle: Bundle) {
            // Здесь надо класть сохраненные штуки фрагментов
            bundle.putInt(BottomFragmentController.CURRENT_ACTIVE, active)

            fragmentsList.forEachIndexed { i, fragment ->
                saveNavigationState(fragment, i, bundle)
            }
            if (fSH != null) {
                bundle.putBundle(HELPER_STATE, fSH.saveHelperState())
            }
        }

//        fun onRestoreInstanceState(bundle: Bundle) {
//            active = bundle.getInt(CURRENT_ACTIVE)
//            fragmentsList.forEachIndexed { index, fragmentWrapped ->
//                fragmentWrapped.fragment!!.getNavController().apply {
//                    val restored = bundle.getBundle(navigationStateTag(index))
//                    restoreState(restored)
//                }
//            }
//        }

        /**
         * Uses fragment manager to get saved instances of fragments if such exist.
         * This can happen for example, when screen orientation is changed
         */
        private fun pickUpFromSaved(fragment: FragmentWrapped) {
            if (fragment.fragment == null) {
                // не нашли у фрагмент менеджера каких-то ссылок на указанный фрагмент
                // создаем новый
                pickFragment(fragment)
                // Сохраняем фрагмент в fm
                // Если есть чел который хранит состояния, восстанавливаем состояние фрагмента
                fSH?.restoreState(fragment.fragment!!, fragment.type.menuItemIdRes)
                fm.beginTransaction().add(fragment).commit()
            } else {
                Timber.w("Fragment was not created - already exists but method was called")
            }
        }

        /**
         * Performs onResume to make bottombar recheck the actual values
         * Why, do you ask? Why are we doing this not in onCreate? Why not in onStop?
         * Because, I tell you, it won't work after orientation change. The thing is, the recreation
         * of bottomNavigation state happens BETWEEN onStop and onResume. So when you call YOUR
         * instantiating methods in onResume before super.onResume - it will work, it's the minimum
         * condition.
         */
        fun recheckOnResume() {
            // Выставляем нужную позицию выбранной
            bottomNavigationView.selectedItemId = fragmentsList[active].type.menuItemIdRes
        }

        // Setting necessary callbacks on onCreate
        fun onCreated() {
            // Filling fragment instances
            fragmentsList.forEach {
                pickUpFromSaved(it)
            }
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                active = fragmentsList.indexOfFirst {
                    item.itemId == it.type.menuItemIdRes
                }
                Timber.i("Current active menu: $active, menu items ids: ${bottomNavigationView.menu}")
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

        //TODO реализовать логику подтверждения выхода
        fun onBackPressed() {
            fragmentsList[active].fragment!!.getNavController().let {
                Timber.i("Current destination ID: ${it.currentDestination!!.id}")
                Timber.i("Id of current active: ${fragmentsList[active].type.menuItemIdRes}")
                if (it.currentDestination!!.id == fragmentsList[active].type.menuItemIdRes) {
                    // Если текущая дестинейшн в стеке равна главному элементу нашего подменю, то не нужно ничего делать
                    Timber.w("No popBack was performed - already top sub-menu level")
                } else {
                    it.popBackStack()
                }
            }
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
