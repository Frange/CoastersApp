package com.frange.coasters.ui.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.frange.coasters.data.repository.queue.QueueRepository
import com.frange.coasters.ui.main.MainActivity
import com.frange.coasters.ui.widget.render.WidgetRenderService
import com.frange.coasters.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class CoasterListWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var queueRepository: QueueRepository

    private var views: RemoteViews? = null
    private var widgetContext: Context? = null

    companion object {
        private const val SYNC_CLICKED = "automaticWidgetSyncButtonClick"
        private const val ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"

        private const val WIDGET_IDS_KEY = "mywidgetproviderwidgetids"
    }

    //================================== APP WIDGET PROVIDER ======================================

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("MY_WIDGET", "CoasterListWidgetProvider - onReceive")
        if (intent?.hasExtra(WIDGET_IDS_KEY) == true) {
            Log.v("MY_WIDGET", "onReceive - REFRESH DATA")
            onUpdateList(context, false)
        } else {
            super.onReceive(context, intent)
            if (intent?.action != null) {
//                    &&
//                    (intent.action == SYNC_CLICKED || intent.action) == ACTION_APPWIDGET_UPDATE) {
                Log.v("MY_WIDGET", "onReceive - BUTTON CLICKED - REFRESH DATA")
                onUpdateList(context, false)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val intent = Intent(context, WidgetRenderService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }

            views = RemoteViews(context.packageName, R.layout.widget_big).apply {
                setRemoteAdapter(R.id.lv_widget_list, intent)
                setEmptyView(R.id.lv_widget_list, R.id.emptyView)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun onUpdateList(aContext: Context?, forListView: Boolean) {
        widgetContext = aContext
        val aList = queueRepository.getCurrentCoasterList().rideList!!
        if (widgetContext != null) {
            WidgetSaveModel.saveData(widgetContext, aList)
            val appWidgetManager = AppWidgetManager.getInstance(widgetContext)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(widgetContext!!, CoasterListWidgetProvider::class.java)
            )
            updateAllAppWidget(widgetContext!!, appWidgetManager, appWidgetIds)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_list)
        }
    }

    private fun updateAllAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            if (views == null) {
                widgetContext = context
                views = getWidgetViews(appWidgetId)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    //============================= VIEWS =========================================================
    @SuppressLint("RemoteViewLayout")
    private fun getWidgetViews(appWidgetId: Int): RemoteViews? {
        views = RemoteViews(widgetContext!!.packageName, R.layout.widget_big)
        views!!.setViewVisibility(R.id.tv_widget_title, View.VISIBLE)
        views!!.setEmptyView(R.id.lv_widget_list, R.id.tv_widget_empty)

        val toastIntent = Intent(widgetContext, CoasterListWidgetProvider::class.java)
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1)

        views!!.setPendingIntentTemplate(
            R.id.lv_widget_list,
            getPrivateMainIntent(widgetContext!!)
        )
        views!!.setOnClickPendingIntent(
            R.id.tv_widget_title,
            getPrivateMainIntent(widgetContext!!)
        )
        val manager = AppWidgetManager.getInstance(widgetContext)
        manager.updateAppWidget(appWidgetId, views)

        views!!.setViewVisibility(R.id.ll_widget_refresh, View.VISIBLE)
        views!!.setOnClickPendingIntent(R.id.ll_widget_refresh, getRefreshIntent(widgetContext))

        val intent = Intent(widgetContext, WidgetRenderService::class.java)
        intent.data = Uri.fromParts("content", appWidgetId.toString(), null)
        views!!.setRemoteAdapter(R.id.lv_widget_list, intent)
        return views
    }

    private fun getPrivateMainIntent(context: Context): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val flag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(
            widgetContext, 0,
            intent, flag
        )
    }

    private fun getRefreshIntent(context: Context?): PendingIntent? {
        val intent = Intent(widgetContext, javaClass)
        intent.action = SYNC_CLICKED
        val flag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(context, 0, intent, flag)
    }

}