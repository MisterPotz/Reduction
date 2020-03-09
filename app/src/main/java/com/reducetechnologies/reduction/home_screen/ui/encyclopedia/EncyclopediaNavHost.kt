package com.reducetechnologies.reduction.home_screen.ui.encyclopedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import com.reducetechnologies.reduction.home_screen.ui.favorites.WithOwnNavController
import timber.log.Timber

class EncyclopediaNavHost : WithOwnNavController() {
    override fun getNavFragment(): NavHostFragment {
        return navHostFragment!!
    }

    override fun getNavController(): NavController {
        return navController!!
    }

    private var navController: NavController? = null
    private var navHostFragment: NavHostFragment? = null

    companion object {
        fun newInstance() = EncyclopediaNavHost()
    }

    private lateinit var viewModel: EncyclopediaNavHostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SingletoneContextCounter.fragments++
        Timber.i("in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")
        val root = inflater.inflate(R.layout.fragment_encyclopedia_host, container, false)
        // Нужно получить navHost
        // Мб здесь просто нужно проверять, вдруг у нас уже есть navhostfragment. Инстанс то navhost'а у нас не пропадает
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (navHostFragment == null) {
            navHostFragment =
                childFragmentManager.findFragmentById(R.id.encyclopedia_nav_host_fragment) as NavHostFragment
            navController = navHostFragment!!.navController
        }

        /*childFragmentManager.setPrimaryNavigationFragment(navHostFragment)*/
        childFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHostFragment)
            .commit()

        Timber.i("Nav controller: ${navHostFragment!!.findNavController()}")
    }

    // Вообще, сеттить primarynavigationfragment - не оч идкя, потому что он в рамках активности создается всего один раз
    // Лучше это делать где-нибудь, когда фокус возвращается данному фрагменту
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EncyclopediaNavHostViewModel::class.java)
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
        SingletoneContextCounter.fragments--
        Timber.i("in onDestroyView: current fragment amount: ${SingletoneContextCounter.fragments}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("in onDestroy: current fragment amount: ${SingletoneContextCounter.fragments}")
    }
}
