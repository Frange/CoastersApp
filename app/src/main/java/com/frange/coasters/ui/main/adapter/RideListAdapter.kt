package com.frange.coasters.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.frange.coasters.databinding.RowMainBinding
import com.frange.coasters.domain.model.Ride

class RideListAdapter(
    private val context: Context,
    private val rideList: List<Ride>?,
    private val listener: ClickItemListener
) : RecyclerView.Adapter<RideListAdapter.RideHolder>() {

    interface ClickItemListener {
        fun onClicked(ride: Ride)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RideHolder {

        val itemBinding = RowMainBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RideHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RideHolder, position: Int) {
        if (!rideList.isNullOrEmpty() && position <= rideList.size) {
            val ride: Ride = rideList[position]
            holder.bind(ride)
        }
    }

    inner class RideHolder(
        private val itemBinding: RowMainBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(ride: Ride) {
            itemBinding.name.text = ride.name

            if (ride.isOpen && ride.waitTime != null && ride.waitTime > 0) {
                itemBinding.time.text = ride.waitTime.toString()
                itemBinding.time.visibility = VISIBLE
                itemBinding.close.visibility = GONE
            } else {
                itemBinding.time.text = ""
                itemBinding.time.visibility = GONE
                itemBinding.close.visibility = VISIBLE
            }

            onClick(ride)
        }

        private fun onClick(ride: Ride) {
            itemBinding.root.setOnClickListener {
                listener.onClicked(ride)
            }
        }
    }

    override fun getItemCount(): Int = if (rideList.isNullOrEmpty()) 0 else rideList.size

}
