package com.reducetechnologies.reduction.home_screen

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.reducetechnologies.reduction.R
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
        Timber.v("Home Activity created: $this")

        Timber.i("Activity supportFragmentManager: $supportFragmentManager")
        // initFragments должен обязательно быть только в onCreate - если выполнять его позже
        // по Lifecycle (в onStart) - будут спауниться лишние фрагменты и порождать дичайшие галюны
        initFragments()
    }

    override fun onStart() {
        Timber.i("in onStart")
        super.onStart()
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
            onCreated()
        }

        fun pickFragment(fragmentWrapped: FragmentWrapped) {
            when (fragmentWrapped.type) {
                TabType.CALCULATION -> fragmentWrapped.fragment = CalculationFragment()
                TabType.ENCYCLOPEDIA -> fragmentWrapped.fragment =
                    EncyclopediaNavHost()
                TabType.FAVORITES -> fragmentWrapped.fragment = FavoritesHostFragment()
            }
        }

        /**
         * Uses fragment manager to get saved instances of fragments if such exist.
         * This can happen for example, when screen orientation is changed
         */

        fun pickUpFromSaved(fragment: FragmentWrapped) {
            fm.findFragmentByTag(fragment.type.TAG).let {
                if (it == null) {
                    // не нашли у фрагмент менеджера каких-то ссылок на указанный фрагмент
                    // создаем новый
                    pickFragment(fragment)
                    // Сохраняем фрагмент в fm
                    fm.beginTransaction().add(fragment).commit()
                } else {
                    fragment.fragment = it as WithOwnNavController
                }
            }
        }

        fun onCreated() {
            // Выставляем нужную позицию выбранной
            bottomNavigationView.menu[active].isChecked = true
            bottomNavigationView.selectedItemId = active

            // Filling fragment instances
            fragmentsList.forEach {
                pickUpFromSaved(it)
            }
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                active = fragmentsList.indexOfFirst {
                    item.itemId == it.type.menuItemIdRes
                }
                showActive()
                true
            }
            showActive()
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
