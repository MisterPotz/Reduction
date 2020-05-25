package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.pfield_binders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.reducetechnologies.command_infrastructure.LinkSpec
import com.reducetechnologies.command_infrastructure.PTypeSpecific
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.home_screen.ui.calculation.flow.PFieldBinder
import timber.log.Timber

class LinkSpecBinder : PFieldBinder {
    private lateinit var linkButtonView: Button
    private var spec: LinkSpec? = null

    override fun bind(spec: PTypeSpecific) {
        this.spec = spec as LinkSpec

        linkButtonView.setText(spec.text)
        linkButtonView.setOnClickListener {
            Timber.i("Calling callback from linkButton")
            this.spec!!.linkCalledCallback!!()
        }
    }

    override fun init(view: View) {
        linkButtonView = view.findViewById(R.id.linkButton)
    }

    companion object : CompanionInflater {
        override fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup): View {
            return inflater.inflate(R.layout.pfield_link, viewGroup, false)
        }
    }
}