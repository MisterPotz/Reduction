package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R

class SettingsItemsAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val navController: NavController
) :
    RecyclerView.Adapter<SettingsItemsAdapter.SettingsItemHolder>() {
    val items: List<String> =
        ItemToDestinationProvider.items.map { context.resources.getString(it.title) }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SettingsItemHolder, position: Int) {
        holder.text.text = items[position]
        setupCallback(holder.itemView, position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SettingsItemHolder {
        inflater.inflate(R.layout.settings_single_line_item, parent, false).let {
            return SettingsItemHolder(it)
        }
    }

    private fun setupCallback(view : View, position: Int) {
        view.setOnClickListener {
            ItemToDestinationProvider.items[position].actionId.let {
                navController.navigate(it)
            }
        }
    }

    class SettingsItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.itemName)
    }
}