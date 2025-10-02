package com.example.plugin;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;
import com.shannic.plugins.audioplayer.AudioPlayer;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerPlugin(AudioPlayer.class);
    }
}