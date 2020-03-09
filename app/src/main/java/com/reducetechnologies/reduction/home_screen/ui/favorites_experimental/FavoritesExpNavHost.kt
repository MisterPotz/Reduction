package com.reducetechnologies.reduction.home_screen.ui.favorites_experimental

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import com.reducetechnologies.reduction.home_screen.ui.favorites.FavoritesHostViewModel
import com.reducetechnologies.reduction.home_screen.ui.favorites.WithOwnNavController
import timber.log.Timber

class FavoritesExpNavHost : NavHostFragment(){
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.i("Fragment onAttach childFragmentManager: $childFragmentManager in $this")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Fragment onCreate: $childFragmentManager in $this")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val root = inflater.inflate(R.layout.fragment_favorites_host, container, false)
        val root = FrameLayout(context!!)
        this.navController.setGraph(R.navigation.favorites_navigation)

        SingletoneContextCounter.fragments++
        Timber.i("Fragment onCreateView: $childFragmentManager in $this")
        Timber.i("in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")
        // setupCallbacks()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (navHostFragment == null) {
//            navHostFragment =
//                childFragmentManager.findFragmentById(R.id.favorites_nav_host_fragment) as NavHostFragment
//            navController = navHostFragment!!.navController
//        }

        /*childFragmentManager.setPrimaryNavigationFragment(navHostFragment)*/
//        childFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHostFragment)
//            .commit()

        //Timber.i("Nav controller: ${navHostFragment!!.findNavController()}")
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