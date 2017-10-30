package com.zyuco.lab7.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.zyuco.lab7.ItemDetailActivity;
import com.zyuco.lab7.MainActivity;
import com.zyuco.lab7.R;

public class Provider extends AppWidgetProvider {
    final public static String GO_MAIN_ACTIVITY = "com.zyuco.lab7.widget.CLICK";
    final public static String GO_DETAIL = "com.zyuco.lab7.widget.GO_DETAIL";
    final public static String GO_CART = "com.zyuco.lab7.widget.GO_CART";
    final public static String ITEM_UPDATE_STATIC = "com.zyuco.lab7.widget.ITEM_UPDATE_STATIC";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("lab7log", "onUpdate");
        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, Provider.class); // must-do in API 26
        intent.setAction(GO_MAIN_ACTIVITY);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateViews.setOnClickPendingIntent(R.id.widget, pi);
        ComponentName me = new ComponentName(context, Provider.class);
        appWidgetManager.updateAppWidget(me, updateViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(GO_MAIN_ACTIVITY)) {
            Log.i("lab7log", "widget: click");
            Intent intent1 = new Intent(context, MainActivity.class);
            context.startActivity(intent1);
        }
        if (intent.getAction().equals(GO_DETAIL)) {
            Log.i("lab7log", "widget: go detail");
            Intent detail = new Intent(context, ItemDetailActivity.class);
            detail.putExtras(intent.getExtras());
            context.startActivity(detail);
        }
        if (intent.getAction().equals(GO_CART)) {
            Log.i("lab7log", "widget: go cart");
            Intent goMain = new Intent(context, MainActivity.class);
            goMain.putExtra("goCart", true);
            context.startActivity(goMain);
        }
        if (intent.getAction().equals(ITEM_UPDATE_STATIC)) {
            Log.i("lab7log", "widget: update static");

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            Bundle bundle = intent.getExtras();
            views.setTextViewText(R.id.widget_text, String.format("%1s仅售%1s!", bundle.get("name"), bundle.get("price")));
            views.setImageViewResource(R.id.widget_image, context.getResources().getIdentifier(bundle.get("sname").toString(), "mipmap", context.getPackageName()));

            Intent detail = new Intent(context, Provider.class);
            detail.setAction(Provider.GO_DETAIL);
            detail.putExtras(intent.getExtras());
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, detail, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget, pi);

            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName me = new ComponentName(context, Provider.class);
            manager.updateAppWidget(me, views);
        }
    }
}

