package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.reducetechnologies.reduction.R
import kotlinx.android.synthetic.main.settings_full.*


class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.settings_full, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsItems.adapter = SettingsItemsAdapter(ItemToDestinationProvider.items, context!!) {
            findNavController().navigate(it)
        }
        settingsItems.layoutManager = LinearLayoutManager(context!!)
        super.onViewCreated(view, savedInstanceState)
    }
}