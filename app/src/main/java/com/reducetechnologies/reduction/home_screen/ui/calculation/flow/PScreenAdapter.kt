package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders.*
import java.lang.IllegalStateException

interface PFieldBinder {
    fun bind(spec: PTypeSpecific)
    fun init(view: View)
    fun onAttach() {}
    fun setCallback(callback: (View, Int) -> Unit ) {}
    fun setInputable(inputable: Boolean) { }
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
    fun setInputable(inputable : Boolean) {
        pFieldBinder.setInputable(inputable)
    }
}

class PFieldAdapter(
    val context: Context,
    val pScreen: PScreen,
    val displayMetrics: DisplayMetrics,
    val windowManager: WindowManager,
    val inputable: Boolean) :
    RecyclerView.Adapter<PFieldHolder>() {
    private var inflater: LayoutInflater? = null
    private var recyclerView: RecyclerView? = null
    private var links : HashMap<Destination, LinkCalledCallback>? = null

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
        // setting up a link if it contains any links
        if (pScreen.fields[position].pFieldType == PFieldType.LINK) {
            findCallbackForLink(pScreen.fields[position].typeSpecificData)
        }
        // binding
        holder.onBind(pScreen.fields[position].typeSpecificData)
    }

    override fun onViewAttachedToWindow(holder: PFieldHolder) {
        holder.onAttach()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun setupLinks(links: HashMap<Destination, LinkCalledCallback>?) {
        this.links = links
    }

    private fun findCallbackForLink(spec: PTypeSpecific) {
        if (spec !is LinkSpec) {
            throw IllegalStateException("Is not destination, cannot setyp")
        }
        spec.linkCalledCallback = links!![spec.where]
    }

    private fun getPFieldBinder(type: PFieldType): PFieldBinder {
        return when (type) {
            PFieldType.TEXT -> TextFieldBinder()
            PFieldType.INPUT_TEXT -> InputTextBinder(inputable)
            PFieldType.PICTURE -> PictureBinder(
                context,
                windowManager
            ).also { it.setCallback(this::callback) }
            PFieldType.INPUT_PICTURE -> InputPictureBinder(context, windowManager, inputable)
            PFieldType.INPUT_LIST -> InputListBinder(inputable)
            PFieldType.MATH_TEXT -> MathTextBinder(14, displayMetrics)
            PFieldType.LINK -> LinkSpecBinder()
        }
    }

    private fun getView(type: PFieldType, root: ViewGroup): View {
        return when (type) {
            PFieldType.TEXT -> TextFieldBinder.inflate(inflater!!, root)
            PFieldType.INPUT_TEXT -> InputTextBinder.inflate(inflater!!, root)
            PFieldType.PICTURE -> PictureBinder.inflate(inflater!!, root)
            PFieldType.INPUT_PICTURE -> InputPictureBinder.inflate(inflater!!, root)
            PFieldType.INPUT_LIST -> InputListBinder.inflate(inflater!!, root)
            PFieldType.MATH_TEXT -> MathTextBinder.inflate(inflater!!, root)
            PFieldType.LINK -> LinkSpecBinder.inflate(inflater!!, root)
        }
    }

    private fun callback(view: View, position: Int): Unit {
        notifyDataSetChanged()
    }
}