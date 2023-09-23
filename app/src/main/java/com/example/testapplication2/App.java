package com.example.testapplication2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application {

    public static final String MUSIC_CHANNEL_ID = "musicChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {

        NotificationChannel musicNotificationChannel = new NotificationChannel(
                MUSIC_CHANNEL_ID,
                "Music Notification Channel",
                NotificationManager.IMPORTANCE_LOW
        );
        musicNotificationChannel.setDescription("Notification Channel for the MediaPlaybackService That Plays the Music");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(musicNotificationChannel);
    }

}
