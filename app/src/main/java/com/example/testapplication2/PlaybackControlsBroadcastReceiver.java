package com.example.testapplication2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

public class PlaybackControlsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if(String.valueOf(PlaybackStateCompat.ACTION_PAUSE).equals(intentAction)){
            MediaControllerCompat mediaControllerCompat = MediaPlaybackService.mediaSession.getController();
            if (mediaControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                mediaControllerCompat.getTransportControls().pause();
            }
        }else if(String.valueOf(PlaybackStateCompat.ACTION_PLAY).equals(intentAction)){
            MediaControllerCompat mediaControllerCompat = MediaPlaybackService.mediaSession.getController();
            mediaControllerCompat.getTransportControls().play();
        }else if(String.valueOf(PlaybackStateCompat.ACTION_STOP).equals(intentAction)){
            MediaControllerCompat mediaControllerCompat = MediaPlaybackService.mediaSession.getController();
            mediaControllerCompat.getTransportControls().stop();
        }else if(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS).equals(intentAction)){
            MediaControllerCompat mediaControllerCompat = MediaPlaybackService.mediaSession.getController();
            mediaControllerCompat.getTransportControls().skipToPrevious();
        }else if(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_NEXT).equals(intentAction)){
            MediaControllerCompat mediaControllerCompat = MediaPlaybackService.mediaSession.getController();
            mediaControllerCompat.getTransportControls().skipToNext();
        }else if(String.valueOf(PlaybackStateCompat.ACTION_REWIND).equals(intentAction)){
            MediaControllerCompat mediaControllerCompat = MediaPlaybackService.mediaSession.getController();
            mediaControllerCompat.sendCommand("rewind",null,null);
        }else if(String.valueOf(PlaybackStateCompat.ACTION_FAST_FORWARD).equals(intentAction)){
            MediaControllerCompat mediaControllerCompat = MediaPlaybackService.mediaSession.getController();
            mediaControllerCompat.sendCommand("fast_forward",null,null);
        }
    }
}
