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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class StaticReceiver extends BroadcastReceiver {
    public final static String SHOW_RECOMMEND = "com.zyuco.lab6.SHOW_RECOMMEND";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("123", "static receiver");
        Bundle bundle = intent.getExtras();
        String name = bundle.get("name").toString();
        String price = bundle.get("price").toString();
        String channelId = "my_channel_zyuco";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // for Android Oreo.......
            NotificationChannel channel = new NotificationChannel(channelId, "channel_name", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        builder
            .setContentTitle("新商品热卖")
            .setContentText(String.format("%1s仅售%1s!", name, price))
            .setTicker("新商品热卖")
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.full_star)
            .setAutoCancel(true);

        Intent mIntent = new Intent(context, ItemDetailActivity.class);
        mIntent.putExtras(bundle);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(mPendingIntent);

        Notification notify = builder.build();
        manager.notify(0, notify);
    }
}
