package com.zyuco.musicbox;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

public class MediaManager {
    private MediaPlayer player = new MediaPlayer();

    final private static String tag = "Lab8.MediaManager";

    {
        try {
            player.setDataSource(Environment.getExternalStorageDirectory() + "/melt.mp3");
            player.prepare();
            player.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(tag, e.getMessage());
        }
    }

    void togglePlay() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    void stop() {
        player.stop();
        try {
            player.prepare();
            player.seekTo(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
