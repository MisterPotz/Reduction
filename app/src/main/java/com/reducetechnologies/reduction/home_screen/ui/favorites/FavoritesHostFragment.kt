package com.reducetechnologies.reduction.home_screen.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import timber.log.Timber

/**
 * In this application different bottom navigation fragments can have their own view
 * hierarchies. Therefore they can have their custom navcontrollers, each. This custom controller
 * cannot be at the current moment be officially registered via Android Components Framework.
 * So we use our custom implementation of navcontroller to deal with that case.
 */
abstract class  WithOwnNavController : Fragment(){
    abstract fun getNavController() : NavController
    abstract fun getNavFragment() : NavHostFragment

}

class FavoritesHostFragment : WithOwnNavController() {
    override fun getNavFragment(): NavHostFragment {
        return navHostFragment
    }

    override fun getNavController(): NavController {
        return navController
    }

    private lateinit var favoritesViewModel: FavoritesHostViewModel
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favoritesViewModel =
            ViewModelProvider(this).get(FavoritesHostViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)
        navHostFragment = childFragmentManager.findFragmentById(R.id.favorites_nav_host_fragment) as NavHostFragment

        /*childFragmentManager.setPrimaryNavigationFragment(navHostFragment)*/
        navController = navHostFragment.navController
        childFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHostFragment).commit()

        SingletoneContextCounter.fragments++
        Timber.i("FavoritesFragment in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")
       // setupCallbacks()
        return root
    }

    override fun onPause() {
        super.onPause()
        Timber.i("in onPause: current fragment amount: ${SingletoneContextCounter.fragments}")
    }
}