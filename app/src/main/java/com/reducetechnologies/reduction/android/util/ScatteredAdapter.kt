package com.reducetechnologies.reduction.android.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.helpers.Repository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * @param CoroutineScope определяется активностью / приложением, где используется адаптер.
 * контекст нужен для работы с базой данных
 */
// TODO чувак который получает нужные записи по тегам
class ScatteredAdapter(
    CoroutineScope : CoroutineScope,
    private val inflater: LayoutInflater
) : RecyclerView.Adapter<ScatteredItemHolder>() {
    // при создании нужно получить вообще количество итемов
    val some = Dispatchers.Main
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScatteredItemHolder {
        val view = createViewBasedOnType(getOrientation(viewType), parent)
        return ScatteredItemHolder(getOrientation(viewType), view, inflater)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            HolderItemsOrientation.SINGLE_BOTTOM.ordinal
        } else {
            HolderItemsOrientation.SINGLE_TOP.ordinal
        }
    }

    fun getOrientation(ordinal: Int): HolderItemsOrientation {
        return HolderItemsOrientation.values().find { it.ordinal == ordinal }!!
    }

    fun createViewBasedOnType(orientation: HolderItemsOrientation, parent: ViewGroup): View {
        return when (orientation) {
            HolderItemsOrientation.SINGLE_BOTTOM -> inflater.inflate(
                R.layout.holder_two_top_one_bottom,
                parent,
                false
            )
            HolderItemsOrientation.SINGLE_TOP -> inflater.inflate(
                R.layout.holder_one_top_two_bottom,
                parent,
                false
            )
        }
    }

    override fun onBindViewHolder(holder: ScatteredItemHolder, position: Int) {
        // TODO получить нужные итемы из сохранки
        //holder.onBind()
    }
}