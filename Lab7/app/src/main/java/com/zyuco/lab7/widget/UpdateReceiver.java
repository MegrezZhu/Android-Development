package com.zyuco.lab7.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.zyuco.lab7.R;

public class UpdateReceiver extends BroadcastReceiver {
    final public static String ITEM_UPDATE_DYNAMIC = "com.zyuco.lab7.widget.ITEM_UPDATE_DYNAMIC";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("lab7log", "widget: update dynamic");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Bundle bundle = intent.getExtras();
        views.setTextViewText(R.id.widget_text, String.format("%1s已添加到购物车", bundle.get("name"), bundle.get("price")));
        views.setImageViewResource(R.id.widget_image, context.getResources().getIdentifier(bundle.get("sname").toString(), "mipmap", context.getPackageName()));

        Intent goMain = new Intent(context, Provider.class);
        goMain.setAction(Provider.GO_CART);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, goMain, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget, pi);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName me = new ComponentName(context, Provider.class);
        manager.updateAppWidget(me, views);
    }
}
