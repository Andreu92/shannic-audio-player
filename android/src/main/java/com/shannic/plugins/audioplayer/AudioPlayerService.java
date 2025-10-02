package com.shannic.plugins.audioplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.media.session.PlaybackStateCompat.CustomAction;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media.app.NotificationCompat.MediaStyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AudioPlayerService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String LOG_TAG = "ShannicAudioPlayerService";
    private static final String ACTION_PLAY = "ACTION_PLAY";
    private static final String ACTION_NOTIFICATION_DISMISSED = "ACTION_NOTIFICATION_DISMISSED";
    private static final String ACTION_TOGGLE_FAVORITE = "ACTION_TOGGLE_FAVORITE";
    private static final String ACTION_TOGGLE_REPEAT = "ACTION_TOGGLE_REPEAT";

    private ExoPlayer player;
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        player = new ExoPlayer.Builder(this).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                updatePlaybackState(isPlaying
                        ? PlaybackStateCompat.STATE_PLAYING
                        : PlaybackStateCompat.STATE_PAUSED);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING)
                    updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING);
                if (playbackState == Player.STATE_ENDED) {
                    //TO DO: ALERT JAVASCRIPT
                    player.pause();
                    player.seekTo(0);
                }
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                //if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
                //TO DO: ALERT JAVASCRIPT
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
            }
        });

        mediaSession = new MediaSessionCompat(this, LOG_TAG);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                player.play();
            }

            @Override
            public void onPause() {
                player.pause();
            }

            @Override
            public void onSeekTo(long pos) {
                player.seekTo(pos);
            }

            @Override
            public void onSkipToNext() {
                //TO DO
            }

            @Override
            public void onSkipToPrevious() {
                if (player.getCurrentPosition() > 0) {
                    player.seekTo(0);
                    player.play();
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                } else {

                }
            }

            @Override
            public void onStop() {
                stop();
            }

            @Override
            public void onCustomAction(@NonNull String action, Bundle extras) {
                switch (action) {
                    case ACTION_TOGGLE_FAVORITE:
                        break;
                    case ACTION_TOGGLE_REPEAT:
                        boolean repeating = player.getRepeatMode() == Player.REPEAT_MODE_ONE;
                        if (repeating) player.setRepeatMode(Player.REPEAT_MODE_OFF);
                        else player.setRepeatMode(Player.REPEAT_MODE_ONE);

                        updatePlaybackState(player.isPlaying()
                                ? PlaybackStateCompat.STATE_PLAYING
                                : PlaybackStateCompat.STATE_PAUSED);
                }
            }
        });
    }

    public void stop() {
        if (player != null) player.release();
        player = null;
        if (mediaSession != null) mediaSession.release();
        mediaSession = null;
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String id = intent.getStringExtra("id");
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        long duration = intent.getLongExtra("duration", 0);
        ArrayList<Thumbnail> thumbnails = intent.getParcelableArrayListExtra("thumbnails");

        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    play(id, url, title, artist, duration, thumbnails.get(thumbnails.size() - 1));
                    break;
                case ACTION_NOTIFICATION_DISMISSED:
                    stop();
            }
        }

        return START_STICKY;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private Notification buildNotification(Bitmap songArt) {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.setAction(ACTION_NOTIFICATION_DISMISSED);

        PendingIntent deletePendingIntent =
                PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.play_arrow)
                .setLargeIcon(songArt)
                .setStyle(new MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()))
                .setDeleteIntent(deletePendingIntent)
                .build();
    }

    private void updatePlaybackState(int state) {
        PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SET_REPEAT_MODE |
                                PlaybackStateCompat.ACTION_SEEK_TO
                );

        boolean repeating = player.getRepeatMode() == Player.REPEAT_MODE_ONE;
        boolean favorite = false;

        CustomAction repeatAction = new CustomAction.Builder(
                ACTION_TOGGLE_REPEAT,
                "Repeat",
                repeating
                        ? R.drawable.infinity
                        : R.drawable.loop
        ).build();

        CustomAction favoriteAction = new CustomAction.Builder(
                ACTION_TOGGLE_FAVORITE,
                "Repeat",
                favorite
                        ? R.drawable.favorite
                        : R.drawable.favorite_border
        ).build();

        playbackStateBuilder.addCustomAction(repeatAction);
        playbackStateBuilder.addCustomAction(favoriteAction);

        playbackStateBuilder.setState(state, player.getCurrentPosition(), 1f);

        mediaSession.setPlaybackState(playbackStateBuilder.build());
    }

    private void play(String id, String url, String title, String artist, long duration, Thumbnail thumbnail) {
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .build();
        mediaSession.setMetadata(metadata);

        MediaItem mediaItem =
                new MediaItem.Builder()
                        .setMediaId(id)
                        .setUri(url)
                        .setMediaMetadata(
                                new MediaMetadata.Builder()
                                        .setArtist(artist)
                                        .setTitle(title)
                                        .setArtworkUri(Uri.parse(thumbnail.getUrl()))
                                        .build())
                        .build();

        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        fetch(thumbnail.getUrl(), stream -> {
            Bitmap songArt = BitmapFactory.decodeStream(stream);
            Notification notification = buildNotification(songArt);
            startForeground(NOTIFICATION_ID, notification);
        });
    }

    private void pause() {
        player.pause();
    }

    private static void fetch(String src, Consumer<InputStream> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                callback.accept(input);
                connection.disconnect();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error fetching: " + e);
            }
        });
    }
}
