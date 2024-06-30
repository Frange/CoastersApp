package com.frange.coasters.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frange.coasters.R
import com.frange.coasters.domain.model.Land
import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.model.Ride

class ParkListAdapter(
    private val park: Park
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_LAND = 0
        private const val VIEW_TYPE_RIDE = 1
        private const val VIEW_TYPE_HEADER = 2
    }

    private val items: MutableList<Any> = mutableListOf()
    private val expandedLands = mutableSetOf<Int>()
    private var isCategoryExpanded = true // Set to true to expand "Sin categoría" by default

    init {
        // Expand all lands by default
        park.landList?.forEach { land ->
            expandedLands.add(land.id)
        }
        populateItems()
    }

    private fun populateItems() {
        items.clear()
        park.landList?.forEach { land ->
            items.add(land)
            if (expandedLands.contains(land.id)) {
                land.rideList?.let { items.addAll(it) }
            }
        }
        val rideList = park.rideList
        if (!rideList.isNullOrEmpty()) {
            items.add("Sin categoría")
            if (isCategoryExpanded) {
                items.addAll(rideList)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Land -> VIEW_TYPE_LAND
            is Ride -> VIEW_TYPE_RIDE
            is String -> VIEW_TYPE_HEADER
            else -> throw IllegalArgumentException("Unknown item type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LAND -> {
                val view = inflater.inflate(R.layout.item_land, parent, false)
                LandViewHolder(view)
            }
            VIEW_TYPE_RIDE -> {
                val view = inflater.inflate(R.layout.item_ride, parent, false)
                RideViewHolder(view)
            }
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is LandViewHolder -> if (item is Land) {
                holder.bind(item, expandedLands.contains(item.id))
            }
            is RideViewHolder -> if (item is Ride) {
                holder.bind(item)
            }
            is HeaderViewHolder -> if (item is String) {
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class LandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val landNameTextView: TextView = itemView.findViewById(R.id.land_name)

        fun bind(land: Land, isExpanded: Boolean) {
            landNameTextView.text = land.name
            landNameTextView.setOnClickListener {
                if (isExpanded) {
                    expandedLands.remove(land.id)
                } else {
                    expandedLands.add(land.id)
                }
                populateItems()
            }
        }
    }

    class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rideNameTextView: TextView = itemView.findViewById(R.id.ride_name)

        fun bind(ride: Ride) {
            rideNameTextView.text = ride.name
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTextView: TextView = itemView.findViewById(R.id.headerTextView)

        fun bind(headerText: String) {
            headerTextView.text = headerText
            headerTextView.setOnClickListener {
                isCategoryExpanded = !isCategoryExpanded
                populateItems()
            }
        }
    }
}
