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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.reducetechnologies.command_infrastructure.PTypeSpecific
import com.reducetechnologies.command_infrastructure.PictureDataTable
import com.reducetechnologies.command_infrastructure.PictureSpec
import com.reducetechnologies.command_infrastructure.PictureStringPath
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.FileStringUtils
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldBinder
import com.squareup.picasso.Picasso
import timber.log.Timber


class PictureBinder(val context: Context, val windowManager: WindowManager) : PFieldBinder {
    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var itemView: View
    private var specific: PictureSpec? = null
    private lateinit var callback : (View, Int) -> Unit
    override fun onAttach() {
        itemView.visibility = View.VISIBLE
        fetchImage()
    }

    override fun init(view: View) {
        itemView = view
        image = view.findViewById(R.id.picture)
        title = view.findViewById(R.id.pictureTitle)
        image.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Timber.i("Calling callback to change data set")
           /* if (right <= oldRight) {
                callback(v, 0)
            }*/
        }
    }


    override fun bind(spec: PTypeSpecific) {
        specific = spec as PictureSpec
        title.text = "Some picture"
        fetchImage()
        Timber.i("Picture binded")
    }

    private fun fetchImage() {
        val source = specific!!.source
        when (source) {
            is PictureStringPath -> loadFromPath(source.string)
            is PictureDataTable -> TODO("not yet implemented")
        }
    }

    override fun setCallback(callback: (View, Int) -> Unit) {
        this.callback = callback
    }

    private fun loadFromPath(path: String) {
        val picasso = Picasso.get()
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        val width = size.x
        val height = size.y
        val sharedOptions: RequestOptions = RequestOptions()
            .override((width.toDouble() / 1.2).toInt(), (height.toDouble() / 3).toInt())
            .fitCenter()
        Glide
            .with(context)
            .load(Uri.parse(FileStringUtils.pathAsPicassoImage(path)))
            .apply(sharedOptions)
            .into(image!!)
    }

    companion object : CompanionInflater {
        override fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup): View {
            return inflater.inflate(R.layout.picture_pfield, viewGroup, false)
        }
    }
}