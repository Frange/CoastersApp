package com.frange.coasters.ui.widget

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
import com.frange.coasters.R
import com.frange.coasters.data.api.model.coaster.CoasterModel
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.base.Status
import com.frange.coasters.domain.model.Ride
import com.frange.coasters.domain.usecase.RequestCoasterUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.stream.Collectors.toList
import javax.inject.Inject

@AndroidEntryPoint
open class WidgetCoasterListProvider : AppWidgetProvider() {

    @Inject
    lateinit var queueRepository: QueueRepository

    @Inject
    lateinit var requestCoasterUseCase: RequestCoasterUseCase

    @Inject
    lateinit var coasterModel: CoasterModel

    private var views: RemoteViews? = null
    private var widgetContext: Context? = null

    companion object {
        private const val SYNC_CLICKED = "automaticWidgetSyncButtonClick"
        private const val ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"
        private const val WIDGET_IDS_KEY = "mywidgetproviderwidgetids"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.hasExtra(WIDGET_IDS_KEY) == true) {
            onUpdateList(context)
        } else {
            super.onReceive(context, intent)
            if (intent?.action != null) {
//                    &&
//                    (intent.action == SYNC_CLICKED || intent.action) == ACTION_APPWIDGET_UPDATE) {
                onUpdateList(context)
            }
        }
    }

    private fun fetchRides(): Flow<List<Ride>> = flow {
        queueRepository.requestRideList().collect { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    emit(result.data ?: emptyList())
                }
                Status.ERROR, Status.LOADING, Status.EXCEPTION -> {
                }
            }
        }
    }

    private fun onUpdateList(context: Context?) {
        widgetContext = context

        CoroutineScope(Dispatchers.Main).launch {
            try {
                fetchRides().collect { rideList ->
                    if (widgetContext != null) {
                        WidgetSaveModel.saveData(widgetContext!!, rideList)
                        val appWidgetManager = AppWidgetManager.getInstance(widgetContext)
                        val appWidgetIds = appWidgetManager.getAppWidgetIds(
                            ComponentName(widgetContext!!, WidgetCoasterListProvider::class.java)
                        )
                        appWidgetIds.forEach { appWidgetId ->
                            if (views == null) {
                                val intent =
                                    Intent(widgetContext, WidgetRenderService::class.java).apply {
                                        data =
                                            Uri.fromParts("content", appWidgetId.toString(), null)
                                    }

                                widgetContext?.let {
                                    views = generateWidgetViews(intent)
                                }
                            }

                            if (views != null) {
                                appWidgetManager.updateAppWidget(appWidgetId, views)
                            }
                        }
                        appWidgetManager.notifyAppWidgetViewDataChanged(
                            appWidgetIds,
                            R.id.lv_widget_list
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MY_WIDGET", "Error al obtener datos de la API: $e")
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            widgetContext = context

            val intent = Intent(context, WidgetRenderService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }

            views = generateWidgetViews(intent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun generateWidgetViews(intent: Intent): RemoteViews {
        return RemoteViews(widgetContext!!.packageName, R.layout.widget_big).apply {
            setRemoteAdapter(R.id.lv_widget_list, intent)
            setEmptyView(R.id.lv_widget_list, R.id.tv_widget_empty)

            setViewVisibility(R.id.ll_widget_refresh, View.VISIBLE)
            setViewVisibility(R.id.tv_widget_title, View.VISIBLE)

            setOnClickPendingIntent(
                R.id.ll_widget_refresh,
                getRefreshIntent(widgetContext!!)
            )

            setOnClickPendingIntent(
                R.id.tv_widget_title,
                getPrivateMainIntent(widgetContext!!)
            )
        }
    }


    // ---------------------- CLICKS

    private fun getPrivateMainIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(widgetContext, 0, intent, flag)
    }

    private fun getRefreshIntent(context: Context): PendingIntent {
        val intent = Intent(widgetContext, javaClass).apply {
            action = SYNC_CLICKED
        }
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(context, 0, intent, flag)
    }
}
