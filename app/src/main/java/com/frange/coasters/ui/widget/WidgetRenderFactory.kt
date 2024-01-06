package com.frange.coasters.ui.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.frange.coasters.R
import com.frange.coasters.domain.model.Ride

class WidgetRenderFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private var rideList: List<Ride> = emptyList()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        rideList = WidgetSaveModel.loadData(context)
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return rideList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val ride = rideList[position]
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_list_item)

        remoteViews.setTextViewText(R.id.tv_widget_ride_name, ride.name)

        if (ride.waitTime!! < 0) {
            remoteViews.setTextViewText(
                R.id.tv_widget_ride_time,
                "CLOSED"
            )
        } else {
            remoteViews.setTextViewText(
                R.id.tv_widget_ride_time,
                if (ride.isOpen) {
                    ride.waitTime.toString()
                } else {
                    "CLOSED"
                }
            )
        }

        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? {
        val views = RemoteViews(context.packageName, R.layout.widget_loading)
        views.setTextViewText(R.id.tv_loading, "Cargando...")

        return views
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}