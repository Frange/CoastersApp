package com.frange.coasters.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.frange.coasters.R
import com.frange.coasters.domain.model.Ride

class WidgetRenderFactory(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private var rideList: List<Ride> = emptyList()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        rideList = WidgetSaveModel.loadData(context)
    }

    override fun onDestroy() {
        rideList = emptyList()
    }

    override fun getCount(): Int = rideList.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= rideList.size) {
            return RemoteViews(context.packageName, R.layout.widget_loading)
        }

        val ride = rideList[position]
        val views = RemoteViews(context.packageName, R.layout.widget_list_item)

        val backgroundRes = if (ride.isFavourite) {
            R.color.widget_row_fav_background
        } else {
            R.color.widget_row_background
        }

        views.setInt(R.id.ll_widget_name_item, "setBackgroundResource", backgroundRes)
        views.setTextViewText(R.id.tv_widget_ride_name, ride.name)

        if (ride.isOpen) {
            val minutes = ride.waitTime ?: 0
            views.setTextViewText(R.id.tv_widget_ride_time, "$minutes MIN")

            val colorResId = when {
                minutes <= 15 -> R.color.wait_low
                minutes < 45 -> R.color.wait_medium
                else -> R.color.wait_high
            }

            views.setInt(R.id.ll_widget_time_item, "setBackgroundResource", colorResId)
        } else {
            views.setTextViewText(R.id.tv_widget_ride_time, "CLOSED")
            views.setInt(
                R.id.ll_widget_time_item,
                "setBackgroundResource",
                context.getColor(R.color.always_red)
            )
        }

        val fillInIntent = Intent()
        views.setOnClickFillInIntent(R.id.ll_widget_item, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_loading)

    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}