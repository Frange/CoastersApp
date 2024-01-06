package com.frange.coasters.ui.widget.bk.request;

//public class BKWidgetService extends IntentService {
//
//    private static final String TAG = "MY_WIDGET_SERVICE";
//    private static final String PARAM_LIST = "public_market_index_list";
//
//    @Inject CoastersApp appData;
//    @Inject RequestCoasterUseCase requestPublicMarketListUseCase;
//
//    private ArrayList<Ride> list;
//    private BKWidgetProvider BKWidgetProvider;
//    private ApplicationComponent appComponent;
//    private Context context;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        LogUtils.log(TAG, "BKWidgetService - onCreate");
//        injectService();
//    }
//
//    public BKWidgetService() {
//        super("WidgetUpdateService");
//        LogUtils.log(TAG, "BKWidgetService - Constructor 1");
//    }
//    public BKWidgetService(BrokerApplication brokerApplication) {
//        super("WidgetUpdateService");
//        LogUtils.log(TAG, "BKWidgetService - Constructor 2");
//        injectService(brokerApplication);
//    }
//
//    //================================== DAGGER ===================================================
//
//    private void injectService() {
//        if (appComponent == null) {
//            ((BrokerApplication) getApplicationContext()).getServiceInjector(this).inject(this);
//            appComponent = ((BrokerApplication) getApplicationContext()).getApplicationComponent();
//        }
//    }
//
//    private void injectService(BrokerApplication brokerApplication) {
//        if (appComponent == null) {
//            brokerApplication.getServiceInjector(this).inject(this);
//            appComponent = brokerApplication.getApplicationComponent();
//        }
//    }
//
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        LogUtils.log(TAG, "onHandleIntent");
//        if (intent != null) {
//            final String action = intent.getAction();
//            try {
//                list = (ArrayList<PublicMarket>) intent.getSerializableExtra(PARAM_LIST);
//
//                LogUtils.log(TAG, "onHandleIntent - Action: " + action);
//                LogUtils.log(TAG, "onHandleIntent - List size: " +
//                        (list != null ?
//                                list.size() :
//                                "0"));
//                updateListView();
//            } catch (Exception e) {
//
//            }
//        }
//    }
//
//    private void updateListView() {
//        if (context != null && BKWidgetProvider != null) {
//            com.gneis.bankinter.broker.ui.widget.save.BKWidgetSaveModel.saveData(context, list);
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
//                    new ComponentName(context, com.gneis.bankinter.broker.ui.widget.WidgetProvider.class));
//            BKWidgetProvider.updateAllAppWidget(context, appWidgetManager, appWidgetIds);
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_list);
//        }
//    }
//
//    //====================== FROM WIDGET PROVIDER ===================================================
//
//    public void onUpdateList(Context context, boolean forListView, com.gneis.bankinter.broker.ui.widget.WidgetProvider provider) {
//        this.BKWidgetProvider = provider;
//        this.context = context;
//        LogUtils.log(TAG, "onUpdateList");
//        LogUtils.log(TAG, "appData is null ?: " + (appData == null));
//        if (appData != null) {
//            LogUtils.log(TAG, "appData.getShowingScreen() ?: " + appData.getShowingScreen());
//        }
//
//        if (appData == null || !appData.getShowingScreen()) {
//            requestPublicMarketListUseCase.execute(null,
//                    new RequestPublicMarketListUseCase.Callback() {
//                        @Override
//                        public void onSuccessRequestPublicMarketListUseCase(
//                                @Nullable ArrayList<PublicMarket> aList) {
//                            LogUtils.log(TAG, "onSuccessRequestPublicMarketListUseCase");
//                            list = aList;
//                            updateListView();
//                        }
//
//                        @Override
//                        public void onConnectionError() {
//                            LogUtils.log(TAG, "onConnectionError");
//                            list = new ArrayList<>();
//                            updateListView();
//                        }
//
//                        @Override
//                        public void onDefaultError(String title, String message,
//                                                   ErrorResponse errorResponse) {
//                            LogUtils.log(TAG, "onDefaultError");
//                            list = new ArrayList<>();
//                            updateListView();
//                        }
//
//                        @Override
//                        public void sessionExpired() {
//                            LogUtils.log(TAG, "sessionExpired");
//                            list = new ArrayList<>();
//                            updateListView();
//                        }
//                    });
//        }
//    }
//
//}