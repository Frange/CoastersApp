package com.frange.coasters.ui.widget.bk;

//public class BKWidgetProvider extends AppWidgetProvider {
//
//    @Inject CoastersApp appData;
//    @Inject LoadCredentialsUseCase loadCredentialsUseCase;
//
//    private static final String SYNC_CLICKED = "automaticWidgetSyncButtonClick";
//    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";
//    private static final String ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
//
//    private ApplicationComponent appComponent;
//    private BKWidgetService BKWidgetService;
//    private RemoteViews views;
//    private Context context;
//
//    //================================== DAGGER ===================================================
//
//    private void injectProvider() {
//        if (appComponent == null) {
//            ((BrokerApplication) getApplicationContext()).getProviderInjector(this).inject(this);
//            appComponent = ((BrokerApplication) getApplicationContext()).getApplicationComponent();
//            BKWidgetService = new BKWidgetService(((BrokerApplication) getApplicationContext()));
//        }
//    }
//
//    //================================== APP WIDGET PROVIDER ======================================
//
//    @Override
//    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
//                                          int appWidgetId, Bundle newOptions) {
//        LogUtils.log(Tag.WIDGET, "onAppWidgetOptionsChanged");
//        injectProvider();
//        BKWidgetService.onUpdateList(context, true, this);
//        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
//    }
//
//    //====================== NEW METHOS FOR UPDATE WIDGET DATA ===================================================
//
//    public static final String WIDGET_IDS_KEY = "mywidgetproviderwidgetids";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        LogUtils.log(Tag.WIDGET, "onReceive");
//        if (intent != null && BrokerApplication.isServiceUnavailable()) {
//            injectProvider();
//            if (intent.hasExtra(WIDGET_IDS_KEY)) {
//                LogUtils.log(Tag.WIDGET, "onReceive - REFRESH DATA");
//                BKWidgetService.onUpdateList(context, false, this);
//            } else {
//                super.onReceive(context, intent);
//                if (intent.getAction() != null) {
//                    if (BKWidgetService != null &&
//                            ((intent.getAction().equals(SYNC_CLICKED) ||
//                                    intent.getAction().equals(ACTION_APPWIDGET_UPDATE)))) {
//                        LogUtils.log(Tag.WIDGET, "onReceive - BUTTON CLICKED - REFRESH DATA");
//                        BKWidgetService.onUpdateList(context, false, this);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
//                         int[] appWidgetIds) {
//        update(context, appWidgetManager, appWidgetIds, null);
//    }
//
//    //This is where we do the actual updating
//    public void update(Context context, AppWidgetManager manager, int[] ids, Favourites data) {
//        //data will contain some predetermined data, but it may be null
//        for (int widgetId : ids) {
//            //Update Widget here
//            if (views == null) {
//                views = getViewForBiggerWidget(context, widgetId);
//            }
//            manager.updateAppWidget(widgetId, views);
//        }
//    }
//
//    public static void updateMyWidgets(Context context) {
//        AppWidgetManager man = AppWidgetManager.getInstance(context);
//        int[] ids = man.getAppWidgetIds(
//                new ComponentName(context, BKWidgetProvider.class));
//        Intent updateIntent = new Intent();
//        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        updateIntent.putExtra(BKWidgetProvider.WIDGET_IDS_KEY, ids);
////        updateIntent.putExtra(BKWidgetProvider.WIDGET_DATA_KEY, data);
//        context.sendBroadcast(updateIntent);
//    }
//
//
//    //====================== UPDATE WIDGET DATA ===================================================
//
//    //UPDATE ALL
//    public void updateAllAppWidget(Context context, AppWidgetManager appWidgetManager,
//                                   int[] appWidgetIds) {
//        LogUtils.log(Tag.WIDGET, "updateAllAppWidget");
//        for (int appWidgetId : appWidgetIds) {
//            if (views == null) {
//                views = getViewForBiggerWidget(context, appWidgetId);
//            }
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }
//    }
//
//
//    //============================= VIEWS =========================================================
//
//    //BIG
//    private RemoteViews getViewForBiggerWidget(Context context, int appWidgetId) {
//        LogUtils.log(Tag.WIDGET, "getViewForBiggerWidget");
//        views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_big);
//        views.setViewVisibility(R.id.tv_widget_title, View.VISIBLE);
//        views.setEmptyView(R.id.lv_widget_list, R.id.tv_widget_empty);
//
//        Intent toastIntent = new Intent(context, BKWidgetProvider.class);
//        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
//
//        this.context = context;
//        loadCredentialsUseCase.execute(null, new LoadCredentialsUseCase.Callback() {
//            @Override
//            public void onSuccessLoadCredentialsUseCase(@Nullable Credentials credentials) {
////                if (hasSession(credentials)) {
//                if (hasHeaders()) {
////                        LogUtils.log(Tag.WIDGET, "----------- Has headers");
//                    views.setPendingIntentTemplate(R.id.lv_widget_list, getPrivateMainIntent(context));
//                    views.setOnClickPendingIntent(R.id.tv_widget_title, getPrivateMainIntent(context));
////                    } else {
////                        LogUtils.log(Tag.WIDGET, "----------- No headers");
////                        views.setPendingIntentTemplate(R.id.lv_widget_list, getPublicIntent(context));
////                        views.setOnClickPendingIntent(R.id.tv_widget_title, getPublicIntent(context));
////                    }
//                } else {
//                    views.setPendingIntentTemplate(R.id.lv_widget_list, getPublicIntent(context));
//                    views.setOnClickPendingIntent(R.id.tv_widget_title, getPublicIntent(context));
//                }
//                AppWidgetManager manager = AppWidgetManager.getInstance(context);
//                manager.updateAppWidget(appWidgetId, views);
//            }
//
//            @Override
//            public void onConnectionError() {
//
//            }
//
//            @Override
//            public void onDefaultError(String title, String message, ErrorResponse errorResponse) {
//
//            }
//
//            @Override
//            public void sessionExpired() {
//
//            }
//        });
//        views.setViewVisibility(R.id.ll_widget_refresh, View.VISIBLE);
//        views.setOnClickPendingIntent(R.id.ll_widget_refresh, getRefreshIntent(context));
//
//        Intent intent = new Intent(context, BKWidgetRenderService.class);
//        intent.setData(Uri.fromParts("content", String.valueOf(appWidgetId), null));
//        views.setRemoteAdapter(R.id.lv_widget_list, intent);
//
//        return views;
//    }
//
//    private PendingIntent getPublicIntent(Context context) {
//        LogUtils.log(Tag.WIDGET, "----------- No session");
//        Intent intent = new Intent(context.getApplicationContext(), IntroActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        int flag;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            flag =  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
//        }else {
//            flag =  PendingIntent.FLAG_UPDATE_CURRENT;
//        }
//        return PendingIntent.getActivity(context.getApplicationContext(), 0,
//                intent, flag);
//    }
//
//    private PendingIntent getPrivateMainIntent(Context context) {
//        LogUtils.log(Tag.WIDGET, "----------- Has open session");
//        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        int flag;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            flag =  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
//        }else {
//            flag =  PendingIntent.FLAG_UPDATE_CURRENT;
//        }
//        return PendingIntent.getActivity(context.getApplicationContext(), 0,
//                intent, flag);
//    }
//
//    private boolean hasHeaders() {
//        return appData != null && appData.getHeaders() != null;
//    }
//
//    private boolean hasSession(Credentials credentials) {
//        return credentials != null && credentials.getUser() != null;
//    }
//
//    protected PendingIntent getRefreshIntent(Context context) {
//        Intent intent = new Intent(context, getClass());
//        intent.setAction(BKWidgetProvider.SYNC_CLICKED);
//        int flag;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            flag =  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
//        }else {
//            flag =  PendingIntent.FLAG_UPDATE_CURRENT;
//        }
//        return PendingIntent.getBroadcast(context, 0, intent, flag);
//    }
//
//}
//
