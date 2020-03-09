package com.reducetechnologies.reduction.home_screen.ui.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
        Timber.i("in onCreate, setting setRetainInstance -> false")
        // Retain this fragment across configuration changes.
    }
}

class FavoritesHostFragment : WithOwnNavController() {
    // Mutable debugging data
    var debugInt = 0

    init {
        Timber.i("FavoritesNavHost constructor constructor, debugInt: $debugInt")
    }

    override fun getNavFragment(): NavHostFragment {
        return navHostFragment!!
    }

    override fun getNavController(): NavController {
        return navController!!
    }

    private lateinit var favoritesViewModel: FavoritesHostViewModel
    private var navController: NavController? = null
    private var navHostFragment: NavHostFragment? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.i("Fragment onAttach childFragmentManager: $childFragmentManager in $this")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debugInt++
        Timber.i("Fragment onCreate: $childFragmentManager in $this, debugInt: $debugInt")
    }

    // Here a fragment must be created
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favoritesViewModel =
            ViewModelProvider(this).get(FavoritesHostViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites_host, container, false)

        SingletoneContextCounter.fragments++
        Timber.i("Fragment onCreateView: $childFragmentManager in $this, debugInt: $debugInt")
        Timber.i("in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")
        // setupCallbacks()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Finding fragment
        navHostFragment =
            childFragmentManager.findFragmentById(R.id.favorites_nav_host_fragment) as NavHostFragment
        navController = navHostFragment!!.navController

        /*childFragmentManager.setPrimaryNavigationFragment(navHostFragment)*/
        childFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHostFragment)
            .commit()

        Timber.i("Nav controller: ${navHostFragment!!.findNavController()}")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("in onResume: current fragment amount: ${SingletoneContextCounter.fragments}")

    }

    override fun onStart() {
        super.onStart()
        Timber.i("in onStart: current fragment amount: ${SingletoneContextCounter.fragments}")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        //navHostFragment.setInitialSavedState()
        super.onSaveInstanceState(outState)
        // Saving instance state of navhostfragment
        childFragmentManager.saveFragmentInstanceState(navHostFragment!!)
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