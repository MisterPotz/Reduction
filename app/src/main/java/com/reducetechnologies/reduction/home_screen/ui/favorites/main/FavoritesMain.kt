package com.reducetechnologies.reduction.home_screen.ui.favorites.main

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.SingletoneContextCounter
import kotlinx.android.synthetic.main.favorites_main_fragment.*
import timber.log.Timber

class FavoritesMain : Fragment() {
    // Mutable debugging data
    var debugInt = 0

    init {
        Timber.i("FavoritesMain constructor constructor, debugInt: $debugInt")
    }

    companion object {
        fun newInstance() =
            FavoritesMain()
    }

    private lateinit var viewModel: FavoritesMainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.i("Fragment onAttach childFragmentManager: $childFragmentManager in $this")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debugInt++
        // Must not be called now as we are trying to achieve the saving of state via
        // FragmentManager#saveInstanceState
        // setRetainInstance(false)
        Timber.i("Fragment onCreate: $childFragmentManager in $this, debugInt: $debugInt")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SingletoneContextCounter.fragments++
        Timber.i("Fragment onCreateView: $childFragmentManager in $this, debugInt: $debugInt")
        Timber.i("in onCreateView: current fragment amount: ${SingletoneContextCounter.fragments}")
        return inflater.inflate(R.layout.favorites_main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("Nav controller: ${findNavController()}")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoritesMainViewModel::class.java)
        Timber.i("NavController : ${findNavController()}")
        setupCallbacks()
    }

    fun setupCallbacks() {
        btn_input.setOnClickListener {
            // Вот здесь экшон можно передавать ViewModel, чтобы тот его заполнил нужной датой.
            val action = FavoritesMainDirections
                .actionNavigationFavoritesToNavigationFavoritesSettings()
                .setMyArg(edit_text.text.toString())
            // Видимо в данный момент как primaryNavHost был установлен какой-то другой фрагмент, у которого контроллер
            // естественно не знал указанного destination. Хотя это устранно. У некоторых челов в инете может
            // такое возникать из-за Single Top, у других из-за использования navcontroller в колбеке, когда
            // операция по переходу уже была совершенно и фрагмент, откуда призывает контроллер, формально как бы уже ушел.
            // У меня походу именно второй случай, потому что когда я протыкиваю на одноим фрагменте pressBack
            // много раз, не происходит ничего, но зато когда я после этого херачу кнопку на фрагменте - тогда это всплывает.
            // Стек-то уже пуст, откуда действие совершать? Ничего не знаем, батенька, а вот вам и краш.
            // Чтобы такой проблемы не возникало, надо:
            // Во-первых, если в дочерних элементах стек и так единичный - предлагать выходить из проги
            // Во-вторых, на все фрагменты вешать слушатели изменения дестинейшнов, и при изменении с
            // текущего на другой - откреплять все колбеки, где используются navcontroller
            findNavController().navigate(action)
        }
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
