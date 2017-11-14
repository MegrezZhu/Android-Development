package com.zyuco.musicbox;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private enum State {
        INIT, PLAYING, PAUSED, STOPPED
    }

    private CDAnimator animator;
    private MediaPlayerService.Binder binder;
    private ServiceConnection conn;
    private State state = State.INIT;
    final static private String TAG = "lab8";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListeners();
        initAnimator();

        startPlayerService();
    }

    private void startPlayerService() {
        Intent intent = new Intent(this, MediaPlayerService.class);
        Handler progressHandler = new ProgressHandler(this);
        intent.putExtra("progressHandler", new Messenger(progressHandler));

        startService(intent);

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "service connected");
                binder = (MediaPlayerService.Binder) iBinder;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                conn = null;
            }
        };
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private void initAnimator() {
        ImageView cd = (ImageView) findViewById(R.id.cd);
        animator = new CDAnimator(cd);
    }

    private void setListeners() {
        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (binder == null) return;
                    binder.transact(MediaPlayerService.TOGGLE_PLAY, Parcel.obtain(), Parcel.obtain(), 0);
                    state = state == State.PLAYING ? State.PAUSED : State.PLAYING;
                    animator.togglePlay();
                    updateState();
                } catch (RemoteException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    binder.transact(MediaPlayerService.STOP, Parcel.obtain(), Parcel.obtain(), 0);
                    state = State.STOPPED;
                    animator.reset();
                    updateState();
                } catch (RemoteException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        findViewById(R.id.quit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService(conn);
                conn = null;
                try {
                    MainActivity.this.finish();
                    System.exit(0);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        ((SeekBar) findViewById(R.id.progress)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (binder != null) {
                    try {
                        Parcel data = Parcel.obtain();
                        data.writeInt(i);
                        binder.transact(MediaPlayerService.SEEK, data, Parcel.obtain(), 0);
                    } catch (RemoteException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (binder != null) {
                    try {
                        binder.transact(MediaPlayerService.PAUSE, Parcel.obtain(), Parcel.obtain(), 0);
                        state = State.PAUSED;
                        updateState();
                    } catch (RemoteException e) {
                        Log.e(TAG, "onStartTrackingTouch: ", e);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (binder != null) {
                    try {
                        binder.transact(MediaPlayerService.PLAY, Parcel.obtain(), Parcel.obtain(), 0);
                        state = State.PLAYING;
                        updateState();
                        // TODO what if the song finished?
                    } catch (RemoteException e) {
                        Log.e(TAG, "onStartTrackingTouch: ", e);
                    }
                }
            }
        });
    }

    private void updateState() {
        int id = R.string.state_init;
        int playId = R.string.button_play;
        switch (state) {
            case INIT:
                id = R.string.state_init;
                break;
            case PAUSED:
                id = R.string.state_paused;
                break;
            case PLAYING:
                id = R.string.state_playing;
                playId = R.string.button_pause;
                break;
            case STOPPED:
                id = R.string.state_stopped;
                break;
        }
        TextView stateView = (TextView) findViewById(R.id.state);
        stateView.setText(id);
        Button playButton = (Button) findViewById(R.id.play_button);
        playButton.setText(playId);
    }

    private class CDAnimator {
        private ObjectAnimator animator;

        CDAnimator(View view) {
            animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
            animator.setDuration(20000);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(null);
            animator.start();
            animator.pause();
        }

        void togglePlay() {
            if (animator.isPaused()) {
                animator.resume();
            } else {
                animator.pause();
            }
        }

        void reset() {
            animator.pause();
            animator.setCurrentFraction(0f);
        }
    }

    static private class ProgressHandler extends Handler {
        WeakReference<MainActivity> ref;

        ProgressHandler(MainActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = ref.get();
            if (activity != null) {
                // TODO
                super.handleMessage(msg);
            }
        }
    }
}
