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
import com.frange.coasters.ui.main.MainActivity
import com.frange.coasters.R
import com.frange.coasters.data.store.PreferenceManager
import com.frange.coasters.domain.base.Status
import com.frange.coasters.domain.usecase.RequestParkUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetCoasterListProvider : AppWidgetProvider() {

    @Inject lateinit var requestParkUseCase: RequestParkUseCase
    @Inject lateinit var prefManager: PreferenceManager

    companion object {
        private const val SYNC_CLICKED = "com.frange.coasters.ACTION_WIDGET_REFRESH"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == SYNC_CLICKED || intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            onUpdateList(context)
        }
    }

    private fun onUpdateList(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = prefManager.userPreferencesFlow.first()
                val parkId = prefs.lastSelectedParkId ?: return@launch

                requestParkUseCase.execute(RequestParkUseCase.Parameters(parkId))
                    .filter { it.status == Status.SUCCESS }
                    .firstOrNull()?.data?.let { park ->

                        val rides = park.rideList ?: emptyList()
                        WidgetSaveModel.saveData(context, rides)

                        val appWidgetManager = AppWidgetManager.getInstance(context)
                        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, WidgetCoasterListProvider::class.java))

                        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.lv_widget_list)
                    }
            } catch (e: Exception) {
                Log.e("WIDGET_ERROR", "Error updating widget: $e")
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            val intent = Intent(context, WidgetRenderService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }

            val views = RemoteViews(context.packageName, R.layout.widget_big).apply {
                setRemoteAdapter(R.id.lv_widget_list, intent)
                setEmptyView(R.id.lv_widget_list, R.id.tv_widget_empty)
                setOnClickPendingIntent(R.id.ll_widget_refresh, getRefreshIntent(context))
                setOnClickPendingIntent(R.id.tv_widget_title, getPrivateMainIntent(context))
            }
            appWidgetManager.updateAppWidget(id, views)
        }
        onUpdateList(context)
    }

    private fun getPrivateMainIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getRefreshIntent(context: Context): PendingIntent {
        val intent = Intent(context, WidgetCoasterListProvider::class.java).apply { action = SYNC_CLICKED }
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}