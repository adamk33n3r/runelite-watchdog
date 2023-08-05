package com.adamk33n3r.runelite.watchdog.elevenlabs;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Map;

@Data
public class Voice {
    @SerializedName("voice_id")
    private String voiceId;
    private String name;
    private String category;
    private String description;
    @SerializedName("preview_url")
    private String previewUrl;
    private Map<String, String> labels;
}
