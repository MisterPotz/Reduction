package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders.InputPictureBinder
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders.InputTextBinder
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders.PictureBinder
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders.TextFieldBinder
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

interface PFieldBinder {
    fun bind(spec: PTypeSpecific)
    fun init(view: View)
    fun onAttach() {}
    fun setCallback(callback: (View, Int) -> Unit ) {}
}

class PFieldHolder(
    val pFieldBinder: PFieldBinder,
    itemView: View
) :
    RecyclerView.ViewHolder(itemView) {
    init {
        pFieldBinder.init(itemView)
    }

    fun onBind(spec: PTypeSpecific) {
        pFieldBinder.bind(spec)
    }

    fun onAttach() {
        pFieldBinder.onAttach()
    }
}

class PFieldAdapter(val context: Context, val pScreen: PScreen, val windowManager: WindowManager) :
    RecyclerView.Adapter<PFieldHolder>() {
    private var inflater: LayoutInflater? = null
    private var recyclerView: RecyclerView? = null
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
        Timber.i("Binding higher order items $position")
        holder.onBind(pScreen.fields[position].typeSpecificData)
    }

    override fun onViewAttachedToWindow(holder: PFieldHolder) {
        holder.onAttach()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    private fun getPFieldBinder(type: PFieldType): PFieldBinder {
        return when (type) {
            PFieldType.TEXT -> TextFieldBinder()
            PFieldType.INPUT_TEXT -> InputTextBinder()
            PFieldType.PICTURE -> PictureBinder(context, windowManager).also { it.setCallback(this::callback) }
            PFieldType.INPUT_PICTURE -> InputPictureBinder(context, windowManager)
            else -> TODO("not all cases implemented")
        }
    }

    private fun getView(type: PFieldType, root: ViewGroup): View {
        return when (type) {
            PFieldType.TEXT -> TextFieldBinder.inflate(inflater!!, root)
            PFieldType.INPUT_TEXT -> InputTextBinder.inflate(inflater!!, root)
            PFieldType.PICTURE -> PictureBinder.inflate(inflater!!, root)
            PFieldType.INPUT_PICTURE -> InputPictureBinder.inflate(inflater!!, root)
            else -> TODO("not all cases implemented")
        }
    }

    private fun callback(view: View, position: Int): Unit {
        notifyDataSetChanged()
    }
}