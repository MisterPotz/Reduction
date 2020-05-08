package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reducetechnologies.reduction.R
import timber.log.Timber

typealias DestinationSelectedCallback = (destinationId: Int) -> Unit

class SettingsItemsAdapter(
    private val list: List<ItemToDestination>,
    private val context: Context,
    private val onItemSelected: DestinationSelectedCallback
) :
    RecyclerView.Adapter<SettingsItemsAdapter.SettingsItemHolder>() {

    val inflater = LayoutInflater.from(context)
    override fun getItemCount(): Int {
        Timber.i("Items amount ${list.size}")
        return list.size
    }

    override fun onBindViewHolder(holder: SettingsItemHolder, position: Int) {
        holder.text.text = context.getString(list[position].title)
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
                onItemSelected(it)
            }
        }
    }

    class SettingsItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.itemName)
    }
}