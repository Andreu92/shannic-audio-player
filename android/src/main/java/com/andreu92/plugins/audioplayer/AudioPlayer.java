package com.andreu92.plugins.audioplayer;

import com.getcapacitor.Logger;

public class AudioPlayer {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
