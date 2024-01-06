package com.frange.coasters.ui.widget.bk.render;

//public class BKWidgetRenderFactory implements RemoteViewsService.RemoteViewsFactory {
//
//    private static final String TAG = "MY_WIDGET";
//    private ArrayList<Ride> dataList;
//    private Context context;
//
//    BKWidgetRenderFactory(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public void onCreate() {
//
//    }
//
//    @Override
//    public void onDataSetChanged() {
//        this.dataList = com.gneis.bankinter.broker.ui.widget.save.BKWidgetSaveModel.loadData(context);
//        LogUtils.log(TAG, "onDataSetChanged - Size: " + (dataList != null ? dataList.size() : "0"));
//    }
//
//    @Override
//    public void onDestroy() {
//
//    }
//
//    @Override
//    public int getCount() {
//        return dataList != null ? dataList.size() : 0;
//    }
//
//    @Override
//    public RemoteViews getViewAt(int position) {
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.row_widget);
//        if (dataList != null && !dataList.isEmpty() && dataList.get(position) != null) {
//            PublicMarket item = dataList.get(position);
//            if (item != null) {
//                renderHeader(item, views);
//                views.setTextViewText(R.id.tv_widget_name, item.getName());
//
//                try {
//                    String amount = NumberUtils.limitDecimalsFromDouble(
//                            item.getVariation(),
//                            item.getDecimals());
//
//                    String legend = item.getLegend();
//
//                    String variation = item.getType() == PublicMarketTypeEnum.INDEX_POSITION ?
//                            (legend != null && legend.toUpperCase().equals("R")) ?
//                                    amount :
//                                    amount + " " + legend :
//                            amount + " " +
//                                    ((item.getCurrency() != null) ?
//                                            item.getCurrency() :
//                                            "EUR") +
//                                    (legend != null && legend.toUpperCase().equals("R") ?
//                                            " " + legend :
//                                            "");
//                    String percentage = (
//                            context.getResources().getString(R.string.profitability_percentage_signed,
//                                    item.getPercentage() != 0 ?
//                                            item.getPercentage() > 0 ?
//                                                    "+" :
//                                                    "-" :
//                                            "",
//                                    NumberUtils.getPercentageFormat(
//                                            Math.abs(item.getPercentage()), 2, 2)));
//
//
//                    Constants.ArrowDrawableType arrowDrawableType =
//                            item.getPercentage() == 0.00 ?
//                                    Constants.ArrowDrawableType.NEUTRAL :
//                                    item.getPercentage() > 0 ?
//                                            Constants.ArrowDrawableType.UP :
//                                            Constants.ArrowDrawableType.DOWN;
//
//                    hideTextViews(views);
//
//                    if (legend != null) {
//                        switch (legend.toUpperCase()) {
//                            case "C": {
//                                switch (arrowDrawableType) {
//                                    case UP:
//                                        views.setViewVisibility(R.id.iv_widget_profit_up_down, View.VISIBLE);
//                                        views.setViewVisibility(R.id.iv_widget_profit_up_down, View.VISIBLE);
//                                        views.setViewVisibility(R.id.tv_widget_percentage_neutral, View.VISIBLE);
//                                        views.setTextViewText(R.id.tv_widget_percentage_neutral, percentage);
//                                        views.setImageViewResource(R.id.iv_widget_profit_up_down, R.drawable.ic_portfolio_quotation_up);
//                                        break;
//                                    case DOWN:
//                                        views.setViewVisibility(R.id.iv_widget_profit_up_down, View.VISIBLE);
//                                        views.setViewVisibility(R.id.tv_widget_percentage_neutral, View.VISIBLE);
//                                        views.setTextViewText(R.id.tv_widget_percentage_neutral, percentage);
//                                        views.setImageViewResource(R.id.iv_widget_profit_up_down, R.drawable.ic_portfolio_quotation_down);
//                                        break;
//                                    case NEUTRAL:
//                                        views.setViewVisibility(R.id.tv_widget_percentage_neutral, View.VISIBLE);
//                                        views.setTextViewText(R.id.tv_widget_percentage_neutral, percentage);
//                                        views.setViewVisibility(R.id.iv_widget_profit_up_down, View.INVISIBLE);
//                                        break;
//                                }
//                                break;
//                            }
//                            default:
//                                switch (arrowDrawableType) {
//                                    case UP:
//                                        views.setViewVisibility(R.id.iv_widget_profit_up_down, View.VISIBLE);
//                                        views.setViewVisibility(R.id.iv_widget_profit_up_down, View.VISIBLE);
//                                        views.setViewVisibility(R.id.tv_widget_percentage_up, View.VISIBLE);
//                                        views.setTextViewText(R.id.tv_widget_percentage_up, percentage);
//                                        views.setImageViewResource(R.id.iv_widget_profit_up_down,
//                                                R.drawable.ic_portfolio_quotation_up);
//                                        break;
//                                    case DOWN:
//                                        views.setViewVisibility(R.id.iv_widget_profit_up_down, View.VISIBLE);
//                                        views.setViewVisibility(R.id.tv_widget_percentage_down, View.VISIBLE);
//                                        views.setTextViewText(R.id.tv_widget_percentage_down, percentage);
//                                        views.setImageViewResource(R.id.iv_widget_profit_up_down,
//                                                R.drawable.ic_portfolio_quotation_down);
//                                        break;
//                                    case NEUTRAL:
//                                        views.setViewVisibility(R.id.tv_widget_percentage_no_bg, View.VISIBLE);
//                                        views.setTextViewText(R.id.tv_widget_percentage_no_bg, percentage);
//                                        views.setViewVisibility(R.id.iv_widget_profit_up_down, View.INVISIBLE);
//                                        break;
//                                }
//                        }
//                    }
//
//                    views.setTextViewText(R.id.tv_widget_amount, variation);
//
//                    Bundle extras = new Bundle();
//                    extras.putInt(com.gneis.bankinter.broker.ui.widget.WidgetProvider.EXTRA_ITEM, position);
//                    Intent fillInIntent = new Intent();
//                    fillInIntent.putExtras(extras);
//
//                    views.setOnClickFillInIntent(R.id.ll_widget_item, fillInIntent);
//                } catch (Exception e) {
//                    LogUtils.log(TAG, "Exception", e);
//                }
//            } else {
//                views.setViewVisibility(R.id.tv_widget_type, View.GONE);
//            }
//        } else {
//            views.setViewVisibility(R.id.tv_widget_type, View.GONE);
//        }
//
//        return views;
//    }
//
//    private void hideTextViews(RemoteViews views) {
//        if (views != null) {
//            views.setViewVisibility(R.id.tv_widget_percentage_up, View.GONE);
//            views.setViewVisibility(R.id.tv_widget_percentage_down, View.GONE);
//            views.setViewVisibility(R.id.tv_widget_percentage_neutral, View.GONE);
//            views.setViewVisibility(R.id.tv_widget_percentage_no_bg, View.GONE);
//        }
//    }
//
//    private void renderHeader(PublicMarket item, RemoteViews views) {
//        if (item != null && item.isFirstItem()) {
//            views.setViewVisibility(R.id.tv_widget_type, View.VISIBLE);
//
//            switch (item.getType()) {
//                default:
//                case PublicMarketTypeEnum.EQUITIES_POSITION:
//                    views.setTextViewText(R.id.tv_widget_type, "Acciones");
//                    break;
//                case PublicMarketTypeEnum.ETF_POSITION:
//                    views.setTextViewText(R.id.tv_widget_type, "ETF");
//                    break;
//                case PublicMarketTypeEnum.INDEX_POSITION:
//                    views.setTextViewText(R.id.tv_widget_type, "Índices");
//                    break;
//            }
//        } else {
//            views.setViewVisibility(R.id.tv_widget_type, View.GONE);
//        }
//    }
//
//    @Override
//    public RemoteViews getLoadingView() {
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_loading);
//        views.setTextViewText(R.id.tv_loading, "Cargando...");
//
//        return views;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 1;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//}