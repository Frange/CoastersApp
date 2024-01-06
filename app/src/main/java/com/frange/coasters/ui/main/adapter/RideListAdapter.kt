package com.frange.coasters.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
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

            if (ride.waitTime!! < 0) {
                itemBinding.time.text = "CLOSED"
                itemBinding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_red_dark
                    )
                )
            } else {
                itemBinding.time.text = if (ride.isOpen) {
                    ride.waitTime.toString()
                } else {
                    "CLOSED"
                }
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
