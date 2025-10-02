package com.shannic.plugins.audioplayer;

import android.content.Intent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.getcapacitor.JSArray;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.ArrayList;

@CapacitorPlugin(name = "AudioPlayer")
public class AudioPlayer extends Plugin {
    private static final String ACTION_PLAY = "ACTION_PLAY";

    @PluginMethod()
    public void play(PluginCall call) throws JsonProcessingException {
        String id = call.getString("id");
        String title = call.getString("title");
        String artist = call.getString("artist");
        String author = call.getString("author");
        Long duration = null;
        Double durationDouble = call.getDouble("duration");
        if (durationDouble != null) duration = durationDouble.longValue();
        JSArray thumbnailJSArray = call.getArray("thumbnails");

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Thumbnail> thumbnails = mapper.readValue(
                thumbnailJSArray.toString(),
                new TypeReference<>() {}
        );

        String url = call.getString("url");

        if (url == null) {
            call.reject("Must provide an audio URL");
            return;
        }

        Intent intent = new Intent(getContext(), AudioPlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra("url", url);

        if (id != null) {
            intent.putExtra("id", id);
        }
        if (title != null) {
            intent.putExtra("title", title);
        }
        if (artist != null) {
            intent.putExtra("artist", artist);
        }
        if (author != null) {
            intent.putExtra("author", author);
        }
        if (duration != null) {
            intent.putExtra("duration", duration);
        }
        if (thumbnails != null && !thumbnails.isEmpty()) {
            intent.putExtra("thumbnails", thumbnails);
        }

        getContext().startForegroundService(intent);

        call.resolve();
    }

    @PluginMethod()
    public void stop(PluginCall call) {
        Intent intent = new Intent(getContext(), AudioPlayerService.class);
        getContext().stopService(intent);
        call.resolve();
    }

    @PluginMethod()
    public void pause(PluginCall call) {
        // Implementar pausa si es necesario
        call.resolve();
    }
}