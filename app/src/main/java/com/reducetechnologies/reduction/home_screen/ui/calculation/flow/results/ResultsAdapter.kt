package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import timber.log.Timber

typealias OnItemSelectedCallback = (Int) -> Unit

class ResultsAdapter(
    val inflater: LayoutInflater,
    val size: Int,
    val callback: OnItemSelectedCallback
    ) : RecyclerView.Adapter<ResultsAdapter.ResultHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultHolder {
        return ResultHolder(inflater.inflate(R.layout.pfield_link, parent, false))
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: ResultHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ResultHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private var index : Int = 0
        private val buttonView : Button = itemView.findViewById(R.id.linkButton)
        init {
            buttonView.setOnClickListener {
                Timber.v("Selected result: $index")
                callback(index)
            }
        }
        fun onBind(i : Int) {
            index = i
            val string = inflater.context.getString(R.string.see_result, i)
            buttonView.setText(string)
        }
    }
}