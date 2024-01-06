package com.frange.coasters.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetRenderService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetRenderFactory(applicationContext, intent)
    }
}