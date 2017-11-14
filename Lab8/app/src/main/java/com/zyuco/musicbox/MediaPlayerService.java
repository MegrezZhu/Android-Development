package com.zyuco.musicbox;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class MediaPlayerService extends Service {
    final static int PLAY = 100;
    final static int PAUSE = 101;
    final static int TOGGLE_PLAY = 102;
    final static int STOP = 103;
    final static int SEEK = 104;
    final static String TAG = "lab8.MediaPlayerService";

    private Binder binder = new Binder();
    private MediaPlayer player = new MediaPlayer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class Binder extends android.os.Binder {

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case PLAY:
                    Log.i(TAG, "onTransact: play");
                    if (!player.isPlaying()) player.start();
                    break;
                case PAUSE:
                    Log.i(TAG, "onTransact: pause");
                    if (player.isPlaying()) player.pause();
                    break;
                case TOGGLE_PLAY:
                    Log.i(TAG, "onTransact: toggle play");
                    player.togglePlay();
                    break;
                case STOP:
                    Log.i(TAG, "onTransact: stop");
                    player.stop();
                    break;
                case SEEK:
                    double pos = data.readInt();
                    player.seekTo((int)Math.floor(player.getDuration() * pos / 100));
            }
            return super.onTransact(code, data, reply, flags);
        }
    }
}

class MediaPlayer extends android.media.MediaPlayer {
    MediaPlayer() {
        try {
            setDataSource(Environment.getExternalStorageDirectory() + "/melt.mp3");
            prepare();
            start();
            pause();
        } catch (IOException e) {
            Log.e("lab8.MediaPlayerService", "MediaPlayer: ", e);
        }
    }

    void togglePlay() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    @Override
    public void stop() {
        super.stop();
        try {
            prepare();
            seekTo(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
