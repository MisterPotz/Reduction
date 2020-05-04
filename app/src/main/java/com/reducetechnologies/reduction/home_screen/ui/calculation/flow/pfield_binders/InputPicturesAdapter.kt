package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.FileStringUtils
import com.squareup.picasso.Picasso
import timber.log.Timber
import kotlin.properties.Delegates

class InputPicturesAdapter(val context: Context,
                           val preparedPaths: List<String>, val windowManager: WindowManager
) : RecyclerView.Adapter<InputPicturesAdapter.InputPictureHolder>() {
    private var inflater: LayoutInflater? = null
    private val picasso = Picasso.get()
    private var width = 0
    private var height = 0
    private var currentlySelected: Int? = null
    private var recyclerView: RecyclerView? = null

    init {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        width = size.x
        height = size.y
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputPictureHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val view = inflater!!.inflate(R.layout.input_picture_item, parent, false)
        return InputPictureHolder(view)
    }

    override fun getItemCount(): Int {
        return preparedPaths.size
    }

    override fun onBindViewHolder(holder: InputPictureHolder, position: Int) {
        Timber.i("Binding view holder : $position")
        holder.onBind(preparedPaths[position], position)
    }

    // overriding is necessary because its the best way to smoothly update incoming views
    override fun onViewAttachedToWindow(holder: InputPictureHolder) {
        holder.reattach()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    inner class InputPictureHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.picture)
        private val text = itemView.findViewById<TextView>(R.id.pictureTitle)
        private val card = itemView.findViewById<MaterialCardView>(R.id.inputPictureCard)
        private var positionOfHolder = 0
        private var path: String = ""

        init {
            card.setOnClickListener {
                Timber.i("Selected card of $positionOfHolder")
                currentlySelected = positionOfHolder
                reselectAll()
            }
        }

        // For smooth reselection of items
        fun reattach() {
            reselect()
            fetchPicture(path, width, height)
        }

        fun onBind(path: String, position: Int) {
            this.path = path
            image.visibility = View.VISIBLE
            positionOfHolder = position
            text.visibility = View.GONE
            reattach()
        }

        fun reselectAll() {
            for (i in preparedPaths.indices) {
                recyclerView?.findViewHolderForAdapterPosition(i)?.let {
                    Timber.i("Reselecting item $i")
                    (it as InputPictureHolder).reselect()
                }
            }
        }

        fun reselect() {
            currentlySelected?.let {
                Timber.i("In reselection of $positionOfHolder, positionOfHolder == currentlySelected ${positionOfHolder == it}")
                card.isChecked = positionOfHolder == it
            } ?: run { card.isChecked = false }
            Timber.i("Holder $positionOfHolder is checkable ${card.isCheckable} and is checked ${card.isChecked}")
        }

        private fun fetchPicture(preparedPath: String, width: Int, height: Int) {
        /*    val sharedOptions: RequestOptions = RequestOptions()
                .override((width.toDouble() / 2.0).toInt(), (height.toDouble() / 3.5).toInt())
                .fitCenter()
            Glide
                .with(context)
                .load(Uri.parse(FileStringUtils.pathAsPicassoImage(path)))
                .apply(sharedOptions)
                .into(image!!)*/
            picasso
                .load(preparedPath)
                .resize((width.toDouble() / 2.0).toInt(), (height.toDouble() / 3.5).toInt())
                .centerInside()
                .into(image!!)
        }
    }
}