package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders.InputTextBinder
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders.TextFieldBinder

interface PFieldBinder {
    fun bind(spec: PTypeSpecific, view: View)
}

class PFieldHolder(val pFieldBinder: PFieldBinder, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    fun onBind(spec: PTypeSpecific) {
        pFieldBinder.bind(spec, view = itemView)
    }
}

class PFieldAdapter(val pScreen: PScreen) : RecyclerView.Adapter<PFieldHolder>() {
    private var inflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PFieldHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val type = viewType.toPFieldType()
        val view = getView(type, parent)
        return PFieldHolder(getPFieldBinder(type), view)
    }

    override fun getItemCount(): Int {
        return pScreen.fields.size
    }

    override fun getItemViewType(position: Int): Int {
        return pScreen.fields[position].pFieldType.ordinal
    }

    override fun onBindViewHolder(holder: PFieldHolder, position: Int) {
        holder.onBind(pScreen.fields[position].typeSpecificData)
    }

    private fun getPFieldBinder(type: PFieldType): PFieldBinder {
        return when (type) {
            PFieldType.TEXT -> TextFieldBinder()
            PFieldType.INPUT_TEXT -> InputTextBinder()
            else -> TODO("not all cases implemented")
        }
    }

    private fun getView(type: PFieldType, root: ViewGroup): View {
        return when (type) {
            PFieldType.TEXT -> TextFieldBinder.inflate(inflater!!, root)
            PFieldType.INPUT_TEXT -> InputTextBinder.inflate(inflater!!, root)
            else -> TODO("not all cases implemented")
        }
    }
}