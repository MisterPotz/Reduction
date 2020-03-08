package com.reducetechnologies.reduction.home_screen.ui.favorites.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.reducetechnologies.reduction.R
import kotlinx.android.synthetic.main.favorites_main_fragment.*

class FavoritesMain : Fragment() {
    companion object {
        fun newInstance() =
            FavoritesMain()
    }

    private lateinit var viewModel: FavoritesMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorites_main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoritesMainViewModel::class.java)
        setupCallbacks()
    }

    fun setupCallbacks() {
        btn_input.setOnClickListener {
            // Вот здесь экшон можно передавать ViewModel, чтобы тот его заполнил нужной датой.
            val action = FavoritesMainDirections
                .actionNavigationFavoritesToNavigationFavoritesSettings()
                .setMyArg(edit_text.text.toString())
            // TODO("вот здесь вещь один раз крашнулась таким образом: navigation destination
            //  com.reducetechnologies.reduction:id/action_navigation_favorites_to_navigation_favorites_settings
            //  is unknown to this NavController")
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
}
