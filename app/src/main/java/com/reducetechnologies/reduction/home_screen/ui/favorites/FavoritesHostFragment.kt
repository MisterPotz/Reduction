package com.reducetechnologies.reduction.home_screen.ui.favorites

import android.content.Context
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
abstract class WithOwnNavController : Fragment() {
    abstract fun getNavController(): NavController
    abstract fun getNavFragment(): NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("in onCreate, setting setRetainInstance -> true")
        // Retain this fragment across configuration changes.
        setRetainInstance(true)
    }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.i("Fragment childFragmentManager: $childFragmentManager in $this")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favoritesViewModel =
            ViewModelProvider(this).get(FavoritesHostViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites_host, container, false)
        navHostFragment =
            childFragmentManager.findFragmentById(R.id.favorites_nav_host_fragment) as NavHostFragment

        /*childFragmentManager.setPrimaryNavigationFragment(navHostFragment)*/
        navController = navHostFragment.navController
        childFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHostFragment)
            .commit()

        SingletoneContextCounter.fragments++
        Timber.i("in onCrereateView: current fragment amount: ${SingletoneContextCounter.fragments}")
        // setupCallbacks()
        return root
    }

    override fun onResume() {
        super.onResume()
        Timber.i("in onResume: current fragment amount: ${SingletoneContextCounter.fragments}")

    }

    override fun onStart() {
        super.onStart()
        Timber.i("in onStart: current fragment amount: ${SingletoneContextCounter.fragments}")

    }

    override fun onPause() {
        super.onPause()
        Timber.i("in onPause: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("in onStop: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("Fragment childFragmentManager: $childFragmentManager")

        SingletoneContextCounter.fragments--
        Timber.i("in onDestroyView: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("in onDestroy: current fragment amount: ${SingletoneContextCounter.fragments}")
    }
}