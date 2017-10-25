package com.zyuco.lab6;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Map;
import java.util.Random;

public class DynamicReceiver extends BroadcastReceiver {
    public final static String ADD_SHOPLIST = "com.zyuco.lab6.ADD_SHOPLIST";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String name = bundle.get("name").toString();
        String channelId = "my_channel_zyuco";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // for Android Oreo.......
            NotificationChannel channel = new NotificationChannel(channelId, "channel_name", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        builder
            .setContentTitle("马上下单")
            .setContentText(String.format("%1s已添加到购物车", name))
            .setTicker("马上下单")
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.full_star)
            .setAutoCancel(true);

        Intent mIntent = new Intent(context, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
        builder.setContentIntent(mPendingIntent);

        Notification notify = builder.build();
        manager.notify(0, notify);
    }
}
