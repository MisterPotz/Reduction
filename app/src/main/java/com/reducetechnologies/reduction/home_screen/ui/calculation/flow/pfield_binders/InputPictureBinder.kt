package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.command_infrastructure.InputPictureSpec
import com.reducetechnologies.command_infrastructure.PTypeSpecific
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.FileStringUtils
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldBinder

class InputPictureBinder(val context: Context, val windowManager: WindowManager): PFieldBinder {
    private lateinit var pictures : RecyclerView
    private lateinit var title: TextView
    private lateinit var counter: TextView
    private var inputPictureSpec : InputPictureSpec?= null
    private var adapter : InputPicturesAdapter? = null
    private var layoutManager : RecyclerView.LayoutManager? = null

    override fun bind(spec: PTypeSpecific) {
        inputPictureSpec = spec as InputPictureSpec
        title.text = inputPictureSpec!!.title
        inputPictureSpec!!.additional.answer?.let { onSelected(it) }
        adapter = InputPicturesAdapter(context,prepareStrings(), windowManager, inputPictureSpec!!, this::onSelected)
        layoutManager = LinearLayoutManager(pictures.context, RecyclerView.HORIZONTAL, false)
        pictures.adapter = adapter
        pictures.setHasFixedSize(true)
        pictures.layoutManager = layoutManager
    }

    override fun init(view: View) {
        pictures = view.findViewById(R.id.selectablePictures)
        title = view.findViewById(R.id.inputPictureTitle)
        counter = view.findViewById(R.id.selected)
    }

    private fun prepareStrings() : List<String> {
        // getting image paths for picasso
        return inputPictureSpec!!.additional.imagePaths.map {
            FileStringUtils.pathAsPicassoImage(it)
        }
    }

    private fun onSelected(selected : Int?) {
        counter.text = selected?.let { (it + 1).toString() } ?: ""
    }

    companion object : CompanionInflater {
        override fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup): View {
            return inflater.inflate(R.layout.input_picture_pfield, viewGroup, false)
        }
    }
}